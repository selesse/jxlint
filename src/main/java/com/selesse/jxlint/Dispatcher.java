package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.LintRule;
import com.selesse.jxlint.model.NonExistentLintRuleException;
import com.selesse.jxlint.model.ProgramOptions;

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
        LintRules lintRules = LintRules.getInstance();
        for (LintRule lintRule : lintRules.getAllRules()) {
            System.out.println(lintRule.getSummaryOutput());
        }
        Main.exitProgram(ExitType.SUCCESS);
    }

    private static void doShow(ProgramOptions programOptions) {
        try {
            if (programOptions.hasOption("show") && programOptions.getOption("show") == null) {
                for (LintRule rule : LintRules.getInstance().getAllRules()) {
                    System.out.println(rule.getDetailedOutput() + "\n\n");
                }
                System.out.println("There are " + LintRules.getInstance().getAllRules().size() + " rules.");
            }
            else {
                String[] rules = programOptions.getOption("show").split(",");
                for (String rule : rules) {
                    LintRule lintRule = LintRules.getInstance().getLintRule(rule.trim());
                    System.out.println(lintRule.getDetailedOutput() + "\n\n");
                }
            }
            Main.exitProgram(ExitType.SUCCESS);
        }
        catch (NonExistentLintRuleException e) {
            Main.exitProgramWithMessage(String.format("'%s' is not a valid rule.", e.getRuleName()),
                    ExitType.COMMAND_LINE_ERROR);
        }
    }
}
