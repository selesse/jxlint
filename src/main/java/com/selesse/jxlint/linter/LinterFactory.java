package com.selesse.jxlint.linter;

import com.selesse.jxlint.model.rules.LintRule;

import java.util.List;

/**
 * A factory/singleton hybrid. Used for accessing {@link com.selesse.jxlint.linter.Linter}s as global variables.
 * This is particularly useful when we're performing unit tests and want to get the results of a lint validation
 * without passing around and returning tons of objects.
 */
public class LinterFactory {
    private static Linter instance;

    /**
     * Creates a new {@link Linter} based on a {@link java.util.List} of {@link LintRule}s.
     */
    public static Linter createNewLinter(List<LintRule> rules) {
        instance = new Linter(rules);
        return instance;
    }

    public static Linter getInstance() {
        return instance;
    }
}
