package com.selesse.jxlint.model;

import com.google.common.primitives.Ints;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;

public class LintErrorOrderings {
    public static int compareLineNumbers(LintError o1, LintError o2) {
        return Ints.compare(o1.getLineNumber(), o2.getLineNumber());
    }

    public static int compareByCategoryNameThenFileThenLineNumber(LintError o1, LintError o2) {
        LintRule firstRule = o1.getViolatedRule();
        LintRule secondRule = o2.getViolatedRule();

        int categoryThenName = LintRuleOrderings.compareCategoryThenName(firstRule, secondRule);

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
