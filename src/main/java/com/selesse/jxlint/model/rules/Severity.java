package com.selesse.jxlint.model.rules;

import com.selesse.jxlint.report.color.Color;

@SuppressWarnings("unused")
public enum Severity {
    WARNING(Color.YELLOW),
    ERROR(Color.BLUE),
    FATAL(Color.RED);

    private Color severityColor;

    private Severity(Color color) {
        this.severityColor = color;
    }

    public Color getColor() {
        return this.severityColor;
    }
}
