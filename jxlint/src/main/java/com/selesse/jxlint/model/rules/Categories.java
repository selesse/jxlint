package com.selesse.jxlint.model.rules;

public class Categories {
    private static Class<? extends Enum<?>> categories;

    public static Class<? extends Enum<?>> get() {
        if (categories == null) {
            categories = Category.class;
        }
        return categories;
    }

    public static void setCategories(Class<? extends Enum<?>> categories) {
        Categories.categories = categories;
    }
}
