package com.selesse.jxlint.maven;

import com.selesse.jxlint.settings.ProgramSettings;

import java.io.File;

/**
 * Our implementation of {@link com.selesse.jxlint.settings.ProgramSettings}. Used for testing.
 */
public class SampleProgramSettings implements ProgramSettings {
    @Override
    public String getProgramVersion() {
        return "1.0.0";
    }

    @Override
    public String getProgramName() {
        return "sample-jxlint";
    }

    @Override
    public void initializeForWeb(File projectRoot) {
    }
}
