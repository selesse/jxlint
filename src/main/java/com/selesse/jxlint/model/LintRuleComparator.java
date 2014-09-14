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

    public static int compareByCategoryNameThenFileThenLineNumber(LintError o1, LintError o2) {
        LintRule firstRule = o1.getViolatedRule();
        LintRule secondRule = o2.getViolatedRule();

        int categoryThenName = compareCategoryThenName(firstRule, secondRule);

        if (categoryThenName == 0) {
            int fileCompare = o1.getFile().compareTo(o2.getFile());
            if (fileCompare == 0) {
                return compareLineNumbers(o1, o2);
            }
            return fileCompare;
        }

        return categoryThenName;
    }
}
