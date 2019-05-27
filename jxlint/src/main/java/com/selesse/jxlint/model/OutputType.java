package com.selesse.jxlint.model;

/**
 * All possible ways to report our {@link com.selesse.jxlint.model.rules.LintError}s.
 */
public enum OutputType {
    DEFAULT(""), QUIET(""), JENKINS_XML("jenkins-xml"), XML("xml"), HTML("html");

    private final String extension;

    OutputType(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }
}
