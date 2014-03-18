package com.selesse.jxlint.model.rules;

/**
 * A singleton for the {@link LintRules} implementation. Also contains information about whether or not the program
 * is in test mode.
 */
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

    /**
     * Returns whether or not the program is currently in test mode. If the program is in test mode,
     * it will not System.exit after reporting.
     */
    public static boolean isTestMode() {
        return LintRulesImpl.isTestMode;
    }

    /**
     * Sets the program's test mode. If the program is in test mode, it will not System.exit after reporting.
     */
    public static void setTestMode(boolean testMode) {
        LintRulesImpl.isTestMode = testMode;
    }
}
