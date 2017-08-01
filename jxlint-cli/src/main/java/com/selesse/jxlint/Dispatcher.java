package com.selesse.jxlint;

import com.google.common.annotations.VisibleForTesting;
import com.selesse.jxlint.actions.web.JettyWebRunner;
import com.selesse.jxlint.cli.CommandLineOptions;
import com.selesse.jxlint.model.AbstractDispatcher;
import com.selesse.jxlint.model.ExitException;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.settings.ProgramSettings;

/**
 * The Dispatcher guides the command line application's logic flow. It looks at the options provided in
 * {@link com.selesse.jxlint.model.ProgramOptions} and decides what objects, functions, etc. to call based on the
 * options. (see the principal method {@link Dispatcher#dispatch()}).
 */
class Dispatcher extends AbstractDispatcher {

    Dispatcher(ProgramOptions programOptions, ProgramSettings programSettings) {
        super(programOptions, programSettings);
    }

    void dispatch() {
        try {
            doDispatch();
        }
        catch (ExitException e) {
            ProgramExitter.exitProgramWithMessage(e.getMessage(), e.getExitType());
        }
    }

    @Override
    protected String createHelpMessage(ProgramSettings settings) {
        return CommandLineOptions.getHelpMessage(settings);
    }

    @Override
    protected void startWebServer() {
        String port = programOptions.getOption(JxlintOption.WEB);

        JettyWebRunner jettyWebRunner = getJettyWebRunner(port);
        jettyWebRunner.start();
    }

    @VisibleForTesting
    JettyWebRunner getJettyWebRunner(String port) {
        return new JettyWebRunner(programSettings, port);
    }
}
