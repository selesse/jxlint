package com.selesse.jxlint.settings;

/**
 * Our implementation of {@link com.selesse.jxlint.settings.ProgramSettings}. Used for testing.
 */
public class JxlintProgramSettings implements ProgramSettings {
    @Override
    public String getProgramVersion() {
        return "1.1.1";
    }

    @Override
    public String getProgramName() {
        return "jxlint";
    }
}
