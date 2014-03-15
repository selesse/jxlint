package com.selesse.jxlint;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.selesse.jxlint.cli.CommandLineOptions;
import com.selesse.jxlint.linter.LintFactory;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.*;
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
     * <li> Perform the lint validation. </li>
     *
     * </ol>
     */
    public static void dispatch(ProgramOptions programOptions) {
        // If/else train of mutually exclusive options
        if (programOptions.hasOption("help")) {
            doHelp();
        }
        else if (programOptions.hasOption("version")) {
            doVersion();
        }
        else if (programOptions.hasOption("list")) {
            doList();
        }
        else if (programOptions.hasOption("show")) {
            doShow(programOptions);
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

                Main.exitProgramWithMessage(outputBuffer, ExitType.COMMAND_LINE_ERROR);
            }

            LintRulesImpl.getInstance().setSourceDirectory(new File(sourceDirectoryString));
        }
        else {
            Main.exitProgramWithMessage("Error: could not find directory to validate.", ExitType.COMMAND_LINE_ERROR);
        }

        if (programOptions.hasOption("check")) {
            String checkRules = programOptions.getOption("check");
            List<String> checkRulesList = ProgramOptions.getListFromRawOptionStringOrDie(checkRules);

            Linter linter = LintFactory.createNewLinter(LintRulesImpl.getInstance().getOnlyRules(checkRulesList),
                    warningsAreErrors);
            linter.doLint(programOptions);
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

        Linter linter = LintFactory.createNewLinter(lintRuleList, warningsAreErrors);
        linter.doLint(programOptions);
    }

    private static void doHelp() {
        Main.exitProgramWithMessage(CommandLineOptions.getHelpMessage(), ExitType.SUCCESS);
    }

    private static void doVersion() {
        Main.exitProgramWithMessage(ProgramSettings.getProgramName() + ": version " +
                ProgramSettings.getProgramVersion(), ExitType.SUCCESS);
    }

    private static void doList() {
        List<String> outputBuffer = Lists.newArrayList();

        LintRules lintRules = LintRulesImpl.getInstance();
        if (lintRules.getAllRules().size() == 0) {
            outputBuffer.add("There are no rules defined.");
        }
        for (LintRule lintRule : lintRules.getAllRules()) {
            outputBuffer.add(lintRule.getSummaryOutput());
        }

        Main.exitProgramWithMessage(Joiner.on("\n").join(outputBuffer), ExitType.SUCCESS);
    }

    private static void doShow(ProgramOptions programOptions) {
        try {
            StringBuilder outputBuffer = new StringBuilder();
            if (programOptions.hasOption("show") && programOptions.getOption("show") == null) {
                for (LintRule rule : LintRulesImpl.getInstance().getAllRules()) {
                    outputBuffer.append(rule.getDetailedOutput()).append("\n\n");
                }
                outputBuffer.append("There are ").append(
                        LintRulesImpl.getInstance().getAllRules().size()).append(" rules.");
            }
            else {
                Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();

                List<String> rules = Lists.newArrayList(splitter.split(programOptions.getOption("show")));
                for (String rule : rules) {
                    LintRule lintRule = LintRulesImpl.getInstance().getLintRule(rule.trim());
                    outputBuffer.append(lintRule.getDetailedOutput()).append("\n\n");
                }
            }
            Main.exitProgramWithMessage(outputBuffer.toString(), ExitType.SUCCESS);
        }
        catch (NonExistentLintRuleException e) {
            Main.exitProgramWithMessage(String.format("'%s' is not a valid rule.", e.getRuleName()),
                    ExitType.COMMAND_LINE_ERROR);
        }
    }

    private static boolean isInvalidSourceDirectory(String sourceDirectoryString) {
        File sourceDirectory = new File(sourceDirectoryString);

        return !sourceDirectory.exists() || !sourceDirectory.isDirectory() || !sourceDirectory.canRead();
    }
}
