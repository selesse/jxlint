package com.selesse.jxlint.settings;

public class JxlintProgramSettings implements ProgramSettings {
    @Override
    public String getProgramVersion() {
        return "1.0.0-SNAPSHOT";
    }

    @Override
    public String getProgramName() {
        return "jxlint";
    }
}
