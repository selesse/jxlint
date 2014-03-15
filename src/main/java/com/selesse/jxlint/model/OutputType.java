package com.selesse.jxlint.model;

public enum OutputType {
    DEFAULT(""), QUIET(""), XML("xml"), HTML("html");

    private final String extension;

    OutputType(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }
}
