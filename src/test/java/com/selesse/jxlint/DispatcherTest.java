package com.selesse.jxlint;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.linter.LintFactory;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DispatcherTest extends AbstractTestCase {
    private File tempDirectory;

    @Before
    public void setup() {
        tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();

        LintRulesImpl.setInstance(new XmlLintRulesTestImpl());
        LintFactory.setTestMode(false);
    }

    public void setupTestLinterAndRunProgramWithArgs(String[] args) {
        LintFactory.setTestMode(true);

        new Main().run(args);
    }

    @Test
    public void testHelpProperlyExtracted() {
        String newLineSeparator = System.lineSeparator();

        final List<String> expectedOutput = Lists.newArrayList(
                  "usage: jxlint [flags] <directory>"
                , " -h,--help                Usage information, help message."
                , " -v,--version             Output version information."
                , " -l,--list                Lists lint rules with a short, summary"
                , "                          explanation."
                , " -s,--show <RULE[s]>      Lists a verbose rule explanation."
                , " -c,--check <RULE[s]>     Only check for these rules."
                , " -d,--disable <RULE[s]>   Disable the list of rules."
                , " -e,--enable <RULE[s]>    Enable the list of rules."
                , " -w,--nowarn              Only check for errors; ignore warnings."
                , " -Wall,--Wall             Check all warnings, including those off by"
                , "                          default."
                , " -Werror,--Werror         Treat all warnings as errors."
                , " -q,--quiet               Don't output any progress or reports."
                , " -t,--html <filename>     Create an HTML report."
                , " -x,--xml <filename>      Create an XML (!!) report."
                , ""
                , "<RULE[s]> should be comma separated, without spaces."
                , "Exit Status:"
                , "0                     Success"
                , "1                     Failed"
                , "2                     Command line error" + newLineSeparator
        );

        runExitTest(new String[] { "--help" }, tempDirectory, Joiner.on(newLineSeparator).join(expectedOutput), ExitType.SUCCESS);
    }

    @Test
    public void testVersionProperlyExtracted() {
        final String expectedOutput = String.format("%s: version %s", Main.getProgramName(), Main.getProgramVersion());

        runExitTest(new String[] { "--version" }, tempDirectory, expectedOutput, ExitType.SUCCESS);
    }

    @Test
    public void testShowInvalidOptionSaysSo() {
        final String expectedOutput = "'foobarrule' is not a valid rule.";

        runExitTest(new String[] { "--show=foobarrule" }, tempDirectory, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testShowInvalidOptionSaysSoVariation() {
        final String expectedOutput = "'foobarrule' is not a valid rule.";

        runExitTest(new String[] { "--show", "foobarrule" }, tempDirectory, expectedOutput,
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
                , "Category: Lint"
                , ""
                , "The XML encoding should be specified. For example, <?xml version=\"1.0\" encoding=\"UTF-8\"?>.\n\n"
        );

        runExitTest(new String[] { "--show", "XML encoding specified" }, tempDirectory, Joiner.on("\n").join(expectedOutput),
                ExitType.SUCCESS);
    }

    @Test
    public void testShowValidDisabledByDefaultOption() {
        final List<String> expectedOutput = Lists.newArrayList(
                  "Author tag specified"
                , "--------------------"
                , "Summary: author.xml files must contain a valid root-element <author> tag."
                , ""
                , "** Disabled by default **"
                , ""
                , "Severity: Warning"
                , "Category: Style"
                , ""
                , "For style purposes, every author.xml file must contain an <author> tag as the root element. " +
                  "This tag should also have the 'name' and 'creationDate' attributes. " +
                  "For example:"
                , "<?xml version=\"1.0\" encoding=\"UTF-8\">"
                , "<author name=\"Steve Holt\" creationDate=\"2013-09-28\">"
                , "  .. content .."
                , "</author>\n\n"
        );

        runExitTest(new String[] { "--show", "Author tag specified" }, tempDirectory,
                Joiner.on("\n").join(expectedOutput), ExitType.SUCCESS);
    }

    @Test
    public void testListOptions() {
        final List<String> expectedOutput = Lists.newArrayList(
               "\"Unique attribute\" : Attributes within a tag must be unique.",
               "\"XML version specified\" : Version of XML must be specified.",
               "\"XML encoding specified\" : Encoding of the XML must be specified.",
               "\"Author tag specified\"* : author.xml files must contain a valid root-element <author> tag."
        );

        runExitTest(new String[] { "--list" }, tempDirectory, Joiner.on("\n").join(expectedOutput), ExitType.SUCCESS);
    }

    @Test
    public void testLintFailsOnNonExistentDirectory() {
        final String expectedOutput = "Invalid source directory \"foobar\" : Directory does not exist.";

        runExitTest(new String[] { "foobar" }, tempDirectory, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testLintFailsOnNonDirectory() {
        try {
            File tempFile = File.createTempFile("jxlint", "tmp");
            tempFile.deleteOnExit();
            final String expectedOutput = "Invalid source directory \"" + tempFile.getAbsolutePath() + "\" : \"" +
                    tempFile.getAbsolutePath() + "\" is not a directory.";

            runExitTest(null, tempFile, expectedOutput, ExitType.COMMAND_LINE_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLintFailsOnNonReadDirectory() {
        // skip this test if we can't set the directory readability to false... (i.e. Windows)
        if (tempDirectory.setReadable(false)) {
            final String expectedOutput = "Invalid source directory \"" + tempDirectory.getAbsolutePath() + "\" : " +
                    "Cannot read directory.";

            runExitTest(null, tempDirectory, expectedOutput, ExitType.COMMAND_LINE_ERROR);
        }
    }

    @Test
    public void testLintAcceptsDirectory() {
        runExitTest(null, tempDirectory, ExitType.SUCCESS);
    }

    @Test
    public void testLintValidatesNothing() {
        createValidXml();
        runExitTest(null, tempDirectory, ExitType.SUCCESS);
    }

    @Test
    public void testLintSampleRuleFailsWhenItShould() {
        createBadAttributeFile();
        runExitTest(new String[] { "--check", "Unique attribute" }, tempDirectory, ExitType.FAILED);
    }

    @Test
    public void testLintSampleRulePassesWhenItShould() {
        createValidXml();
        runExitTest(new String[] { "--check", "XML version specified" }, tempDirectory, ExitType.SUCCESS);
    }

    @Test
    public void testWarningsReturnProperReturnCode() {
        createBadEncodingFile();
        runExitTest(new String[] { "--check", "XML encoding specified" }, tempDirectory, ExitType.SUCCESS);
    }

    @Test
    public void testFailedRulesAreAppropriate() {
        createBadAuthorFile();
        createBadVersionFile();
        createBadEncodingFile();
        createBadAttributeFile();

        setupTestLinterAndRunProgramWithArgs(new String[] { "--Wall", tempDirectory.getAbsolutePath() });
        Linter linter = LintFactory.getInstance();
        assertEquals(8, linter.getLintErrors().size());
    }

    private File createValidXml() {
        File file = new File(tempDirectory + File.separator + "valid.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fileWriter.println("<empty/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private File createBadVersionFile() {
        File file = new File(tempDirectory + File.separator + "bad-version.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml encoding=\"UTF-8\"?>");
            fileWriter.println("<empty/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private File createBadEncodingFile() {
        File file = new File(tempDirectory + File.separator + "bad-encoding.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml version=\"1.0\"?>");
            fileWriter.println("<empty/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private File createBadAttributeFile() {
        File file = new File(tempDirectory + File.separator + "bad-attribute.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fileWriter.println("<attribute name=\"dupe-name\" name=\"dupe-name\"/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private File createBadAuthorFile() {
        File file = new File(tempDirectory + File.separator + "author.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fileWriter.println("<author name=\"\"/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
