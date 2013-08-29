package com.selesse.jxlint.model.rules;

public class NonExistentLintRuleException extends Exception {
    private String ruleName;

    public NonExistentLintRuleException(String ruleName) {
        super("Lint rule '" + ruleName + "' does not exist.");
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }
}
