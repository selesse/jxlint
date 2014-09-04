package com.selesse.jxlint;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.selesse.jxlint.actions.JettyWebRunner;
import com.selesse.jxlint.actions.LintHandler;
import com.selesse.jxlint.actions.LintRuleInformationDisplayer;
import com.selesse.jxlint.cli.CommandLineOptions;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.*;
import com.selesse.jxlint.settings.Profiler;
import com.selesse.jxlint.settings.ProgramSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

/**
 * The Dispatcher guides the application's logic flow. It looks at the options provided in
 * {@link com.selesse.jxlint.model.ProgramOptions} and decides what objects, functions, etc. to call based on the
 * options. Its logical route is documented in
 * {@link #dispatch(com.selesse.jxlint.model.ProgramOptions, com.selesse.jxlint.settings.ProgramSettings)}.
 */
public class Dispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

    /**
     * The order for the dispatcher is as such:
     *
     * <ol>
     * <li> First, look for the mutually exclusive options ("help", "version", "list", "show").
     * These are first-come, first-serve. If enabled, branch out to those options. </li>
     *
     * <li> Second, check to see if warnings are errors and keep note of it. </li>
     *
     * <li> Thirdly, check to see if the source directory exists. Exit if it doesn't. </li>
     *
     * <li> Fourthly, check to see if "check" was called. Branch out if it is. </li>
     *
     * <li> Then, by default, we only do enabled rules. We check to see if "Wall" or "nowarn" are set.
     * If they are, adjust accordingly. </li>
     *
     * <li> Finally, we check to see if "enable" or "disable" are set and modify our list of rules accordingly. </li>
     *
     * <li> Dispatch to {@link com.selesse.jxlint.actions.LintHandler}. </li>
     *
     * </ol>
     */
    public static void dispatch(ProgramOptions programOptions, ProgramSettings programSettings) {
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
            String port = programOptions.getOption(JxlintOption.WEB);

            JettyWebRunner jettyWebRunner = new JettyWebRunner(programSettings, port);
            jettyWebRunner.start();
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
                if (!sourceDirectory.exists()) {
                    outputBuffer += "Directory does not exist.";
                }
                else if (!sourceDirectory.isDirectory()) {
                    outputBuffer += "\"" + sourceDirectoryString + "\" is not a directory.";
                }
                else if (!sourceDirectory.canRead()) {
                    outputBuffer += "Cannot read directory.";
                }

                ProgramExitter.exitProgramWithMessage(outputBuffer, ExitType.COMMAND_LINE_ERROR);
            }

            lintRules.setSourceDirectory(new File(sourceDirectoryString));
        }
        else {
            ProgramExitter.exitProgramWithMessage("Error: could not find directory to validate.",
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
        List<LintRule> lintRuleList = Lists.newArrayList(lintRules.getAllEnabledRules());

        if (programOptions.hasOption(JxlintOption.ALL_WARNINGS)) {
            lintRuleList = Lists.newArrayList(lintRules.getAllRules());
        }
        else if (programOptions.hasOption(JxlintOption.NO_WARNINGS)) {
            lintRuleList = Lists.newArrayList(lintRules.getAllRulesWithSeverity(Severity.ERROR));
            lintRuleList.addAll(lintRules.getAllRulesWithSeverity(Severity.FATAL));
        }

        if (programOptions.hasOption(JxlintOption.CATEGORY)) {
            String enabledCategories = programOptions.getOption(JxlintOption.CATEGORY);
            try {
                final List<String> enabledCategoriesList =
                        ProgramOptions.getCategoryListFromOptionString(enabledCategories);
                lintRuleList = Lists.newArrayList(Iterables.filter(lintRuleList, new Predicate<LintRule>() {
                    @Override
                    public boolean apply(@Nullable LintRule input) {
                        return input != null && enabledCategoriesList.contains(input.getCategory().toString());

                    }
                }));
            }
            catch (IllegalArgumentException e) {
                ProgramExitter.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
            }
        }

        // Options that are "standalone"
        if (programOptions.hasOption(JxlintOption.DISABLE)) {
            String disabledRules = programOptions.getOption(JxlintOption.DISABLE);
            try {
                List<String> disabledRulesList = ProgramOptions.getRuleListFromOptionString(disabledRules);
                lintRuleList.removeAll(lintRules.getOnlyRules(disabledRulesList));
            }
            catch (NonExistentLintRuleException e) {
                ProgramExitter.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
            }
        }
        if (programOptions.hasOption(JxlintOption.ENABLE)) {
            String enabledRules = programOptions.getOption(JxlintOption.ENABLE);
            try {
                List<String> enabledRulesList = ProgramOptions.getRuleListFromOptionString(enabledRules);
                lintRuleList.addAll(lintRules.getOnlyRules(enabledRulesList));
            }
            catch (NonExistentLintRuleException e) {
                ProgramExitter.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
            }
        }
        handleLint(lintRuleList, warningsAreErrors, programOptions, programSettings);
    }

    private static void handleLint(List<LintRule> lintRules, boolean warningsAreErrors, ProgramOptions options,
                                   ProgramSettings settings) {
        LintHandler lintHandler = new LintHandler(lintRules, warningsAreErrors, options, settings);
        lintHandler.lintAndReportAndExit(LintRulesImpl.willExitAfterReporting());
    }

    private static void doHelp(ProgramSettings programSettings) {
        ProgramExitter.exitProgramWithMessage(CommandLineOptions.getHelpMessage(programSettings), ExitType.SUCCESS);
    }

    private static void doVersion(ProgramSettings programSettings) {
        ProgramExitter.exitProgramWithMessage(programSettings.getProgramName() + ": version " +
                programSettings.getProgramVersion(), ExitType.SUCCESS);
    }

    private static boolean isInvalidSourceDirectory(String sourceDirectoryString) {
        File sourceDirectory = new File(sourceDirectoryString);

        return !sourceDirectory.exists() || !sourceDirectory.isDirectory() || !sourceDirectory.canRead();
    }
}
