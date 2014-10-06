package com.selesse.jxlint.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.selesse.jxlint.model.rules.LintError;

import javax.annotation.Nullable;

public class LintErrorOrderings {
    private static Ordering<LintError> lineNumberOrdering = new Ordering<LintError>() {
        @Override
        public int compare(@Nullable LintError left, @Nullable LintError right) {
            return Ints.compare(left.getLineNumber(), right.getLineNumber());
        }
    };

    private static Ordering<LintError> categoryNameOrdering = new Ordering<LintError>() {
        @Override
        public int compare(@Nullable LintError left, @Nullable LintError right) {
            return LintRuleOrderings.getCategoryThenNameOrdering().
                    compare(left.getViolatedRule(), right.getViolatedRule());
        }
    };

    private static Ordering<LintError> categoryNameFileLineNumberOrdering = new Ordering<LintError>() {
        @Override
        public int compare(@Nullable LintError left, @Nullable LintError right) {
            return ComparisonChain.start()
                    .compare(left.getViolatedRule(), right.getViolatedRule(),
                            LintRuleOrderings.getCategoryThenNameOrdering())
                    .compare(left.getFile(), right.getFile())
                    .compare(left.getLineNumber(), right.getLineNumber())
                    .result();
        }
    };

    private static final Ordering<LintError> fileThenLineNumberOrdering = new Ordering<LintError>() {
        @Override
        public int compare(@Nullable LintError left, @Nullable LintError right) {
            return ComparisonChain.start()
                    .compare(left.getFile(), right.getFile())
                    .compare(left.getLineNumber(), right.getLineNumber())
                    .result();
        }
    };

    public static Ordering<LintError> getLineNumberOrdering() {
        return lineNumberOrdering;
    }

    public static Ordering<LintError> getCategoryThenNameOrdering() {
        return categoryNameOrdering;
    }

    public static Ordering<LintError> getCategoryNameFileLineNumberOrdering() {
        return categoryNameFileLineNumberOrdering;
    }

    public static Ordering<LintError> getFileThenLineNumberOrdering() {
        return fileThenLineNumberOrdering;
    }
}
