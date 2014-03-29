package com.selesse.jxlint.samplerulestest.textfiles;

import com.google.common.io.Resources;
import com.selesse.jxlint.AbstractPassFailFileTest;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.samplerules.textfiles.TextFileLintRulesTestImpl;

import java.io.File;

public abstract class AbstractPassFailFileTextFileTest extends AbstractPassFailFileTest {
    public AbstractPassFailFileTextFileTest(LintRule lintRule) {
        super(new TextFileLintRulesTestImpl(), new File(Resources.getResource("samplerules/textfiles").getPath()),
                lintRule);

    }
}
