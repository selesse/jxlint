package com.selesse.jxlintimpl;

import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlintimpl.rules.JxlintImplRules;
import org.junit.Before;

public class JxlintImplTest {
    @Before
    public void setup() {
        LintRulesImpl.setInstance(new JxlintImplRules());
    }
}
