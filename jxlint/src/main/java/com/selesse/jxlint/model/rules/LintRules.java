package com.selesse.jxlint.model.rules;

import java.io.File;
import java.util.List;

/**
 * Interface specification of a container of list rules. Has utility methods to deal with various
 * {@link LintRule} configurations.
 */
public interface LintRules {
    /**
     * Get the {@link LintRule} object that matches the given rule name. Throw a
     * {@link com.selesse.jxlint.model.rules.NonExistentLintRuleException} if such a rule doesn't exist.
     */
    LintRule getLintRule(String ruleName) throws NonExistentLintRuleException;

    /**
     * Return every single rule, including ones off by default.
     */
    List<LintRule> getAllRules();

    /**
     * Get all rules enabled by default.
     */
    List<LintRule> getAllEnabledRules();

    List<LintRule> getAllRulesExcept(List<String> disabledRules);

    List<LintRule> getAllEnabledRulesExcept(List<String> disabledRules);

    List<LintRule> getAllEnabledRulesAsWellAs(List<String> enabledRulesList);

    List<LintRule> getRulesWithCategoryNames(List<String> enabledCategoriesList);

    List<LintRule> getOnlyRules(List<String> rulesList);

    List<LintRule> getAllRulesWithSeverity(Severity severity);

    /**
     * Set the source/root directory. This is the directory that was passed to the program,
     * i.e. "java -jar myjar.jar sourceDirectory".
     */
    void setSourceDirectory(File sourceDirectory);

    /**
     * Get the source/root directory. This is the directory that was passed to the program,
     * i.e. "java -jar myjar.jar sourceDirectory".
     */
    File getSourceDirectory();
}
