package com.selesse.jxlint.samplerulestest.xml;

import com.selesse.jxlint.samplerules.xml.rules.XmlVersionRule;

public class XmlVersionTest extends AbstractPassFailFileXmlFileTest {
    public XmlVersionTest() {
        super(new XmlVersionRule());
    }
}
