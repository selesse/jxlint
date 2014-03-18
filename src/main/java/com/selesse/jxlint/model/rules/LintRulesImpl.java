package com.selesse.jxlint.model.rules;

public class LintRulesImpl {
    private static LintRules instance;
    private static boolean isTestMode;

    public static LintRules getInstance() {
        if (instance == null) {
            throw new RuntimeException("No instance of LintRules is defined.");
        }
        return instance;
    }

    public static void setInstance(LintRules instance) {
        LintRulesImpl.instance = instance;
    }

    public static boolean isTestMode() {
        return LintRulesImpl.isTestMode;
    }

    public static void setTestMode(boolean testMode) {
        LintRulesImpl.isTestMode = testMode;
    }
}
