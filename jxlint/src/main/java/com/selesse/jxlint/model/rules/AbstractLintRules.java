package com.selesse.jxlint.model.rules;

import com.google.common.collect.Lists;
import com.selesse.jxlint.utils.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
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
 * {@link com.selesse.jxlint.model.ProgramOptions#getRuleListFromOptionString(String)}.
 * </p>
 */
public abstract class AbstractLintRules implements LintRules {
    protected List<LintRule> lintRules;
    /**
     * The source/root directory in which we will be performing all validations.
     */
    protected File sourceDirectory;

    public AbstractLintRules() {
        this.lintRules = Lists.newArrayList();
        initializeLintRules();
    }

    /**
     * Add all desired {@link LintRule}s to {@link #lintRules}.
     */
    public abstract void initializeLintRules();

    @Override
    @Nonnull
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
        return Collections.unmodifiableList(getModifiableAllEnabledRules());
    }

    private List<LintRule> getModifiableAllEnabledRules() {
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
            if (!lintRule.hasNameInList(disabledRules)) {
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
            if (!lintRule.hasNameInList(disabledRules)) {
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

        List<LintRule> bloatedLintRules = getModifiableAllEnabledRules();

        for (String enabledLintString : enabledRulesList) {
            try {
                LintRule lintRule = getLintRule(enabledLintString);
                bloatedLintRules.add(lintRule);
            }
            catch (NonExistentLintRuleException e) {
                // do nothing, we assume these are already all validated
            }
        }

        return Collections.unmodifiableList(bloatedLintRules);
    }

    @Override
    public List<LintRule> getRulesWithCategoryNames(List<String> categoryNames) {
        List<LintRule> categoryRules = Lists.newArrayList();

        List<LintRule> lintRules = getAllEnabledRules();

        for (LintRule lintRule : lintRules) {
            for (String categoryName : categoryNames) {
                if (lintRule.getCategory().toString().equalsIgnoreCase(categoryName)) {
                    categoryRules.add(lintRule);
                }
            }
        }

        return categoryRules;
    }

    @Override
    public List<LintRule> getOnlyRules(List<String> rulesList) {
        List<LintRule> filteredLintRules = Lists.newArrayList();

        for (String ruleName : rulesList) {
            try {
                LintRule lintRule = getLintRule(ruleName);
                filteredLintRules.add(lintRule);
            }
            catch (NonExistentLintRuleException e) {
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

        return Collections.unmodifiableList(severityRules);
    }

    @Override
    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = FileUtils.normalizeFile(sourceDirectory);
    }

    @Override
    public File getSourceDirectory() {
        return sourceDirectory;
    }
}
