package com.selesse.jxlint;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.LintRulesTestImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DispatcherTest extends AbstractTestCase {
    private LintRules lintRuleImpl = new LintRulesTestImpl();

    @Before
    public void setup() {
        LintRulesImpl.setInstance(lintRuleImpl);
    }

    @Test
    public void testHelpProperlyExtracted() {
        final List<String> expectedOutput = Lists.newArrayList(
                  "usage: jxlint [flags] [directory]"
                , " -h,--help                Usage information, help message."
                , " -v,--version             Output version information."
                , " -l,--list                Lists lint rules with a short, summary"
                , "                          explanation."
                , " -s,--show <RULE[s]>      Lists a verbose rule explanation."
                , " -d,--disable <RULE[s]>   Disable the list of rules."
                , " -e,--enable <RULE[s]>    Enable the list of rules."
                , " -c,--check <RULE[s]>     Only check for these rules."
                , " -w,--nowarn              Only check for errors; ignore warnings."
                , " -Wall,--Wall             Check all warnings, including those all by"
                , "                          default."
                , " -Werror,--Werror         Treat all warnings as errors."
                , " -q,--quiet               Don't output any progress or reports."
                , " -t,--html <arg>          Create an HTML report."
                , " -x,--xml <arg>           Create an XML (!!) report."
                , ""
                , "<RULE[s]> should be comma separated, without spaces."
                , "Exit Status:"
                , "0                     Success"
                , "1                     Failed"
                , "2                     Command line error\n"
        );

        runExitTest(new String[] { "--help" }, null, Joiner.on("\n").join(expectedOutput), ExitType.SUCCESS);
    }

    @Test
    public void testVersionProperlyExtracted() {
        final String expectedOutput = String.format("%s: version %s", Main.getProgramName(), Main.getProgramVersion());

        runExitTest(new String[] { "--version" }, null, expectedOutput, ExitType.SUCCESS);
    }

    @Test
    public void testShowInvalidOptionSaysSo() {
        final String expectedOutput = "'foobarrule' is not a valid rule.";

        runExitTest(new String[] { "--show=foobarrule" }, null, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testShowInvalidOptionSaysSoVariation() {
        final String expectedOutput = "'foobarrule' is not a valid rule.";

        runExitTest(new String[] { "--show", "foobarrule" }, null, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testShowValidOption() {
        final List<String> expectedOutput = Lists.newArrayList(
                  "Valid XML"
                , "---------"
                , "Summary: XML must be well-formed, valid."
                , ""
                , "Severity: FATAL"
                , "Category: DEFAULT"
                , ""
                , "The XML needs to be \"valid\" XML. This test definition means that the XML can be parsed by any parser. "
                + "Any tag must be closed.\n\n"
        );

        runExitTest(new String[] { "--show", "Valid XML" }, null, Joiner.on("\n").join(expectedOutput), ExitType.SUCCESS);
    }

    @Test
    public void testShowValidDisabledByDefaultOption() {
        final List<String> expectedOutput = Lists.newArrayList(
                  "XML version specified"
                , "---------------------"
                , "Summary: Version of XML must be specified."
                , ""
                , "** Disabled by default **"
                , ""
                , "Severity: WARNING"
                , "Category: DEFAULT"
                , ""
                , "The xml version should be specified. For example, <?xml version=\"1.0\">.\n\n"
        );

        runExitTest(new String[] { "--show", "XML version specified" }, null, Joiner.on("\n").join(expectedOutput), ExitType.SUCCESS);
    }

    @Test
    public void testListOptions() {
        final List<String> expectedOutput = Lists.newArrayList(
                "\"Valid XML\" : XML must be well-formed, valid."
               ,"\"Unique attribute\" : Attributes within a tag must be unique."
               ,"\"XML version specified\"* : Version of XML must be specified.\n"
        );

        runExitTest(new String[] { "--list" }, null, Joiner.on("\n").join(expectedOutput), ExitType.SUCCESS);
    }

    @Test
    public void testLintFailsOnNonExistentDirectory() {
        final String expectedOutput = "Invalid source directory \"foobar\" : Directory does not exist.";

        runExitTest(new String[] { "foobar" }, null, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testLintFailsOnNonDirectory() {
        try {
            File tempFile = File.createTempFile("jxlint", "tmp");
            tempFile.deleteOnExit();
            final String expectedOutput = "Invalid source directory \"" + tempFile.getAbsolutePath() + "\" : \"" +
                    tempFile.getAbsolutePath() + "\" is not a directory.";

            runExitTest(new String[] { tempFile.getAbsolutePath() }, null, expectedOutput, ExitType.COMMAND_LINE_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLintFailsOnNonReadDirectory() {
        File tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();

        tempDirectory.setReadable(false);

        final String expectedOutput = "Invalid source directory \"" + tempDirectory.getAbsolutePath() + "\" : " +
                "Cannot read directory.";

        runExitTest(new String[] { tempDirectory.getAbsolutePath() }, null, expectedOutput,
                ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testLintAcceptsDirectory() {
        File tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();

        runExitTest(new String[] { tempDirectory.getAbsolutePath() }, null, "", ExitType.SUCCESS);
    }
}
