package com.selesse.jxlint.linter;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;

import java.util.List;

public class TestLinterImpl implements Linter {
    private List<LintRule> rules;
    private List<LintError> failedRules;

    public TestLinterImpl(List<LintRule> rules, boolean ignored) {
        this.rules = rules;
    }

    @Override
    public void doLint(ProgramOptions programOptions) {
        failedRules = Lists.newArrayList();

        for (LintRule lintRule : rules) {
            lintRule.validate();
            failedRules.addAll(lintRule.getFailedRules());
        }
    }

    @Override
    public List<LintError> getLintErrors() {
        return failedRules;
    }
}
