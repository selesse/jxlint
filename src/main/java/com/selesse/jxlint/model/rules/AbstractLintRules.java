package com.selesse.jxlint.model.rules;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.ProgramOptions;

import java.util.Collections;
import java.util.List;

/**
 * Abstract {@link LintRules}. Provides most of the implementation that should be expected from {@link LintRules}.
 * All the methods (except {@link #getLintRule(String)}) assume that the elements are valid. In other words,
 * calling <code> getAllRulesExcept( { "rule1", "rule2" } ) </code> assumes that these rules do exist and will fail
 * silently if they don't.
 *
 * <p>
 * The program is expected to fail if these rules don't exist, specifically in
 * {@link ProgramOptions#getListFromRawOptionStringOrDie(String)}.
 * </p>
 */
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

    @Override
    public List<LintRule> getAllEnabledRulesAsWellAs(List<String> enabledRulesList) {
        if (enabledRulesList.size() == 0) {
            return getAllEnabledRules();
        }

        List<LintRule> bloatedLintRules = getAllEnabledRules();

        for (String enabledLintString : enabledRulesList) {
            try {
                LintRule lintRule = getLintRule(enabledLintString);
                bloatedLintRules.add(lintRule);
            } catch (NonExistentLintRuleException e) {
                // do nothing, we assume these are already all validated
            }
        }

        return Collections.unmodifiableList(bloatedLintRules);
    }

    @Override
    public List<LintRule> getOnlyRules(List<String> checkRules) {
        List<LintRule> filteredLintRules = Lists.newArrayList();

        for (String enabledLintString : checkRules) {
            try {
                LintRule lintRule = getLintRule(enabledLintString);
                filteredLintRules.add(lintRule);
            } catch (NonExistentLintRuleException e) {
                // do nothing, we assume these are already all validated
            }
        }

        return Collections.unmodifiableList(filteredLintRules);
    }

    @Override
    public List<LintRule> getAllRulesWithSeverity(Severity severity) {
        List<LintRule> severityRules = Lists.newArrayList();

        for (LintRule lintRule : getAllRules()) {
            if (lintRule.getSeverity() == severity) {
                severityRules.add(lintRule);
            }
        }

        return severityRules;
    }
}
