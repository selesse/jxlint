package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;

public class Dispatcher {
    public static void dispatch(ProgramOptions programOptions) {
        if (programOptions.isHelpEnabled()) {
            Main.exitProgram(programOptions.getHelpMessage(), ExitType.SUCCESS);
        }
        else if (programOptions.isVersionEnabled()) {
            Main.exitProgram(Main.programName + ": version " + Main.getProgramVersion(), ExitType.SUCCESS);
        }
        else {
            Main.exitProgram("Unable to parse program options.", ExitType.COMMAND_LINE_ERROR);
        }
    }
}
