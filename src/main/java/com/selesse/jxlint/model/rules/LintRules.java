package com.selesse.jxlint.model.rules;

import java.util.List;

public interface LintRules {
    LintRule getLintRule(String ruleName) throws NonExistentLintRuleException;
    List<LintRule> getAllRules();
    List<LintRule> getAllEnabledRules();
    List<LintRule> getAllRulesExcept(List<String> disabledRules);
    List<LintRule> getAllEnabledRulesExcept(List<String> disabledRules);
    List<LintRule> getAllEnabledRulesAsWellAs(List<String> enabledRulesList);
    List<LintRule> getOnlyRules(List<String> checkRulesList);
}
