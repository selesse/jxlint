package com.selesse.jxlint.linter;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;

import java.util.List;

public class LinterImpl implements Linter {
    private List<LintRule> rules;
    private List<LintError> lintErrors;

    public LinterImpl(List<LintRule> rules) {
        this.rules = rules;
        lintErrors = Lists.newArrayList();
    }

    @Override
    public void doLint(ProgramOptions programOptions) {
        for (LintRule lintRule : rules) {
            lintRule.validate();
            lintErrors.addAll(lintRule.getFailedRules());
        }
    }

    @Override
    public List<LintError> getLintErrors() {
        return lintErrors;
    }
}
