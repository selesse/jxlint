package com.selesse.jxlint.samplerules.textfiles;

import com.selesse.jxlint.model.rules.AbstractLintRules;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.samplerules.textfiles.rules.MustHaveAuthor;

public class TextFileLintRulesTestImpl extends AbstractLintRules implements LintRules {
    @Override
    public void initializeLintRules() {
        lintRules.add(new MustHaveAuthor());
    }
}
