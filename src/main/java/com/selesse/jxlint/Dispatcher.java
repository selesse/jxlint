package com.selesse.jxlint;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.*;
import com.selesse.jxlint.report.Reporter;
import com.selesse.jxlint.report.UnableToCreateReportException;

import java.io.File;
import java.util.List;

public class Dispatcher {
    private static boolean warningsAreErrors = false;

    public static void dispatch(ProgramOptions programOptions) {
        // If/else train of mutually exclusive options
        if (programOptions.hasOption("help")) {
            doHelp(programOptions);
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
        else if (programOptions.hasOption("check")) {
            String checkRules = programOptions.getOption("check");
            List<String> checkRulesList = ProgramOptions.getListFromRawOptionStringOrDie(checkRules);
            doLint(LintRulesImpl.getInstance().getOnlyRules(checkRulesList), programOptions);
        }

        // By default, we only check enabled rules.
        // We adjust this according to the options below.
        List<LintRule> lintRuleList = LintRulesImpl.getInstance().getAllEnabledRules();

        if (programOptions.hasOption("Wall")) {
            lintRuleList = LintRulesImpl.getInstance().getAllRules();
        }
        else if (programOptions.hasOption("nowarn")) {
            lintRuleList = LintRulesImpl.getInstance().getAllRulesWithSeverity(Severity.ERROR);
        }

        // Options that are "standalone"
        if (programOptions.hasOption("Werror")) {
            warningsAreErrors = true;
        }
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

        if (programOptions.getSourceDirectory() != null) {
            String sourceDirectoryString = programOptions.getSourceDirectory();
            if (isValidSourceDirectory(sourceDirectoryString)) {
                doLint(lintRuleList, programOptions);
            }
            else {
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
        }
        else {
            Main.exitProgramWithMessage("Error: could not find directory to validate.", ExitType.COMMAND_LINE_ERROR);
        }
    }

    private static void doHelp(ProgramOptions programOptions) {
        Main.exitProgramWithMessage(programOptions.getHelpMessage(), ExitType.SUCCESS);
    }

    private static void doVersion() {
        Main.exitProgramWithMessage(Main.getProgramName() + ": version " + Main.getProgramVersion(), ExitType.SUCCESS);
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
            String outputBuffer = "";
            if (programOptions.hasOption("show") && programOptions.getOption("show") == null) {
                for (LintRule rule : LintRulesImpl.getInstance().getAllRules()) {
                    outputBuffer += rule.getDetailedOutput() + "\n\n";
                }
                outputBuffer += "There are " + LintRulesImpl.getInstance().getAllRules().size() + " rules.";
            }
            else {
                String[] rules = programOptions.getOption("show").split(",");
                for (String rule : rules) {
                    LintRule lintRule = LintRulesImpl.getInstance().getLintRule(rule.trim());
                    outputBuffer += lintRule.getDetailedOutput() + "\n\n";
                }
            }
            Main.exitProgramWithMessage(outputBuffer, ExitType.SUCCESS);
        }
        catch (NonExistentLintRuleException e) {
            Main.exitProgramWithMessage(String.format("'%s' is not a valid rule.", e.getRuleName()),
                    ExitType.COMMAND_LINE_ERROR);
        }
    }

    private static boolean isValidSourceDirectory(String sourceDirectoryString) {
        File sourceDirectory = new File(sourceDirectoryString);

        return sourceDirectory.exists() && sourceDirectory.isDirectory() && sourceDirectory.canRead();
    }

    private static void doLint(List<LintRule> rules, ProgramOptions programOptions) {
        List<LintError> failedRules = Lists.newArrayList();

        for (LintRule lintRule : rules) {
            lintRule.validate();
            failedRules.addAll(lintRule.getFailedRules());
        }

        try {
            Reporter reporter = programOptions.createReporterFor(failedRules);
            reporter.outputReport();
        } catch (UnableToCreateReportException e) {
            Main.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
        }

        if (failedRules.size() > 0) {
            Main.exitProgram(ExitType.FAILED);
        }

        Main.exitProgram(ExitType.SUCCESS);
    }
}
