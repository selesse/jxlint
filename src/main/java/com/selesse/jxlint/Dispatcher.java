package com.selesse.jxlint;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;

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
            doLint(programOptions);
        }
        else {
            Main.exitProgramWithMessage("Unable to parse program options.", ExitType.COMMAND_LINE_ERROR);
        }
    }

    private static void doHelp(ProgramOptions programOptions) {
        Main.exitProgramWithMessage(programOptions.getHelpMessage(), ExitType.SUCCESS);
    }

    private static void doVersion() {
        Main.exitProgramWithMessage(Main.getProgramName() + ": version " + Main.getProgramVersion(), ExitType.SUCCESS);
    }

    private static void doList() {
        String outputBuffer = "";
        LintRules lintRules = LintRulesImpl.getInstance();
        if (lintRules.getAllRules().size() == 0) {
            outputBuffer += "There are no rules defined.";
        }
        for (LintRule lintRule : lintRules.getAllRules()) {
            outputBuffer += lintRule.getSummaryOutput() + "\n";
        }
        Main.exitProgramWithMessage(outputBuffer, ExitType.SUCCESS);
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

    private static void doLint(ProgramOptions programOptions) {
        String sourceDirectoryString = programOptions.getSourceDirectory();
        File sourceDirectory = new File(sourceDirectoryString);

        if (sourceDirectory.exists() && sourceDirectory.isDirectory() && sourceDirectory.canRead()) {
            performLint();
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

    private static void performLint() {
        performLint(LintRulesImpl.getInstance().getAllEnabledRules());
    }

    private static void performLint(List<LintRule> rules) {
        List<LintRule> failedRules = Lists.newArrayList();

        for (LintRule lintRule : rules) {
            if (!lintRule.validate()) {
                failedRules.add(lintRule);
            }
        }
    }
}
