package com.selesse.jxlint.model.rules;

public class LintRulesTestImpl extends AbstractLintRules implements LintRules {
    @Override
    public void initializeLintRules() {
        lintRules.add(new LintRuleTestImpl());
    }
}
