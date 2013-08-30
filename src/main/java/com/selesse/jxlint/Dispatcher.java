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
        else if (programOptions.getSourceDirectory() != null) {
            verifySourceDirectoryThenValidate(programOptions);
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

    private static void verifySourceDirectoryThenValidate(ProgramOptions programOptions) {
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

        Reporter reporter = null;
        try {
            reporter = programOptions.createReporterFor(lintErrors);
        } catch (UnableToCreateReportException e) {
            Main.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
        }
        reporter.outputReport();

        if (failedRules.size() > 0) {
            Main.exitProgram(ExitType.FAILED);
        }
    }

    private static List<LintError> getLintErrorsFrom(List<LintRule> failedRules) {
        return Lists.newArrayList();
    }
}
