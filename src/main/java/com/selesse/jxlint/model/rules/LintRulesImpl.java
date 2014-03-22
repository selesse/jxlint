package com.selesse.jxlint.model.rules;

/**
 * A singleton for the {@link LintRules} implementation. Also contains information about whether or not the program
 * is in test mode.
 */
public class LintRulesImpl {
    private static LintRules instance;
    private static boolean willExitAfterReporting;

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
     * Returns whether or not the program will exit after reporting. Specifically,
     * if it will call {@link System#exit(int)}.
     */
    public static boolean willExitAfterReporting() {
        return LintRulesImpl.willExitAfterReporting;
    }

    /**
     * Set whether or not the program will exit after reporting. Specifically,
     * it will call {@link System#exit(int)} if exitAfterReporting is true.
     */
    public static void setExitAfterReporting(boolean exitAfterReporting) {
        LintRulesImpl.willExitAfterReporting = exitAfterReporting;
    }
}
