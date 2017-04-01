package com.selesse.jxlintimpl.rules;

import com.selesse.jxlint.model.rules.AbstractLintRules;
import com.selesse.jxlintimpl.rules.impl.FunctionsStartingWithTestAreTests;
import com.selesse.jxlintimpl.rules.impl.Slf4jLoggerStringFormat;

public class JxlintImplRules extends AbstractLintRules {
    @Override
    public void initializeLintRules() {
        lintRules.add(new FunctionsStartingWithTestAreTests());
        lintRules.add(new Slf4jLoggerStringFormat());
    }
}
