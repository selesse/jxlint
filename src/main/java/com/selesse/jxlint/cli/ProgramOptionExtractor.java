package com.selesse.jxlint.cli;

import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.ProgramOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.List;

/**
 * Class for extracting program options from {@link CommandLine} and {@link Options}.
 * A wrapper so that if the CLI parser changes, code modifications will be limited to this class.
 */
public class ProgramOptionExtractor {
    public static ProgramOptions extractProgramOptions(CommandLine commandLine) {
        ProgramOptions programOptions = new ProgramOptions();

        if (commandLine.hasOption("help")) {
            programOptions.addOption(JxlintOption.HELP);
        }
        if (commandLine.hasOption("version")) {
            programOptions.addOption(JxlintOption.VERSION);
        }
        if (commandLine.hasOption("list")) {
            programOptions.addOption(JxlintOption.LIST);
        }
        if (commandLine.hasOption("show")) {
            programOptions.addOption(JxlintOption.SHOW, commandLine.getOptionValue("show"));
        }
        if (commandLine.hasOption("disable")) {
            programOptions.addOption(JxlintOption.DISABLE, commandLine.getOptionValue("disable"));
        }
        if (commandLine.hasOption("enable")) {
            programOptions.addOption(JxlintOption.ENABLE, commandLine.getOptionValue("enable"));
        }
        if (commandLine.hasOption("check")) {
            programOptions.addOption(JxlintOption.CHECK, commandLine.getOptionValue("check"));
        }
        if (commandLine.hasOption("nowarn")) {
            programOptions.addOption(JxlintOption.NO_WARNINGS);
        }
        if (commandLine.hasOption("Wall")) {
            programOptions.addOption(JxlintOption.ALL_WARNINGS);
        }
        if (commandLine.hasOption("Werror")) {
            programOptions.addOption(JxlintOption.WARNINGS_ARE_ERRORS);
        }
        if (commandLine.hasOption("quiet")) {
            programOptions.addOption(JxlintOption.OUTPUT_TYPE, "quiet");
        }
        if (commandLine.hasOption("html")) {
            programOptions.addOption(JxlintOption.OUTPUT_TYPE, "html");
            if (commandLine.getOptionValue("html") != null) {
                programOptions.addOption(JxlintOption.OUTPUT_TYPE_PATH, commandLine.getOptionValue("html"));
            }
        }
        if (commandLine.hasOption("xml")) {
            programOptions.addOption(JxlintOption.OUTPUT_TYPE, "xml");
            if (commandLine.getOptionValue("xml") != null) {
                programOptions.addOption(JxlintOption.OUTPUT_TYPE_PATH, commandLine.getOptionValue("xml"));
            }
        }

        @SuppressWarnings("unchecked") List<String> argList = commandLine.getArgList();

        if (argList.size() > 0) {
            programOptions.setSourceDirectory(argList.get(0));
        }

        return programOptions;
    }

}
