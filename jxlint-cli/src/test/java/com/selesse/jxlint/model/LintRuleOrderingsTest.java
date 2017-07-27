package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import com.selesse.jxlint.samplerules.xml.rules.UniqueAttributeRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlEncodingRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlVersionRule;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LintRuleOrderingsTest {

    private AuthorTagRule authorTagRule;
    private UniqueAttributeRule uniqueAttributeRule;
    private XmlEncodingRule xmlEncodingRule;
    private XmlVersionRule xmlVersionRule;
    private List<LintRule> lintRules;

    @Before
    public void setup() {
        authorTagRule = new AuthorTagRule();
        uniqueAttributeRule = new UniqueAttributeRule();
        xmlEncodingRule = new XmlEncodingRule();
        xmlVersionRule = new XmlVersionRule();

        lintRules = Lists.newArrayList(
                authorTagRule,          // Category.STYLE
                uniqueAttributeRule,    // Category.PERFORMANCE
                xmlEncodingRule,        // Category.LINT
                xmlVersionRule          // Category.LINT
        );
    }

    @Test
    public void testCompareCategoryThenName() throws Exception {
        Collections.sort(lintRules, LintRuleOrderings.getCategoryThenNameOrdering());

        assertThat(lintRules).containsExactly(xmlEncodingRule, xmlVersionRule, uniqueAttributeRule, authorTagRule);
    }

}