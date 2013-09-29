package com.selesse.jxlint.model.rules;

import java.io.File;

public class LintError {
    private LintRule lintRule;
    private int lineNumber;
    private String errorMessage;
    private Exception e;
    private File faultyFile;

    public LintError(LintRule lintRule, File faultyFile) {
        this.lintRule = lintRule;
        this.faultyFile = faultyFile;
        this.lineNumber = 0;
        this.errorMessage = "";
    }

    public LintError(LintRule lintRule, File faultyFile, int lineNumber) {
        this(lintRule, faultyFile);
        this.lineNumber = lineNumber;
    }

    public LintError(LintRule lintRule, File faultyFile, Exception e) {
        this(lintRule, faultyFile);
        this.errorMessage = "Exception " + e.getClass().getName() + " was thrown";
        this.e = e;
    }

    public LintError(LintRule lintRule, File faultyFile, String errorMessage) {
        this(lintRule, faultyFile);
        this.errorMessage = errorMessage;
    }

    public LintError(LintRule lintRule, File faultyFile, String errorMessage, Exception e) {
        this(lintRule, faultyFile, errorMessage);
        this.e = e;
    }

    public LintRule getViolatedRule() {
        return lintRule;
    }

    public String getMessage() {
        return errorMessage;
    }

    public File getFile() {
        return faultyFile;
    }

    public Exception getException() {
        return e;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'").append(lintRule.getName()).append("' failed");
        if (lineNumber > 0) {
            stringBuilder.append(" at line ").append(lineNumber);
        }
        if (errorMessage.length() > 0) {
            stringBuilder.append(": ").append(errorMessage);
        }

        return stringBuilder.toString();
    }
}
