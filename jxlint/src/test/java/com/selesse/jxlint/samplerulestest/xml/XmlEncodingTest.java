package com.selesse.jxlint.samplerulestest.xml;

import com.selesse.jxlint.samplerules.xml.rules.XmlEncodingRule;

public class XmlEncodingTest extends AbstractPassFailFileXmlFileTest {
    public XmlEncodingTest() {
        super(new XmlEncodingRule());
    }
}
