package com.selesse.jxlint.gradle;

import com.selesse.jxlint.model.AbstractDispatcher;
import com.selesse.jxlint.model.ExitException;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.settings.ProgramSettings;

import org.gradle.api.GradleScriptException;

public class GradleDispatcher extends AbstractDispatcher {

    GradleDispatcher(ProgramOptions programOptions, ProgramSettings programSettings) {
        super(programOptions, programSettings);
    }

    void dispatch() {
        try {
            doDispatch();
        }
        catch (ExitException e) {
            throw new GradleScriptException(e.getMessage(), e);
        }
    }

    @Override
    protected String createHelpMessage(ProgramSettings settings) {
        return "";
    }

    @Override
    protected void startWebServer() {
    }
}
