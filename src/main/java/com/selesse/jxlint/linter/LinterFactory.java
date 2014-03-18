package com.selesse.jxlint.linter;

import com.selesse.jxlint.model.rules.LintRule;

import java.util.List;

public class LinterFactory {
    private static Linter instance;

    public static Linter createNewLinter(List<LintRule> rules) {
        instance = new LinterImpl(rules);
        return instance;
    }

    public static Linter getInstance() {
        return instance;
    }
}
