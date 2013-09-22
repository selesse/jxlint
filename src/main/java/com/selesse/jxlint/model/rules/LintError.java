package com.selesse.jxlint.model.rules;

public class LintError {
    private LintRule lintRule;
    private int lineNumber;
    private String miscErrorMessage;

    public LintError(LintRule lintRule) {
        this.lintRule = lintRule;
        this.lineNumber = 0;
        this.miscErrorMessage = "";
    }

    public LintError(LintRule lintRule, int lineNumber) {
        this(lintRule);
        this.lineNumber = lineNumber;
    }

    public LintError(LintRule lintRule, Exception e) {
        this(lintRule);
        this.miscErrorMessage = "Exception " + e.getClass().getName() + " was thrown";
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(lintRule.getName()).append(" failed");
        if (lineNumber > 0) {
            stringBuilder.append(" at line " + lineNumber);
        }
        if (miscErrorMessage.length() > 0) {
            stringBuilder.append(" due to " + miscErrorMessage);
        }

        return stringBuilder.toString();
    }
}
