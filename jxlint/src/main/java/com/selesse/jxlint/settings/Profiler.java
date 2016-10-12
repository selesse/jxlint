package com.selesse.jxlint.settings;

import com.google.common.base.Stopwatch;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.report.color.Color;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class Profiler {
    private static Profiler instance = new Profiler();

    private Stopwatch stopwatch;
    private boolean isEnabled;
    private Map<LintRule, Long> ruleExecutionTimeMap;

    private Profiler() {
        ruleExecutionTimeMap = new TreeMap<>((rule1, rule2) -> rule1.getName().compareToIgnoreCase(rule2.getName()));
    }

    public static void beginProgramProfiling() {
        instance.stopwatch = Stopwatch.createStarted();
    }

    public static void endProgramProfiling() {
        instance.stopwatch = instance.stopwatch.stop();
    }

    public static boolean isEnabled() {
        return instance.isEnabled;
    }

    public static void setEnabled(boolean isEnabled) {
        instance.isEnabled = isEnabled;
    }

    public static void addExecutionTime(LintRule lintRule, long executionTimeMs) {
        instance.ruleExecutionTimeMap.put(lintRule, executionTimeMs);
    }

    private static String getJxlintRuntimeReportString() {
        return String.format("Program completed in %3.3f seconds.",
                (instance.stopwatch.elapsed(TimeUnit.MILLISECONDS)) / 1000.0);
    }

    private static String getRuleReportString() {
        StringBuilder ruleReport = new StringBuilder();
        boolean shouldPrintBold = false;

        for (Map.Entry<LintRule, Long> lintRuleAndLongEntry : instance.ruleExecutionTimeMap.entrySet()) {
            LintRule lintRule = lintRuleAndLongEntry.getKey();
            long executionTimeMs = lintRuleAndLongEntry.getValue();

            String ruleReportString =
                    String.format("%-40s %3.3f seconds", lintRule.getName(), executionTimeMs / 1000.0);
            if (shouldPrintBold) {
                ruleReportString = Color.WHITE.wrapAround(ruleReportString);
            }

            ruleReport.append(ruleReportString).append("\n");

            shouldPrintBold = !shouldPrintBold;
        }

        return ruleReport.toString();
    }

    public static String getGeneratedProfileReport() {
        String profileReport = "\n\n" + getJxlintRuntimeReportString();

        String ruleReportString = getRuleReportString();
        if (ruleReportString.trim().length() > 0) {
            profileReport += "\n\n" + getRuleReportString();
        }

        return profileReport;
    }

    public static void printProfileReport() {
        System.out.println(getGeneratedProfileReport());
    }
}
