package com.selesse.jxlint.linter;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.settings.Profiler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Simple implementation of a Linter. Goes through all the {@link LintRule}s and calls
 * {@link com.selesse.jxlint.model.rules.LintRule#validate()}. If there are any errors, this class
 * accumulates them. Call {@link #getLintErrors()} to retrieve them.
 */
public class Linter {
    private List<LintRule> rules;
    private List<LintError> lintErrors;

    public Linter(List<LintRule> rules) {
        this.rules = rules;
        lintErrors = Lists.newArrayList();
    }

    /**
     * This validates (or invalidates) every lint rule. {@link com.selesse.jxlint.model.rules.LintError}s may arise
     * through failed validations. For every rule that fails a validation, there should be a corresponding
     * {@link com.selesse.jxlint.model.rules.LintError}.
     */
    public void performLintValidations() {
        for (LintRule lintRule : rules) {
            long startTime = System.currentTimeMillis();
            lintRule.validate();
            List<LintError> lintErrorList = lintRule.getLintErrors();
            Collections.sort(lintErrorList, lineNumberComparator);
            lintErrors.addAll(lintRule.getLintErrors());
            long endTime = System.currentTimeMillis();

            Profiler.addExecutionTime(lintRule, endTime - startTime);
        }
    }

    private static final Comparator<LintError> lineNumberComparator = new Comparator<LintError>() {
        @Override
        public int compare(LintError o1, LintError o2) {
            return o1.getLineNumber() - o2.getLineNumber();
        }
    };

    /**
     * Returns all the {@link com.selesse.jxlint.model.rules.LintError}s that have been found through validations
     * performed in {@link #performLintValidations()}.
     */
    public List<LintError> getLintErrors() {
        return lintErrors;
    }
}
