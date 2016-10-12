package com.selesse.jxlint.model.rules;

/**
 * Specific {@link java.lang.Exception} for the case where a lint rule was queried but did not exist.
 */
public class NonExistentLintRuleException extends Exception {
    private String ruleName;

    NonExistentLintRuleException(String ruleName) {
        super("Lint rule '" + ruleName + "' does not exist.");
        this.ruleName = ruleName;
    }

    /**
     * Returns the rule name that was queried but wasn't found.
     */
    public String getRuleName() {
        return ruleName;
    }
}
