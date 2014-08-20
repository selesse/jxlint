package com.selesse.jxlint.settings;

import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.report.color.Color;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Profiler {
    private static long startTime;
    private static long stopTime;
    private static boolean isEnabled;
    private static Map<LintRule, Long> ruleExecutionTimeMap = new TreeMap<LintRule, Long>(new Comparator<LintRule>() {
        @Override
        public int compare(LintRule lintRule, LintRule lintRule2) {
            return lintRule.getName().compareToIgnoreCase(lintRule2.getName());
        }
    });

    public static void setStartTime(long startTime) {
        Profiler.startTime = startTime;
    }

    public static void setStopTime(long stopTime) {
        Profiler.stopTime = stopTime;
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void setEnabled(boolean isEnabled) {
        Profiler.isEnabled = isEnabled;
    }

    public static void addExecutionTime(LintRule lintRule, long executionTimeMs) {
        ruleExecutionTimeMap.put(lintRule, executionTimeMs);
    }

    private static String getJxlintRuntimeReportString() {
        return String.format("Program completed in %3.3f seconds.", (stopTime - startTime) / 1000.0);
    }

    private static String getRuleReportString() {
        StringBuilder ruleReport = new StringBuilder();
        boolean shouldPrintBold = false;

        for (Map.Entry<LintRule, Long> lintRuleAndLongEntry : ruleExecutionTimeMap.entrySet()) {
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
