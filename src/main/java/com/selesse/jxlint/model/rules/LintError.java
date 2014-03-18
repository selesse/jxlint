package com.selesse.jxlint.model.rules;

import com.selesse.jxlint.utils.FileUtils;

import java.io.File;

/**
 * A lint error representation. Uses the Builder pattern for its optional parameters.
 * It knows about which rule was violated and the file that violated it. It may have other
 * information, like the line number in which the violation was found, an error message,
 * an Exception relating to its violation.
 */
public class LintError {
    private LintRule violatedRule;
    private int lineNumber;
    private String errorMessage;
    private Exception e;
    /**
     * The file that failed the validation.
     */
    private File faultyFile;

    /**
     * Creates a {@link LintError} with the {@link com.selesse.jxlint.model.rules.LintRule} and
     * {@link java.io.File} parameters. This constructor should be used when chaining multiple
     * parameters, i.e. line number and error message and exception, and is finalized by calling
     * {@link com.selesse.jxlint.model.rules.LintError.LintErrorBuilder#create()}.
     *
     * For example:
     *
     * <pre>
     *     LintError lintError = LintError.with(this, file).andLineNumber(lineNumber).andErrorMessage(
     *          "You forgot to do something!").create();
     * </pre>
     */
    public static LintErrorBuilder with(LintRule violatedRule, File faultyFile) {
        return new LintErrorBuilder(violatedRule, faultyFile);
    }

    private LintError(LintRule violatedRule, File faultyFile) {
        this.violatedRule = violatedRule;
        this.faultyFile = FileUtils.normalizeFile(faultyFile);
    }

    public LintRule getViolatedRule() {
        return violatedRule;
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

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setException(Exception e) {
        this.e = e;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'").append(violatedRule.getName()).append("' failed");
        if (lineNumber > 0) {
            stringBuilder.append(" at line ").append(lineNumber);
        }
        if (errorMessage.length() > 0) {
            stringBuilder.append(": ").append(errorMessage);
        }

        return stringBuilder.toString();
    }

    /**
     * Utility class for creating a {@link com.selesse.jxlint.model.rules.LintError}.
     */
    public static class LintErrorBuilder {
        private final LintError lintError;

        public LintErrorBuilder(LintRule lintRule, File file) {
            this.lintError = new LintError(lintRule, file);
        }

        /**
         * Adds an associated {@link java.lang.Exception} to the error.
         */
        public LintErrorBuilder andException(Exception e) {
            this.lintError.setException(e);
            return this;
        }

        /**
         * Adds an associated error message to the error. This message is printed in the reports if available.
         */
        public LintErrorBuilder andErrorMessage(String errorMessage) {
            this.lintError.setErrorMessage(errorMessage);
            return this;
        }

        /**
         * Adds an associated line number to the error. This is printed in the reports if available.
         */
        public LintErrorBuilder andLineNumber(int lineNumber) {
            this.lintError.setLineNumber(lineNumber);
            return this;
        }

        /**
         * Create the {@link com.selesse.jxlint.model.rules.LintError} for this builder. Should be called
         * when all the optional arguments are provided.
         */
        public LintError create() {
            return lintError;
        }
    }
}
