package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.selesse.jxlint.actions.LintHandler;
import com.selesse.jxlint.actions.LintRuleInformationDisplayer;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.settings.Profiler;
import com.selesse.jxlint.settings.ProgramSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Dispatcher guides the application's logic flow. It looks at the options provided in
 * {@link com.selesse.jxlint.model.ProgramOptions} and decides what objects, functions, etc. to call based on the
 * options. Its logical route is documented in {@link AbstractDispatcher#doDispatch()}.
 */
public abstract class AbstractDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDispatcher.class);
    protected final ProgramOptions programOptions;
    protected final ProgramSettings programSettings;

    public AbstractDispatcher(ProgramOptions programOptions, ProgramSettings programSettings) {
        this.programOptions = programOptions;
        this.programSettings = programSettings;
    }

    /**
     * The order for the dispatcher is as such:
     * <ol>
     * <li>First, look for the mutually exclusive options ("help", "version", "list", "show"). These are first-come,
     * first-serve. If enabled, branch out to those options.</li>
     * <li>Second, check to see if warnings are errors and keep note of it.</li>
     * <li>Thirdly, check to see if the source directory exists. Exit if it doesn't.</li>
     * <li>Fourthly, check to see if "check" was called. Branch out if it is.</li>
     * <li>Then, by default, we only do enabled rules. We check to see if "Wall" or "nowarn" are set. If they are,
     * adjust accordingly.</li>
     * <li>Finally, we check to see if "enable" or "disable" are set and modify our list of rules accordingly.</li>
     * <li>Dispatch to {@link com.selesse.jxlint.actions.LintHandler}.</li>
     * </ol>
     */
    protected void doDispatch() throws ExitException {
        Profiler.setEnabled(programOptions.hasOption(JxlintOption.PROFILE));

        LintRules lintRules = LintRulesImpl.getInstance();

        // If/else train of mutually exclusive options
        if (programOptions.hasOption(JxlintOption.HELP)) {
            doHelp(programSettings);
        }
        else if (programOptions.hasOption(JxlintOption.VERSION)) {
            doVersion(programSettings);
        }
        else if (programOptions.hasOption(JxlintOption.LIST)) {
            LintRuleInformationDisplayer.listRules();
        }
        else if (programOptions.hasOption(JxlintOption.WEB)) {
            startWebServer();
            return;
        }
        else if (programOptions.hasOption(JxlintOption.SHOW)) {
            LintRuleInformationDisplayer.showRules(programOptions);
        }
        else if (programOptions.hasOption(JxlintOption.REPORT_RULES)) {
            LintRuleInformationDisplayer.printMarkdownRuleReport(programSettings);
        }

        boolean warningsAreErrors = false;
        if (programOptions.hasOption(JxlintOption.WARNINGS_ARE_ERRORS)) {
            warningsAreErrors = true;
        }

        // we've parsed all mutually exclusive options, now let's make sure we have a proper source directory
        if (programOptions.getSourceDirectory() != null) {
            String sourceDirectoryString = programOptions.getSourceDirectory();
            if (isInvalidSourceDirectory(sourceDirectoryString)) {
                File sourceDirectory = new File(sourceDirectoryString);
                String outputBuffer = "Invalid source directory \"" + sourceDirectoryString + "\" : ";
                if (!sourceDirectory.isDirectory()) {
                    outputBuffer += "\"" + sourceDirectoryString + "\" is not an existing directory.";
                }
                else if (!sourceDirectory.canRead()) {
                    outputBuffer += "Cannot read directory.";
                }

                throw new ExitException(outputBuffer, ExitType.COMMAND_LINE_ERROR);
            }

            lintRules.setSourceDirectory(new File(sourceDirectoryString));
        }
        else {
            throw new ExitException("Error: could not find directory to validate.",
                    ExitType.COMMAND_LINE_ERROR);
        }

        if (programOptions.hasOption(JxlintOption.CHECK)) {
            String checkRules = programOptions.getOption(JxlintOption.CHECK);
            List<String> checkRulesList = null;
            try {
                checkRulesList = ProgramOptions.getRuleListFromOptionString(checkRules);
            }
            catch (NonExistentLintRuleException e) {
                LOGGER.warn("Could not find rule [{}] in: {}", e.getRuleName(), checkRules);
            }

            handleLint(lintRules.getOnlyRules(checkRulesList), warningsAreErrors, programOptions, programSettings);
            return;
        }

        // By default, we only check enabled rules.
        // We adjust this according to the options below.
        Set<LintRule> lintRulesSet;

        if (programOptions.hasOption(JxlintOption.ALL_WARNINGS)) {
            lintRulesSet = Sets.newHashSet(lintRules.getAllRules());
        }
        else if (programOptions.hasOption(JxlintOption.NO_WARNINGS)) {
            lintRulesSet = Sets.newHashSet(lintRules.getAllRulesWithSeverity(Severity.ERROR));
            lintRulesSet.addAll(lintRules.getAllRulesWithSeverity(Severity.FATAL));
        }
        else {
            lintRulesSet = Sets.newHashSet(lintRules.getAllEnabledRules());
        }

        if (programOptions.hasOption(JxlintOption.CATEGORY)) {
            String enabledCategories = programOptions.getOption(JxlintOption.CATEGORY);
            try {
                final List<String> enabledCategoriesList =
                        ProgramOptions.getCategoryListFromOptionString(enabledCategories);
                lintRulesSet =
                        lintRulesSet.stream()
                                .filter(input -> enabledCategoriesList.contains(input.getCategory().toString()))
                                .collect(Collectors.toSet());
            }
            catch (IllegalArgumentException e) {
                throw new ExitException(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
            }
        }

        // Options that are "standalone"
        if (programOptions.hasOption(JxlintOption.DISABLE)) {
            String disabledRules = programOptions.getOption(JxlintOption.DISABLE);
            try {
                List<String> disabledRulesList = ProgramOptions.getRuleListFromOptionString(disabledRules);
                lintRulesSet.removeAll(lintRules.getOnlyRules(disabledRulesList));
            }
            catch (NonExistentLintRuleException e) {
                throw new ExitException(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
            }
        }
        if (programOptions.hasOption(JxlintOption.ENABLE)) {
            String enabledRules = programOptions.getOption(JxlintOption.ENABLE);
            try {
                List<String> enabledRulesList = ProgramOptions.getRuleListFromOptionString(enabledRules);
                lintRulesSet.addAll(lintRules.getOnlyRules(enabledRulesList));
            }
            catch (NonExistentLintRuleException e) {
                throw new ExitException(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
            }
        }
        handleLint(Lists.newArrayList(lintRulesSet), warningsAreErrors, programOptions, programSettings);
    }

    protected abstract void startWebServer();

    private void handleLint(List<LintRule> lintRules, boolean warningsAreErrors, ProgramOptions options,
            ProgramSettings settings) throws ExitException {
        LintHandler lintHandler = new LintHandler(lintRules, warningsAreErrors, options, settings);
        lintHandler.lintAndReportAndExit(LintRulesImpl.willExitAfterReporting());
    }

    private void doHelp(ProgramSettings settings) throws ExitException {
        throw new ExitException(createHelpMessage(settings), ExitType.SUCCESS);
    }

    protected abstract String createHelpMessage(ProgramSettings settings);

    private void doVersion(ProgramSettings settings) throws ExitException {
        throw new ExitException(settings.getProgramName() + ": version " +
                settings.getProgramVersion(), ExitType.SUCCESS);
    }

    private boolean isInvalidSourceDirectory(String sourceDirectoryString) {
        File sourceDirectory = new File(sourceDirectoryString);

        return !sourceDirectory.exists() || !sourceDirectory.isDirectory() || !sourceDirectory.canRead();
    }
}
