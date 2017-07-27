package com.selesse.jxlint.samplerulestest.xml;

import com.google.common.io.Resources;
import com.selesse.jxlint.AbstractPassFailFileTest;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;

import java.io.File;

public abstract class AbstractPassFailFileXmlFileTest extends AbstractPassFailFileTest {
    public AbstractPassFailFileXmlFileTest(LintRule lintRule) {
        super(new XmlLintRulesTestImpl(), new File(Resources.getResource("samplerules/xml").getPath()), lintRule);
    }
}
