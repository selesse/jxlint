package com.selesse.jxlint.maven;

import com.selesse.jxlint.model.AbstractDispatcher;
import com.selesse.jxlint.model.ExitException;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.settings.ProgramSettings;

import org.apache.maven.plugin.MojoExecutionException;

public class MojoDispatcher extends AbstractDispatcher {

    MojoDispatcher(ProgramOptions programOptions, ProgramSettings programSettings) {
        super(programOptions, programSettings);
    }

    void dispatch() throws MojoExecutionException {
        try {
            doDispatch();
        }
        catch (ExitException e) {
            throw new MojoExecutionException(e.getMessage());
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
