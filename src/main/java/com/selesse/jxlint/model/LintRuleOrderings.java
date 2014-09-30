package com.selesse.jxlint.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.selesse.jxlint.model.rules.LintRule;

public class LintRuleOrderings {

    private static final Ordering<LintRule> categoryThenName = new Ordering<LintRule>() {
        @Override
        public int compare(LintRule left, LintRule right) {
            return ComparisonChain.start()
                    .compare(left.getCategory().toString(), right.getCategory().toString())
                    .compare(left.getName(), right.getName())
                    .result();
        }
    };

    public static Ordering<LintRule> getCategoryThenNameOrdering() {
        return categoryThenName;
    }
}
