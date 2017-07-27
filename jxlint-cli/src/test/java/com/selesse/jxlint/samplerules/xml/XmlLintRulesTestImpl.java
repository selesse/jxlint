package com.selesse.jxlint.samplerules.xml;

import com.selesse.jxlint.model.rules.AbstractLintRules;
import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import com.selesse.jxlint.samplerules.xml.rules.UniqueAttributeRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlEncodingRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlVersionRule;

/**
 * Implementation of a {@link com.selesse.jxlint.model.rules.LintRules} used for testing.
 */
public class XmlLintRulesTestImpl extends AbstractLintRules {
    @Override
    public void initializeLintRules() {
        // Example rule saying that duplicate attribute tags within XML are bad
        lintRules.add(new UniqueAttributeRule());

        // Example rule saying that XML Version needs to be specified
        lintRules.add(new XmlVersionRule());

        // Example rule saying that XML encoding needs to be specified
        lintRules.add(new XmlEncodingRule());

        // Example rule saying that author tag needs to exist somewhere in every XML file
        lintRules.add(new AuthorTagRule());
    }
}
