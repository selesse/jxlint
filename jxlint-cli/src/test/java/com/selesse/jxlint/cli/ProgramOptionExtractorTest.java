package com.selesse.jxlint.cli;

import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.ProgramOptions;
import org.apache.commons.cli.CommandLine;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ProgramOptionExtractorTest {
    @Test
    public void testProgramOptionExtractor_appendsHtmlWhenNeeded() {
        CommandLine commandLineMock = getCommandLineFixture(ProgramOptionExtractor.HTML_OPTION, "index");

        ProgramOptions programOptions = ProgramOptionExtractor.extractProgramOptions(commandLineMock);

        assertThat(programOptions.hasOption(JxlintOption.OUTPUT_TYPE)).isTrue();
        assertThat(programOptions.getOption(JxlintOption.OUTPUT_TYPE_PATH)).isEqualTo("index.html");
    }

    @Test
    public void testProgramOptionExtractor_doesNotModifyHtmlPaths() {
        CommandLine commandLineMock = getCommandLineFixture(ProgramOptionExtractor.HTML_OPTION, "index.html");

        ProgramOptions programOptions = ProgramOptionExtractor.extractProgramOptions(commandLineMock);

        assertThat(programOptions.hasOption(JxlintOption.OUTPUT_TYPE)).isTrue();
        assertThat(programOptions.getOption(JxlintOption.OUTPUT_TYPE_PATH)).isEqualTo("index.html");
    }

    @Test
    public void testProgramOptionExtractor_appendsXmlWhenNeeded() {
        CommandLine commandLineMock = getCommandLineFixture(ProgramOptionExtractor.XML_OPTION, "index");

        ProgramOptions programOptions = ProgramOptionExtractor.extractProgramOptions(commandLineMock);

        assertThat(programOptions.hasOption(JxlintOption.OUTPUT_TYPE)).isTrue();
        assertThat(programOptions.getOption(JxlintOption.OUTPUT_TYPE_PATH)).isEqualTo("index.xml");
    }

    @Test
    public void testProgramOptionExtractor_doesNotModifyXmlPaths() {
        CommandLine commandLineMock = getCommandLineFixture(ProgramOptionExtractor.XML_OPTION, "index.xml");

        ProgramOptions programOptions = ProgramOptionExtractor.extractProgramOptions(commandLineMock);

        assertThat(programOptions.hasOption(JxlintOption.OUTPUT_TYPE)).isTrue();
        assertThat(programOptions.getOption(JxlintOption.OUTPUT_TYPE_PATH)).isEqualTo("index.xml");
    }

    private CommandLine getCommandLineFixture(String option, String optionReturn) {
        CommandLine commandLineMock = Mockito.mock(CommandLine.class);

        when(commandLineMock.hasOption(eq(option))).thenReturn(true);
        when(commandLineMock.getOptionValue(eq(option))).thenReturn(optionReturn);

        return commandLineMock;
    }
}