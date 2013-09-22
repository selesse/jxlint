package com.selesse.jxlint.samplerulestest.xml;

import com.selesse.jxlint.AbstractPassFailFileTest;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;

import java.io.File;

public abstract class AbstractPassFailFileXmlFileTest extends AbstractPassFailFileTest {
    public AbstractPassFailFileXmlFileTest(LintRule lintRule) {
        super(new XmlLintRulesTestImpl(), new File("src/test/resources/samplerules/xml"), lintRule);
    }
}
