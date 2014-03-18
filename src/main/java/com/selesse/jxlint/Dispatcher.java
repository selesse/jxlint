package com.selesse.jxlint;

import com.google.common.collect.Lists;
import com.selesse.jxlint.actions.LintHandler;
import com.selesse.jxlint.actions.LintRuleInformation;
import com.selesse.jxlint.cli.CommandLineOptions;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.settings.ProgramSettings;

import java.io.File;
import java.util.List;

public class Dispatcher {
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
        // If/else train of mutually exclusive options
        if (programOptions.hasOption("help")) {
            doHelp(programSettings);
        }
        else if (programOptions.hasOption("version")) {
            doVersion(programSettings);
        }
        else if (programOptions.hasOption("list")) {
            LintRuleInformation.listRules();
        }
        else if (programOptions.hasOption("show")) {
            LintRuleInformation.showRules(programOptions);
        }

        boolean warningsAreErrors = false;
        if (programOptions.hasOption("Werror")) {
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

            LintRulesImpl.getInstance().setSourceDirectory(new File(sourceDirectoryString));
        }
        else {
            ProgramExitter.exitProgramWithMessage("Error: could not find directory to validate.", ExitType.COMMAND_LINE_ERROR);
        }

        if (programOptions.hasOption("check")) {
            String checkRules = programOptions.getOption("check");
            List<String> checkRulesList = ProgramOptions.getListFromRawOptionStringOrDie(checkRules);

            handleLint(LintRulesImpl.getInstance().getOnlyRules(checkRulesList), warningsAreErrors, programOptions);
        }

        // By default, we only check enabled rules.
        // We adjust this according to the options below.
        List<LintRule> lintRuleList = Lists.newArrayList(LintRulesImpl.getInstance().getAllEnabledRules());

        if (programOptions.hasOption("Wall")) {
            lintRuleList = Lists.newArrayList(LintRulesImpl.getInstance().getAllRules());
        }
        else if (programOptions.hasOption("nowarn")) {
            lintRuleList = Lists.newArrayList(LintRulesImpl.getInstance().getAllRulesWithSeverity(Severity.ERROR));
            lintRuleList.addAll(LintRulesImpl.getInstance().getAllRulesWithSeverity(Severity.FATAL));
        }

        // Options that are "standalone"
        if (programOptions.hasOption("disable")) {
            String disabledRules = programOptions.getOption("disable");
            List<String> disabledRulesList = ProgramOptions.getListFromRawOptionStringOrDie(disabledRules);
            lintRuleList.removeAll(LintRulesImpl.getInstance().getOnlyRules(disabledRulesList));
        }
        if (programOptions.hasOption("enable")) {
            String enabledRules = programOptions.getOption("enable");
            List<String> enabledRulesList = ProgramOptions.getListFromRawOptionStringOrDie(enabledRules);
            lintRuleList.addAll(LintRulesImpl.getInstance().getOnlyRules(enabledRulesList));
        }

        handleLint(lintRuleList, warningsAreErrors, programOptions);
    }

    private static void handleLint(List<LintRule> lintRules, boolean warningsAreErrors, ProgramOptions options) {
        LintHandler lintHandler = new LintHandler(lintRules, warningsAreErrors, options);
        lintHandler.handleLint();
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
