package com.selesse.jxlint.settings;

import java.io.File;

/**
 * Our implementation of {@link com.selesse.jxlint.settings.ProgramSettings}. Used for testing.
 */
public class JxlintProgramSettings implements ProgramSettings {
    @Override
    public String getProgramVersion() {
        return "1.3.0";
    }

    @Override
    public String getProgramName() {
        return "jxlint";
    }

    @Override
    public void initializeForWeb(File projectRoot) {}
}
