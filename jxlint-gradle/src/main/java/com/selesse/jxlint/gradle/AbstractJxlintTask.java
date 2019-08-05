package com.selesse.jxlint.gradle;

import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.NonExistentCategoryException;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.Categories;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.settings.Profiler;
import com.selesse.jxlint.settings.ProgramSettings;

import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractJxlintTask extends DefaultTask {

    public static final String DISABLE_RULES = "disableRules";
    public static final String ENABLE_ONLY_RULES = "enableOnlyRules";
    public static final String ENABLE_RULES = "enableRules";
    public static final String ENABLE_CATEGORY = "enableCategories";
    public static final String SOURCE_DIRECTORY = "sourceDirectory";
    public static final String NO_WARNINGS = "noWarnings";
    public static final String ALL_WARNINGS = "allWarnings";
    public static final String WARNINGS_ARE_ERRORS = "warningsAreErrors";
    public static final String OUTPUT_TYPE = "outputType";
    public static final String OUTPUT_FILE = "outputFile";
    public static final String SRC_PATH_PREFIX = "srcPathPrefix";

    protected List<String> disableRules;

    protected List<String> enableOnlyRules;

    protected List<String> enableRules;

    protected List<String> enableCategories;

    protected String sourceDirectory;

    protected boolean noWarnings = false;

    protected boolean allWarnings = false;

    protected boolean warningsAreErrors = false;

    protected String outputType = "html";

    protected String outputFile;

    protected String srcPathPrefix;

    @Option(option = DISABLE_RULES, description = "Disable the list of rules.")
    public void setDisableRules(List<String> disableRules) {
        this.disableRules = disableRules;
    }

    @Option(option = ENABLE_ONLY_RULES, description = "Only check for these rules.")
    public void setEnableOnlyRules(List<String> enableOnlyRules) {
        this.enableOnlyRules = enableOnlyRules;
    }

    @Option(option = ENABLE_RULES, description = "Enable the list of rules.")
    public void setEnableRules(List<String> enableRules) {
        this.enableRules = enableRules;
    }

    @Option(option = ENABLE_CATEGORY, description = "Run all rules of a certain category.")
    public void setEnableCategories(List<String> enableCategories) {
        this.enableCategories = enableCategories;
    }

    @Option(option = SOURCE_DIRECTORY, description = "Directory where the sources to analyze are located")
    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    @Option(option = NO_WARNINGS, description = "Only check for errors; ignore warnings.")
    public void setNoWarnings(boolean noWarnings) {
        this.noWarnings = noWarnings;
    }

    @Option(option = ALL_WARNINGS, description = "Check all warnings, including those off by default.")
    public void setAllWarnings(boolean allWarnings) {
        this.allWarnings = allWarnings;
    }

    @Option(option = WARNINGS_ARE_ERRORS, description = "Treat all warnings as errors.")
    public void setWarningsAreErrors(boolean warningsAreErrors) {
        this.warningsAreErrors = warningsAreErrors;
    }

    @Option(option = OUTPUT_TYPE, description = "Type of report that should be created (xml, jenkins-xml, html)")
    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    @Option(option = OUTPUT_FILE, description = "Name of the file, where the report will be created.")
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    @Option(option = SRC_PATH_PREFIX, description = "Local or remote path to the source directory,"
            + " if not set a relative path to the local file will be computed.")
    public void setSrcPathPrefix(String srcPathPrefix) {
        this.srcPathPrefix = srcPathPrefix;
    }

    protected abstract ProgramSettings provideProgramSettings();

    protected abstract LintRules provideLintRules();

    protected Class<? extends Enum<?>> provideCategories() {
        return Category.class;
    }

    protected ProgramOptions createProgramOptions() {
        ProgramOptions options = new ProgramOptions();

        String sourceDirectoryValue;
        if (sourceDirectory == null) {
            sourceDirectoryValue = getProject().getProjectDir().getAbsolutePath();
        }
        else {
            sourceDirectoryValue = getProject().file(sourceDirectory).getAbsolutePath();
        }
        getLogger().debug("set source directory option to '" + sourceDirectoryValue + "'");
        options.setSourceDirectory(sourceDirectoryValue);

        addOption(options, JxlintOption.OUTPUT_TYPE, outputType);
        String outputFileValue;
        if (outputFile == null) {
            String extension;
            if ("xml".equals(outputType) || "jenkins-xml".equals(outputType)) {
                extension = ".xml";
            }
            else {
                extension = ".html";
            }
            outputFileValue = new File(getProject().getBuildDir(), "report" + extension).getAbsolutePath();
        }
        else {
            outputFileValue = getProject().file(outputFile).getAbsolutePath();
        }
        addOption(options, JxlintOption.OUTPUT_TYPE_PATH, outputFileValue);
        addOption(options, JxlintOption.SRC_PATH_PREFIX, srcPathPrefix);

        addRulesOption(l -> options.setCheckRules(l), JxlintOption.CHECK, enableOnlyRules);
        addRulesOption(l -> options.setEnabledRules(l), JxlintOption.ENABLE, enableRules);
        addRulesOption(l -> options.setDisabledRules(l), JxlintOption.DISABLE, disableRules);

        addCategoryOption(options, enableCategories);

        addBooleanOption(options, JxlintOption.NO_WARNINGS, noWarnings);
        addBooleanOption(options, JxlintOption.ALL_WARNINGS, allWarnings);
        addBooleanOption(options, JxlintOption.WARNINGS_ARE_ERRORS, warningsAreErrors);

        return options;
    }

    private void addOption(ProgramOptions options, JxlintOption option, String value) {
        if (value != null) {
            getLogger().debug("set option '" + option + "' to '" + value + "'");
            options.addOption(option, value);
        }
        else {
            getLogger().debug("option '" + option + "' is not set (is null)");
        }
    }

    private void addRulesOption(Consumer<List<LintRule>> setter, JxlintOption option, List<String> list) {
        if (list != null && !list.isEmpty()) {
            try {
                List<LintRule> ruleList = ProgramOptions.getRuleListFromRuleNameList(list);
                setter.accept(ruleList);
            }
            catch (NonExistentLintRuleException e) {
                throw new InvalidUserDataException(e.getMessage());
            }
        }
        else {
            getLogger().debug("option '" + option + "' is not set (null or empty list)");
        }
    }

    private void addCategoryOption(ProgramOptions options, List<String> rawCategoryStringList) {
        if (rawCategoryStringList != null && !rawCategoryStringList.isEmpty()) {
            try {
                List<Enum<?>> categories = ProgramOptions.getCategoryListFromCategoryNameList(rawCategoryStringList);
                options.setEnabledCategories(categories);
            }
            catch (NonExistentCategoryException e) {
                throw new InvalidUserDataException(e.getMessage());
            }
        }
        else {
            getLogger().debug("option '" + JxlintOption.CATEGORY + "' is not set (null or empty list)");
        }
    }

    private void addBooleanOption(ProgramOptions options, JxlintOption option, boolean value) {
        if (value) {
            getLogger().debug("add option '" + option + "' with default value");
            options.addOption(option);
        }
        else {
            getLogger().debug("option '" + option + "' is not added");
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

    @TaskAction
    public void runJxlint() {
        initJxlint();

        ProgramSettings programSettings = provideProgramSettings();
        getLogger().info("running '" + programSettings.getProgramName() + "' version '"
                + programSettings.getProgramVersion() + "'");

        ProgramOptions programOptions = createProgramOptions();

        GradleDispatcher dispatcher = new GradleDispatcher(programOptions, programSettings);
        dispatcher.dispatch();
    }
}
