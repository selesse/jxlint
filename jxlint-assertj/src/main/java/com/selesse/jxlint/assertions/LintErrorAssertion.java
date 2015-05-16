package com.selesse.jxlint.assertions;

import com.selesse.jxlint.model.rules.LintError;
import org.assertj.core.api.AbstractAssert;

import java.io.File;

public class LintErrorAssertion extends AbstractAssert<LintErrorAssertion, LintError> {
    public LintErrorAssertion(LintError actual) {
        super(actual, LintErrorAssertion.class);
    }

    public LintErrorAssertion occursOnLineNumber(int lineNumber) {
        isNotNull();

        if (actual.getLineNumber() == -1) {
            failWithMessage("Expected error for \"" + actual.getViolatedRule().getName() + "\" to occur on line " +
                    "number " + lineNumber + ", but no line number was set");
        }
        if (actual.getLineNumber() != lineNumber) {
            failWithMessage("Expected error for \"" + actual.getViolatedRule().getName() + "\" to occur on line " +
                    "number <%s>, but was <%s>", lineNumber, actual.getLineNumber());
        }

        return this;
    }

    public LintErrorAssertion isViolatedIn(String fileName) {
        isNotNull();

        if (!actual.getFile().getName().equals(fileName)) {
            failWithMessage("Expected error for \"" + actual.getViolatedRule().getName() + "\" to have been violated" +
                    " in <%s>, but was <%s>", fileName, actual.getFile().getName());
        }

        return this;
    }

    public LintErrorAssertion isViolatedIn(File file) {
        isNotNull();

        if (actual.getFile().compareTo(file) != 0)  {
            failWithMessage("Expected error for \"" + actual.getViolatedRule().getName() + "\" to have been violated" +
                    " in <%s>, but was <%s>", file.getName(), actual.getFile().getName());
        }

        return this;
    }

    public LintErrorAssertion hasErrorMessage(String errorMessage) {
        isNotNull();

        if (!actual.getMessage().equals(errorMessage)) {
            failWithMessage("Expected error message for \"" + actual.getViolatedRule().getName() + "\" to "
                    + "be <%s>, but was <%s>", errorMessage, actual.getMessage());
        }

        return this;
    }

    public LintErrorAssertion hasErrorMessageContaining(String errorMessage) {
        isNotNull();

        if (!actual.getMessage().contains(errorMessage)) {
            failWithMessage("Expected error message for \"" + actual.getViolatedRule().getName() + "\" to "
                    + "contain <%s>, but was <%s>", errorMessage, actual.getMessage());
        }

        return this;
    }
}
