package com.selesse.jxlintimpl.settings;

import com.selesse.jxlint.settings.ProgramSettings;

import java.io.File;

public class JxlintImplProgramSettings implements ProgramSettings {
    @Override
    public String getProgramVersion() {
        return "0.1.0";
    }

    @Override
    public String getProgramName() {
        return "jxlint-impl";
    }

    @Override
    public void initializeForWeb(File projectRoot) {

    }
}
