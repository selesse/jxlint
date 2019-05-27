package com.selesse.jxlint.cli;

import com.google.common.annotations.VisibleForTesting;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.ProgramOptions;
import org.apache.commons.cli.CommandLine;

import java.util.List;

/**
 * Class for extracting program options from {@link CommandLine} and {@link org.apache.commons.cli.Options}.
 * A wrapper so that if the CLI parser changes, code modifications will be limited to this class.
 */
public class ProgramOptionExtractor {
    public static final String DEFAULT_PORT = "8380";
    @VisibleForTesting
    static final String HTML_OPTION = "html";
    @VisibleForTesting
    static final String XML_OPTION = "xml";
    @VisibleForTesting
    static final String JENKINS_XML_OPTION = "jenkins-xml";
    @VisibleForTesting
    static final String QUIET_OPTION = "quiet";

    public static ProgramOptions extractProgramOptions(CommandLine commandLine) {
        ProgramOptions programOptions = new ProgramOptions();

        if (commandLine.hasOption(JxlintOption.HELP.getOptionString())) {
            programOptions.addOption(JxlintOption.HELP);
        }
        if (commandLine.hasOption(JxlintOption.VERSION.getOptionString())) {
            programOptions.addOption(JxlintOption.VERSION);
        }
        if (commandLine.hasOption(JxlintOption.PROFILE.getOptionString())) {
            programOptions.addOption(JxlintOption.PROFILE);
        }
        if (commandLine.hasOption(JxlintOption.LIST.getOptionString())) {
            programOptions.addOption(JxlintOption.LIST);
        }
        if (commandLine.hasOption(JxlintOption.WEB.getOptionString())) {
            String port = commandLine.getOptionValue(JxlintOption.WEB.getOptionString(), DEFAULT_PORT);
            programOptions.addOption(JxlintOption.WEB, port);
        }
        if (commandLine.hasOption(JxlintOption.REPORT_RULES.getOptionString())) {
            programOptions.addOption(JxlintOption.REPORT_RULES);
        }
        if (commandLine.hasOption(JxlintOption.SHOW.getOptionString())) {
            programOptions.addOption(JxlintOption.SHOW,
                    commandLine.getOptionValue(JxlintOption.SHOW.getOptionString()));
        }
        if (commandLine.hasOption(JxlintOption.DISABLE.getOptionString())) {
            programOptions.addOption(JxlintOption.DISABLE,
                    commandLine.getOptionValue(JxlintOption.DISABLE.getOptionString()));
        }
        if (commandLine.hasOption(JxlintOption.ENABLE.getOptionString())) {
            programOptions.addOption(JxlintOption.ENABLE,
                    commandLine.getOptionValue(JxlintOption.ENABLE.getOptionString()));
        }
        if (commandLine.hasOption(JxlintOption.CATEGORY.getOptionString())) {
            programOptions.addOption(JxlintOption.CATEGORY,
                    commandLine.getOptionValue(JxlintOption.CATEGORY.getOptionString()));
        }
        if (commandLine.hasOption(JxlintOption.CHECK.getOptionString())) {
            programOptions.addOption(JxlintOption.CHECK,
                    commandLine.getOptionValue(JxlintOption.CHECK.getOptionString()));
        }
        if (commandLine.hasOption(JxlintOption.NO_WARNINGS.getOptionString())) {
            programOptions.addOption(JxlintOption.NO_WARNINGS);
        }
        if (commandLine.hasOption(JxlintOption.ALL_WARNINGS.getOptionString())) {
            programOptions.addOption(JxlintOption.ALL_WARNINGS);
        }
        if (commandLine.hasOption(JxlintOption.WARNINGS_ARE_ERRORS.getOptionString())) {
            programOptions.addOption(JxlintOption.WARNINGS_ARE_ERRORS);
        }
        if (commandLine.hasOption(QUIET_OPTION)) {
            programOptions.addOption(JxlintOption.OUTPUT_TYPE, QUIET_OPTION);
        }
        if (commandLine.hasOption(HTML_OPTION)) {
            programOptions.addOption(JxlintOption.OUTPUT_TYPE, HTML_OPTION);
            String outputHtmlFile = commandLine.getOptionValue(HTML_OPTION);
            if (outputHtmlFile != null) {
                if (!outputHtmlFile.endsWith(".html")) {
                    outputHtmlFile += ".html";
                }
                programOptions.addOption(JxlintOption.OUTPUT_TYPE_PATH, outputHtmlFile);
            }
        }
        if (commandLine.hasOption(XML_OPTION)) {
            programOptions.addOption(JxlintOption.OUTPUT_TYPE, XML_OPTION);
            String outputXmlFile = commandLine.getOptionValue(XML_OPTION);
            if (outputXmlFile != null) {
                if (!outputXmlFile.endsWith(".xml")) {
                    outputXmlFile += ".xml";
                }
                programOptions.addOption(JxlintOption.OUTPUT_TYPE_PATH, outputXmlFile);
            }
        }
        if (commandLine.hasOption(JENKINS_XML_OPTION)) {
            programOptions.addOption(JxlintOption.OUTPUT_TYPE, JENKINS_XML_OPTION);
            String outputXmlFile = commandLine.getOptionValue(XML_OPTION);
            if (outputXmlFile != null) {
                if (!outputXmlFile.endsWith(".xml")) {
                    outputXmlFile += ".xml";
                }
                programOptions.addOption(JxlintOption.OUTPUT_TYPE_PATH, outputXmlFile);
            }
        }
        if (commandLine.hasOption(JxlintOption.SRC_PATH_PREFIX.getOptionString())) {
            programOptions.addOption(JxlintOption.SRC_PATH_PREFIX,
                    commandLine.getOptionValue(JxlintOption.SRC_PATH_PREFIX.getOptionString()));
        }

        List<String> argList = commandLine.getArgList();

        if (argList.size() > 0) {
            programOptions.setSourceDirectory(argList.get(0));
        }

        return programOptions;
    }

}
