package com.selesse.jxlint.cli;

import com.selesse.jxlint.model.OutputType;
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
        if (commandLine.hasOption("disable")) {
            programOptions.addOption("disable", commandLine.getOptionValue("disable"));
        }
        if (commandLine.hasOption("enable")) {
            programOptions.addOption("enable", commandLine.getOptionValue("enable"));
        }
        if (commandLine.hasOption("check")) {
            programOptions.addOption("check", commandLine.getOptionValue("check"));
        }
        if (commandLine.hasOption("nowarn")) {

        }
        if (commandLine.hasOption("Wall")) {

        }
        if (commandLine.hasOption("Werror")) {

        }
        if (commandLine.hasOption("quiet")) {
            programOptions.addOption("outputType", "quiet");
        }
        if (commandLine.hasOption("html")) {
            programOptions.addOption("outputType", "html");
        }
        if (commandLine.hasOption("xml")) {
            programOptions.addOption("outputType", "xml");
        }

        @SuppressWarnings("unchecked") List<String> argList = commandLine.getArgList();

        if (argList.size() > 0) {
            programOptions.setSourceDirectory(argList.get(0));
        }

        return programOptions;
    }

}
