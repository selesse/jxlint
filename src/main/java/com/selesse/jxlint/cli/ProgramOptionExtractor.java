package com.selesse.jxlint.cli;

import com.selesse.jxlint.model.ProgramOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class ProgramOptionExtractor {
    public static ProgramOptions extractProgramOptions(CommandLine commandLine, Options options) {
        ProgramOptions programOptions = new ProgramOptions();
        programOptions.setHelpMessage(CLIHelpMessage.getMessage(options));

        if (commandLine.hasOption("help")) {
            programOptions.setHelpEnabled(true);
        }
        if (commandLine.hasOption("version")) {
            programOptions.setVersionEnabled(true);
        }

        return programOptions;
    }

}
