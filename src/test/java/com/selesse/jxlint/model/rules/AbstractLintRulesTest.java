package com.selesse.jxlint.model.rules;

import com.google.common.collect.Lists;
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
}
