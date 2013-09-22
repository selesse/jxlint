package com.selesse.jxlint.samplerulestest.xml;

import com.selesse.jxlint.samplerules.xml.rules.UniqueAttributeRule;

public class UniqueAttributeTest extends AbstractPassFailFileXmlFileTest {
    public UniqueAttributeTest() {
        super(new UniqueAttributeRule());
    }
}
