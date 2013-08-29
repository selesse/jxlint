package com.selesse.jxlint.model.rules;

public class LintRulesImpl {
    private static LintRules instance;

    public static LintRules getInstance() {
        if (instance == null) {
            throw new RuntimeException("No instance of LintRules is defined.");
        }
        return instance;
    }

    public static void setInstance(LintRules instance) {
        LintRulesImpl.instance = instance;
    }
}
