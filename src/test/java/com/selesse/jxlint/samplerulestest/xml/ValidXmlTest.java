package com.selesse.jxlint.samplerulestest.xml;

import com.selesse.jxlint.samplerules.xml.rules.ValidXmlRule;

public class ValidXmlTest extends AbstractPassFailFileXmlFileTest {
    public ValidXmlTest() {
        super(new ValidXmlRule());
    }
}
