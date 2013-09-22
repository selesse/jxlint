package com.selesse.jxlint.samplerulestest.textfiles;

import com.selesse.jxlint.AbstractPassFailFileTest;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.samplerules.textfiles.TextFileLintRulesTestImpl;

import java.io.File;

public abstract class AbstractPassFailFileTextFileTest extends AbstractPassFailFileTest {
    public AbstractPassFailFileTextFileTest(LintRule lintRule) {
        super(new TextFileLintRulesTestImpl(), new File("src/test/resources/samplerules/textfiles"), lintRule);
    }
}
