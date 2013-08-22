package com.selesse.jxlint.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

public class LintRule {
    private String name;
    private String summary;
    private String detailedDescription;
    private Severity severity;
    private Category category;
    private boolean enabled = true;

    public LintRule(String name, String summary, String detailedDescription, Severity severity, Category category) {
        this.name = name;
        this.summary = summary;
        this.detailedDescription = detailedDescription;
        this.severity = severity;
        this.category = category;
    }

    public LintRule(String name, String summary, String detailedDescription, Severity severity, Category category,
                    boolean isEnabledByDefault) {
        this(name, summary, detailedDescription, severity, category);
        this.enabled = isEnabledByDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSummaryOutput() {
        return String.format("\"%s\" : %s", getName(), getSummary());
    }

    public String getDetailedOutput() {
        List<String> detailedOutput = Lists.newArrayList(
                getName(),
                new String(new char[getName().length()]).replace("\0", "-"),
                "Summary: " + getSummary(),
                "",
                "Severity: " + getSeverity(),
                "",
                getDetailedDescription()
        );

        return Joiner.on("\n").join(detailedOutput);
    }
}
