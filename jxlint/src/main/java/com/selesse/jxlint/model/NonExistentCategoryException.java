package com.selesse.jxlint.model;

import com.google.common.base.Joiner;

/**
 * Specific {@link java.lang.Exception} for the case where a category was queried but did not exist.
 */
public class NonExistentCategoryException extends Exception {
    private static final long serialVersionUID = 1L;
    private String categoryName;

    public NonExistentCategoryException(String categoryName, Iterable<String> categories) {
        super("Category '" + categoryName + "' does not exist. Try one of: " +
                Joiner.on(", ").join(categories) + ".");
        this.categoryName = categoryName;
    }

    /**
     * Returns the category name that was queried but wasn't found.
     */
    public String getRuleName() {
        return categoryName;
    }
}
