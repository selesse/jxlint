package com.selesse.jxlint.model.rules;

import com.google.common.collect.Lists;
import com.selesse.jxlint.samplerules.xml.LintRulesTestImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AbstractLintRulesTest {
    private LintRules lintRules = new LintRulesTestImpl();

    @Test
    public void testLintRulesGetsAllRuleSizeProperly() {
        assertEquals(3, lintRules.getAllRules().size());
    }

    @Test
    public void testLintRulesGetsAllEnabledRulesSizeProperly() {
        assertEquals(2, lintRules.getAllEnabledRules().size());
    }

    @Test
    public void testLintRulesGetsAllDisabledRulesSizeProperly() {
        List<String> disabledRules = Lists.newArrayList("XML version specified");
        assertEquals(2, lintRules.getAllRulesExcept(disabledRules).size());
    }

    @Test
    public void testLintRulesGetsAllEnabledMinusDisabledRulesSizeProperly() {
        List<String> disabledRules = Lists.newArrayList("Unique attribute");
        assertEquals(1, lintRules.getAllEnabledRulesExcept(disabledRules).size());
    }

    @Test
    public void testLintRulesGetsAllEnabledPlusEnabledRulesSizeProperly() {
        List<String> enabledRules = Lists.newArrayList("XML version specified");
        assertEquals(3, lintRules.getAllEnabledRulesAsWellAs(enabledRules).size());
    }

    @Test
    public void testGetAllRulesBySeverity() {
        List<LintRule> emptyErrorRules = lintRules.getAllRulesWithSeverity(Severity.ERROR);
        assertEquals(0, emptyErrorRules.size());

        List<LintRule> fatalRules = lintRules.getAllRulesWithSeverity(Severity.FATAL);
        assertEquals(1, fatalRules.size());

        List<LintRule> warningRules = lintRules.getAllRulesWithSeverity(Severity.WARNING);
        assertEquals(2, warningRules.size());
    }

    @Test
    public void testLintRulesGetsCheckRuleProperly() {
        List<String> checkRules = Lists.newArrayList("XML version specified");
        assertEquals(1, lintRules.getOnlyRules(checkRules).size());
        assertEquals("XML version specified", lintRules.getOnlyRules(checkRules).get(0).getName());
    }

    @Test
    public void testLintRulesGetsMultipleCheckRulesProperly() {
        List<String> checkRules = Lists.newArrayList("XML version specified", "Unique attribute");
        assertEquals(2, lintRules.getOnlyRules(checkRules).size());
    }

    @Test
    public void testInvalidLintRulesGetsIgnored() {
        List<String> badRules = Lists.newArrayList("foo", "foobar", "bar");

        assertEquals(0, lintRules.getOnlyRules(badRules).size());
        assertEquals(lintRules.getAllEnabledRules().size(), lintRules.getAllEnabledRulesAsWellAs(badRules).size());
    }
}
