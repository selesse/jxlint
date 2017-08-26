package com.selesse.jxlint.model.rules;

public class LintRulesTestImpl extends AbstractLintRules {
    @Override
    public void initializeLintRules() {
        lintRules.add(new LintRuleTestImpl());
    }
}
