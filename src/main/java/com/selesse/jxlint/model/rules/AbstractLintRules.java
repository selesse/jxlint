package com.selesse.jxlint.model.rules;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public abstract class AbstractLintRules implements LintRules {
    protected List<LintRule> lintRules;

    public AbstractLintRules() {
        this.lintRules = Lists.newArrayList();
        initializeLintTasks();
    }

    public abstract void initializeLintTasks();

    @Override
    public LintRule getLintRule(String ruleName) throws NonExistentLintRuleException {
        LintRule lintRule = null;

        for (LintRule rule : lintRules) {
            if (rule.getName().equalsIgnoreCase(ruleName)) {
                lintRule = rule;
                break;
            }
        }

        if (lintRule == null) {
            throw new NonExistentLintRuleException(ruleName);
        }

        return lintRule;
    }

    @Override
    public List<LintRule> getAllRules() {
        return Collections.unmodifiableList(lintRules);
    }

    @Override
    public List<LintRule> getAllEnabledRules() {
        List<LintRule> filteredLintRules = Lists.newArrayList();

        for (LintRule lintRule : getAllRules()) {
            if (lintRule.isEnabled()) {
                filteredLintRules.add(lintRule);
            }
        }

        return filteredLintRules;
    }

    @Override
    public List<LintRule> getAllRulesExcept(List<String> disabledRules) {
        if (disabledRules.size() == 0) {
            return getAllRules();
        }

        List<LintRule> filteredLintRules = Lists.newArrayList();

        for (LintRule lintRule : getAllRules()) {
            if (!disabledRules.contains(lintRule)) {
                filteredLintRules.add(lintRule);
            }
        }

        return Collections.unmodifiableList(filteredLintRules);
    }

    @Override
    public List<LintRule> getAllEnabledRulesExcept(List<String> disabledRules) {
        if (disabledRules.size() == 0) {
            return getAllEnabledRules();
        }

        List<LintRule> filteredLintRules = Lists.newArrayList();

        for (LintRule lintRule : getAllEnabledRules()) {
            if (!disabledRules.contains(lintRule)) {
                filteredLintRules.add(lintRule);
            }
        }

        return Collections.unmodifiableList(filteredLintRules);
    }
}
