package com.selesse.jxlint.model.rules;

import com.google.common.collect.Lists;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractLintRulesTest {
    private LintRules lintRules = new XmlLintRulesTestImpl();

    @Test
    public void testLintRulesGetsAllRuleSizeProperly() {
        assertThat(lintRules.getAllRules()).hasSize(4);
    }

    @Test
    public void testLintRulesGetsAllEnabledRulesSizeProperly() {
        assertThat(lintRules.getAllEnabledRules()).hasSize(3);
    }

    @Test
    public void testLintRulesGetsAllDisabledRulesSizeProperly() {
        List<String> disabledRules = Lists.newArrayList("XML version specified");
        assertThat(lintRules.getAllRulesExcept(disabledRules)).hasSize(3);
    }

    @Test
    public void testLintRulesGetsAllEnabledMinusDisabledRulesSizeProperly() {
        List<String> disabledRules = Lists.newArrayList("Unique attribute");
        assertThat(lintRules.getAllEnabledRulesExcept(disabledRules)).hasSize(2);
    }

    @Test
    public void testLintRulesGetsAllEnabledPlusEnabledRulesSizeProperly() {
        List<String> enabledRules = Lists.newArrayList("XML version specified");
        assertThat(lintRules.getAllEnabledRulesAsWellAs(enabledRules)).hasSize(4);
    }

    @Test
    public void testGetAllRulesBySeverity() {
        List<LintRule> emptyErrorRules = lintRules.getAllRulesWithSeverity(Severity.ERROR);
        assertThat(emptyErrorRules).hasSize(1);

        List<LintRule> fatalRules = lintRules.getAllRulesWithSeverity(Severity.FATAL);
        assertThat(fatalRules).hasSize(1);

        List<LintRule> warningRules = lintRules.getAllRulesWithSeverity(Severity.WARNING);
        assertThat(warningRules).hasSize(2);
    }

    @Test
    public void testLintRulesGetsCheckRuleProperly() {
        List<String> checkRules = Lists.newArrayList("XML version specified");
        assertThat(lintRules.getOnlyRules(checkRules)).hasSize(1);
        assertThat(lintRules.getOnlyRules(checkRules).get(0).getName()).isEqualTo("XML version specified");
    }

    @Test
    public void testLintRulesGetsMultipleCheckRulesProperly() {
        List<String> checkRules = Lists.newArrayList("XML version specified", "Unique attribute");
        assertThat(lintRules.getOnlyRules(checkRules)).hasSize(2);
    }

    @Test
    public void testInvalidLintRulesGetsIgnored() {
        List<String> badRules = Lists.newArrayList("foo", "foobar", "bar");

        assertThat(lintRules.getOnlyRules(badRules)).isEmpty();
        assertThat(lintRules.getAllEnabledRulesAsWellAs(badRules)).hasSameSizeAs(lintRules.getAllEnabledRules());
    }

    @Test
    public void testBadCategoriesGetIgnored() {
        List<String> badCategories = Lists.newArrayList("hoo", "haw");

        assertThat(lintRules.getRulesWithCategoryNames(badCategories)).isEmpty();
    }

    @Test
    public void testGetCategoriesReturnsRightAmountOfRules() {
        List<String> categories = Lists.newArrayList("LINT");

        assertThat(lintRules.getRulesWithCategoryNames(categories)).hasSize(2);
    }
}
