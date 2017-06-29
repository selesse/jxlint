package com.selesse.jxlint;

import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.settings.ProgramSettings;

//TODO: This class only makes the com.selesse.jxlint.Dispatcher class accessible. To be discussed in #15
//      See https://github.com/selesse/jxlint/issues/15

public class MojoDispatcher extends Dispatcher {

    public MojoDispatcher(ProgramOptions programOptions, ProgramSettings programSettings) {
        super(programOptions, programSettings);
    }

    @Override
    public void dispatch() {
        super.dispatch();
    }
}
