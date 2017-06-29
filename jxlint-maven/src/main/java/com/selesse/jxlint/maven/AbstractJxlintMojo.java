package com.selesse.jxlint.maven;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.MojoDispatcher;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.Categories;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.settings.Profiler;
import com.selesse.jxlint.settings.ProgramSettings;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

public abstract class AbstractJxlintMojo extends AbstractMojo {

    private static final String DISABLE_RULES = "disableRules";
    private static final String ENABLE_ONLY_RULES = "enableOnlyRules";
    private static final String ENABLE_RULES = "enableRules";
    private static final String ENABLE_CATEGORY = "enableCategories";
    private static final String SOURCE_DIRECTORY = "sourceDirectory";
    private static final String NO_WARNINGS = "noWarnings";
    private static final String ALL_WARNINGS = "allWarnings";
    private static final String WARNINGS_ARE_ERRORS = "warningAsErrors";
    private static final String OUTPUT_TYPE = "outputType";
    private static final String OUTPUT_FILE = "outputFile";
    private static final String SRC_PATH_PREFIX = "srcPath";

    /**
     * Disable the list of rules.
     */
    @Parameter(property = DISABLE_RULES)
    protected List<String> disableRules;

    /**
     * Only check for these rules.
     */
    @Parameter(property = ENABLE_ONLY_RULES)
    protected List<String> enableOnlyRules;

    /**
     * Enable the list of rules.
     */
    @Parameter(property = ENABLE_RULES)
    protected List<String> enableRules;

    /**
     * Run all rules of a certain category.
     */
    @Parameter(property = ENABLE_CATEGORY)
    protected List<String> enableCategories;

    @Parameter(property = SOURCE_DIRECTORY, defaultValue = "${project.basedir}")
    protected File sourceDirectory;

    /**
     * Only check for errors; ignore warnings.
     */
    @Parameter(property = NO_WARNINGS, defaultValue = "false")
    protected boolean noWarnings = false;

    /**
     * Check all warnings, including those off by default.
     */
    @Parameter(property = ALL_WARNINGS, defaultValue = "false")
    protected boolean allWarnings = false;

    /**
     * Treat all warnings as errors.
     */
    @Parameter(property = WARNINGS_ARE_ERRORS, defaultValue = "false")
    protected boolean waringsAreErrors = false;

    /**
     * Type of report that should be created (xml, html)
     */
    @Parameter(property = OUTPUT_TYPE, defaultValue = "html")
    protected String outputType;

    /**
     * Name of the file, where the report will be created.
     */
    @Parameter(property = OUTPUT_FILE, defaultValue = "${project.build.directory}/report.html")
    protected File outputFile;

    /**
     * Local or remote path to the source directory, if not set a relative path to the local file will be computed.
     */
    @Parameter(property = SRC_PATH_PREFIX)
    protected String srcPathPrefix;

    protected abstract ProgramSettings provideProgramSettings();

    protected abstract LintRules provideLintRules();

    protected abstract Class<? extends Enum<?>> provideCategories();

    protected ProgramOptions createProgramOptions() throws MojoExecutionException {
        ProgramOptions options = new ProgramOptions();

        getLog().debug("set source directory option to '" + sourceDirectory.getAbsolutePath() + "'");
        options.setSourceDirectory(sourceDirectory.getAbsolutePath());

        addOption(options, JxlintOption.OUTPUT_TYPE, outputType);
        addOption(options, JxlintOption.OUTPUT_TYPE_PATH, outputFile.getAbsolutePath());
        addOption(options, JxlintOption.SRC_PATH_PREFIX, srcPathPrefix);

        addRulesOption(options, JxlintOption.CHECK, enableOnlyRules);
        addRulesOption(options, JxlintOption.ENABLE, enableRules);
        addRulesOption(options, JxlintOption.DISABLE, disableRules);

        addCategoryOption(options, enableCategories);

        addBooleanOption(options, JxlintOption.NO_WARNINGS, noWarnings);
        addBooleanOption(options, JxlintOption.ALL_WARNINGS, allWarnings);
        addBooleanOption(options, JxlintOption.WARNINGS_ARE_ERRORS, waringsAreErrors);

        return options;
    }

    private void addOption(ProgramOptions options, JxlintOption option, String value) {
        if (value != null) {
            getLog().debug("set option '" + option + "' to '" + value + "'");
            options.addOption(option, value);
        }
        else {
            getLog().debug("option '" + option + "' is not set (is null)");
        }
    }

    private void addRulesOption(ProgramOptions options, JxlintOption option, List<String> list)
            throws MojoExecutionException {
        if (list != null && !list.isEmpty()) {
            for (String rule : list) {
                try {
                    LintRulesImpl.getInstance().getLintRule(rule);
                }
                catch (NonExistentLintRuleException e) {
                    throw new MojoExecutionException(e.getMessage());
                }
            }
            String value = Joiner.on(',').join(list);
            addOption(options, option, value);
        }
        else {
            getLog().debug("option '" + option + "' is not set (null or empty list)");
        }
    }

    private void addCategoryOption(ProgramOptions options, List<String> rawCategoryStringList)
            throws MojoExecutionException {
        if (rawCategoryStringList != null && !rawCategoryStringList.isEmpty()) {
            Enum<?>[] categories = Categories.get().getEnumConstants();
            List<String> categoryNames = Lists.newArrayList();
            for (Enum<?> category : categories) {
                categoryNames.add(category.toString());
            }

            for (String categoryString : rawCategoryStringList) {
                if (!categoryNames.contains(categoryString)) {
                    throw new MojoExecutionException(
                            "Category \"" + categoryString + "\" does not exist. Try one of: " +
                                    Joiner.on(", ").join(categories) + ".");
                }
            }
            String value = Joiner.on(',').join(rawCategoryStringList);
            addOption(options, JxlintOption.CATEGORY, value);
        }
        else {
            getLog().debug("option '" + JxlintOption.CATEGORY + "' is not set (null or empty list)");
        }
    }

    private void addBooleanOption(ProgramOptions options, JxlintOption option, boolean value) {
        if (value) {
            getLog().debug("add option '" + option + "' with default value");
            options.addOption(option);
        }
        else {
            getLog().debug("option '" + option + "' is not added");
        }
    }

    protected void initJxlint() {
        LintRules lintRules = provideLintRules();
        Class<? extends Enum<?>> categories = provideCategories();

        Profiler.setEnabled(false);
        Categories.setCategories(categories);

        LintRulesImpl.setInstance(lintRules);
        LintRulesImpl.setExitAfterReporting(false);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        initJxlint();

        ProgramSettings programSettings = provideProgramSettings();
        getLog().info("running '" + programSettings.getProgramName() + "' version '"
                + programSettings.getProgramVersion() + "'");

        ProgramOptions programOptions = createProgramOptions();

        MojoDispatcher dispatcher = new MojoDispatcher(programOptions, programSettings);
        dispatcher.dispatch();
    }
}
