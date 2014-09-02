package com.selesse.jxlint.model.rules;

/**
 * A {@link com.selesse.jxlint.model.rules.LintRule} category list.
 */
public enum Category {
    /**
     * Catch-all category for other categories that don't fit.
     */
    LINT,
    /**
     * If a rule violates "correctness", it means that there is possibly unintended, incorrect behavior.
     */
    CORRECTNESS,
    /**
     * Validations that give suggestions to improve performance.
     */
    PERFORMANCE,
    /**
     * Security-related validations.
     */
    SECURITY,
    /**
     * Validations relating to style. Could be code style, trailing whitespace, line endings, or anything
     * else that's style-related.
     */
    STYLE
}
