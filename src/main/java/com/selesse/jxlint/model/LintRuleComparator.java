package com.selesse.jxlint.model;

import com.google.common.primitives.Ints;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;

public class LintRuleComparator {
    public static int compareCategoryThenName(LintRule lintRule, LintRule lintRule2) {
        Enum<?> firstCategory = lintRule.getCategory();
        Enum<?> secondCategory = lintRule2.getCategory();

        if (firstCategory == secondCategory) {
            return lintRule.getName().compareTo(lintRule2.getName());
        }

        return firstCategory.toString().compareToIgnoreCase(secondCategory.toString());
    }

    public static int compareLineNumbers(LintError o1, LintError o2) {
        return Ints.compare(o1.getLineNumber(), o2.getLineNumber());
    }
}
