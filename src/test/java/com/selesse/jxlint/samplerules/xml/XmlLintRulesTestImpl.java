package com.selesse.jxlint.samplerules.xml;

import com.selesse.jxlint.model.rules.AbstractLintRules;
import com.selesse.jxlint.samplerules.xml.rules.UniqueAttributeRule;
import com.selesse.jxlint.samplerules.xml.rules.ValidXmlRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlVersionRule;

/**
 * Implementation of a {@link com.selesse.jxlint.model.rules.LintRules} used for testing.
 */
public class XmlLintRulesTestImpl extends AbstractLintRules {
    @Override
    public void initializeLintRules() {
        // Example rule saying that XML must be valid
        lintRules.add(new ValidXmlRule());

        // Example rule saying that duplicate attribute tags within XML are bad
        lintRules.add(new UniqueAttributeRule());

        // Example (disabled-by-default) rule
        lintRules.add(new XmlVersionRule());
    }
}
