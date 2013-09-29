package com.selesse.jxlint.linter;

import com.selesse.jxlint.model.rules.LintRule;

import java.util.List;

public class LintFactory {
    private static boolean isTestMode = false;
    private static Linter instance;

    public static Linter createNewLinter(List<LintRule> rules, boolean warningsAreErrors) {
        if (isTestMode) {
            instance = new TestLinterImpl(rules, warningsAreErrors);
        }
        else {
            instance = new LinterImpl(rules, warningsAreErrors);
        }
        return instance;
    }

    public static Linter getInstance() {
        return instance;
    }

    public static void setTestMode(boolean isTestMode) {
        LintFactory.isTestMode = isTestMode;
    }
}
