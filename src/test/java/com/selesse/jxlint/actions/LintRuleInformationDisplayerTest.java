package com.selesse.jxlint.actions;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.selesse.jxlint.AbstractTestCase;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import com.selesse.jxlint.settings.ProgramSettings;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class LintRuleInformationDisplayerTest extends AbstractTestCase {
    private File tempDirectory;

    @Before
    public void setup() {
        tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();

        LintRulesImpl.setInstance(new XmlLintRulesTestImpl());
        LintRulesImpl.setExitAfterReporting(true);
    }

    @Test
    public void testMarkdownReportProperlyGenerated() throws URISyntaxException, IOException {
        File markdownDump = new File(Resources.getResource("markdown-dump.md").toURI());
        List<String> markdownDumpContents = Files.readLines(markdownDump, Charsets.UTF_8);
        String expectedOutput = Joiner.on("\n").join(markdownDumpContents);
        ProgramSettings programSettings = new JxlintProgramSettings();
        expectedOutput = expectedOutput.replace("$$$VERSION$$$", programSettings.getProgramVersion());

        runExitTest(new String[]{"-r"}, tempDirectory, expectedOutput, ExitType.SUCCESS);
    }

    @Test
    public void testShowInvalidOptionSaysSo() {
        final String expectedOutput = "'foobarrule' is not a valid rule.";

        runExitTest(new String[]{"--show=foobarrule"}, tempDirectory, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testShowInvalidOptionSaysSoVariation() {
        final String expectedOutput = "'foobarrule' is not a valid rule.";

        runExitTest(new String[]{"--show", "foobarrule"}, tempDirectory, expectedOutput,
                ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testShowValidOption() {
        final List<String> expectedOutput = Lists.newArrayList(
                "XML encoding specified"
                , "----------------------"
                , "Summary: Encoding of the XML must be specified."
                , ""
                , "Severity: Warning"
                , "Category: LINT"
                , ""
                , "The XML encoding should be specified. For example, <?xml version=\"1.0\" encoding=\"UTF-8\"?>.\n\n"
        );

        runExitTest(new String[]{"--show", "XML encoding specified"}, tempDirectory,
                Joiner.on("\n").join(expectedOutput), ExitType.SUCCESS);
    }

    @Test
    public void testShowValidDisabledByDefaultOption() {
        String expectedRuleOutput =
                "Author tag specified\n" +
                        "--------------------\n" +
                        "Summary: author.xml files must contain a valid root-element <author> tag.\n" +
                        "\n" +
                        "** Disabled by default **\n" +
                        "\n" +
                        "Severity: Warning\n" +
                        "Category: STYLE\n" +
                        "\n" +
                        "For style purposes, every author.xml file must contain an <author> tag as the\n" +
                        "root element. This tag should also have the 'name' and 'creationDate'\n" +
                        "attributes. For example:\n" +
                        "\n" +
                        "    <?xml version=\"1.0\" encoding=\"UTF-8\">\n" +
                        "    <author name=\"Steve Holt\" creationDate=\"2013-09-28\">\n" +
                        "      .. content ..\n" +
                        "    </author>\n\n";

        runExitTest(new String[]{"--show", "Author tag specified"}, tempDirectory,
                expectedRuleOutput, ExitType.SUCCESS);
    }

    @Test
    public void testListOptions() {
        final List<String> expectedOutput = Lists.newArrayList(
                "\"Unique attribute\" : Attributes within a tag must be unique.",
                "\"XML version specified\" : Version of XML must be specified.",
                "\"XML encoding specified\" : Encoding of the XML must be specified.",
                "\"Author tag specified\"* : author.xml files must contain a valid root-element <author> tag."
        );

        runExitTest(new String[]{"--list"}, tempDirectory, Joiner.on("\n").join(expectedOutput), ExitType.SUCCESS);
    }

}
