package com.selesse.jxlint.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.AbstractTestCase;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import com.selesse.jxlint.samplerules.xml.rules.UniqueAttributeRule;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramOptionsTest extends AbstractTestCase {
    @BeforeClass
    public static void setup() {
        LintRulesImpl.setInstance(new XmlLintRulesTestImpl());
    }

    @Test(expected = NonExistentLintRuleException.class)
    public void testGettingAnInvalidRuleThrowsException() throws NonExistentLintRuleException {
        ProgramOptions.getRuleListFromOptionString("blah");
    }

    @Test
    public void testGettingValidRulesReturnsThem() throws NonExistentLintRuleException {
        LintRule authorTagRule = new AuthorTagRule();
        LintRule uniqueAttributeRule = new UniqueAttributeRule();

        List<String> ruleList = ProgramOptions.getRuleListFromOptionString(
                Joiner.on(", ").join(Lists.newArrayList(authorTagRule.getName(), uniqueAttributeRule.getName()))
        );

        assertThat(ruleList).hasSize(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGettingInvalidCategoriesThrowsAnException() {
        ProgramOptions.getCategoryListFromOptionString("blah");
    }

    @Test
    public void testGettingValidCategoriesReturnsThem() {
        List<String> categoryList = ProgramOptions.getCategoryListFromOptionString(
                Joiner.on(", ").join(Category.CORRECTNESS, Category.LINT, Category.PERFORMANCE, Category.STYLE)
        );

        assertThat(categoryList).hasSize(4);
    }
}
