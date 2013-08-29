package com.selesse.jxlint.cli;

import com.selesse.jxlint.model.ProgramOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.List;

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

        @SuppressWarnings("unchecked") List<String> argList = commandLine.getArgList();

        if (argList.size() > 0) {
            programOptions.setSourceDirectory(argList.get(0));
        }

        return programOptions;
    }

}
