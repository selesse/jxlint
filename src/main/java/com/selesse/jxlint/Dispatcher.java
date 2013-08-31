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
    public static void dispatch(ProgramOptions programOptions) {
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
        // TODO verify mutually exclusive characteristics of disable/enable (I might want to disable but also enable)
        else if (programOptions.hasOption("disable")) {
            String disabledRules = programOptions.getOption("disable");
            List<String> disabledRulesList = ProgramOptions.getListFromRawOptionStringOrDie(disabledRules);
            doLint(LintRulesImpl.getInstance().getAllEnabledRulesExcept(disabledRulesList), programOptions);
        }
        else if (programOptions.hasOption("enable")) {
            String enabledRules = programOptions.getOption("enable");
            List<String> enabledRulesList = ProgramOptions.getListFromRawOptionStringOrDie(enabledRules);
            doLint(LintRulesImpl.getInstance().getAllEnabledRulesAsWellAs(enabledRulesList), programOptions);
        }
        else if (programOptions.getSourceDirectory() != null) {
            verifySourceDirectoryThenDoLint(programOptions);
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

    private static void verifySourceDirectoryThenDoLint(ProgramOptions programOptions) {
        String sourceDirectoryString = programOptions.getSourceDirectory();
        File sourceDirectory = new File(sourceDirectoryString);

        if (sourceDirectory.exists() && sourceDirectory.isDirectory() && sourceDirectory.canRead()) {
            doLint(programOptions);
            Main.exitProgramWithMessage("", ExitType.SUCCESS);
        }
        else {
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

    private static void doLint(ProgramOptions programOptions) {
        doLint(LintRulesImpl.getInstance().getAllEnabledRules(), programOptions);
    }

    private static void doLint(List<LintRule> rules, ProgramOptions programOptions) {
        List<LintRule> failedRules = Lists.newArrayList();

        for (LintRule lintRule : rules) {
            if (!lintRule.validate(programOptions.getSourceDirectory())) {
                failedRules.add(lintRule);
            }
        }

        List<LintError> lintErrors = getLintErrorsFrom(failedRules);

        try {
            Reporter reporter = programOptions.createReporterFor(lintErrors);
            reporter.outputReport();
        } catch (UnableToCreateReportException e) {
            Main.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
        }

        if (failedRules.size() > 0) {
            Main.exitProgram(ExitType.FAILED);
        }
    }

    private static List<LintError> getLintErrorsFrom(List<LintRule> failedRules) {
        return Lists.newArrayList();
    }
}
