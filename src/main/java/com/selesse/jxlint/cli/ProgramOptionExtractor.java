package com.selesse.jxlint.cli;

import com.selesse.jxlint.model.ProgramOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class ProgramOptionExtractor {
    public static ProgramOptions extractProgramOptions(CommandLine commandLine, Options options) {
        ProgramOptions programOptions = new ProgramOptions();
        programOptions.setHelpMessage(CLIHelpMessage.getMessage(options));

        if (commandLine.hasOption("help")) {
            programOptions.addOption("help");
        }
        if (commandLine.hasOption("version")) {
            programOptions.addOption("version");
        }
        if (commandLine.hasOption("list")) {
            programOptions.addOption("list");
        }
        if (commandLine.hasOption("show")) {
            programOptions.addOption("show", commandLine.getOptionValue("show"));
        }

        return programOptions;
    }

}
