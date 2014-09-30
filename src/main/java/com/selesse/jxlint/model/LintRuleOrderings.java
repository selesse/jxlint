package com.selesse.jxlint.model;

import com.selesse.jxlint.model.rules.LintRule;

public class LintRuleOrderings {
    public static int compareCategoryThenName(LintRule lintRule, LintRule lintRule2) {
        Enum<?> firstCategory = lintRule.getCategory();
        Enum<?> secondCategory = lintRule2.getCategory();

        if (firstCategory == secondCategory) {
            return lintRule.getName().compareTo(lintRule2.getName());
        }

        return firstCategory.toString().compareToIgnoreCase(secondCategory.toString());
    }

}
