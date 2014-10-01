package com.selesse.jxlintimpl;

public enum CustomCategories {
    PROBABLY_ACCIDENT("Probably An Accident"),
    ;

    private final String displayName;

    CustomCategories(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

}
