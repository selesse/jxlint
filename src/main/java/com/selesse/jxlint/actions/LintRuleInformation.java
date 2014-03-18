package com.selesse.jxlint.actions;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.selesse.jxlint.ProgramExitter;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;

import java.util.List;

public class LintRuleInformation {
    public static void listRules() {
        List<String> outputBuffer = Lists.newArrayList();

        LintRules lintRules = LintRulesImpl.getInstance();
        if (lintRules.getAllRules().size() == 0) {
            outputBuffer.add("There are no rules defined.");
        }
        for (LintRule lintRule : lintRules.getAllRules()) {
            outputBuffer.add(lintRule.getSummaryOutput());
        }

        ProgramExitter.exitProgramWithMessage(Joiner.on("\n").join(outputBuffer), ExitType.SUCCESS);
    }

    public static void showRules(ProgramOptions programOptions) {
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
            ProgramExitter.exitProgramWithMessage(outputBuffer.toString(), ExitType.SUCCESS);
        }
        catch (NonExistentLintRuleException e) {
            ProgramExitter.exitProgramWithMessage(String.format("'%s' is not a valid rule.", e.getRuleName()),
                    ExitType.COMMAND_LINE_ERROR);
        }
    }
}
