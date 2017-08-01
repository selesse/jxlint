package com.selesse.jxlint.maven;

import com.selesse.jxlint.model.rules.AbstractLintRules;
import com.selesse.jxlint.model.rules.LintRules;

public class SampleLintRules extends AbstractLintRules implements LintRules {
    @Override
    public void initializeLintRules() {
        lintRules.add(new SampleRule());
    }
}
