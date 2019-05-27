package com.selesse.jxlint;

import com.google.common.base.Joiner;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.actions.web.JettyWebRunner;
import com.selesse.jxlint.cli.ProgramOptionExtractor;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import com.selesse.jxlint.settings.ProgramSettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DispatcherTest extends AbstractTestCase {
    private File tempDirectory;

    @Before
    public void setup() {
        tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();

        LintRulesImpl.setInstance(new XmlLintRulesTestImpl());
        LintRulesImpl.setExitAfterReporting(true);
    }

    @Test
    public void testHelpProperlyExtracted() {
        String newLineSeparator = System.getProperty(StandardSystemProperty.LINE_SEPARATOR.key());

        List<String> expectedOutput = Lists.newArrayList(
                "usage: jxlint [flags] <directory>",
                " -h,--help                     Usage information, help message.",
                " -v,--version                  Output version information.",
                " -p,--profile                  Measure time every rule takes to complete.",
                " -l,--list                     Lists lint rules with a short, summary",
                "                               explanation.",
                " -b,--web <port>               Run in the background, as a website.",
                "                               (default port: " + ProgramOptionExtractor.DEFAULT_PORT + ")",
                " -r,--rules                    Prints a Markdown dump of the program's",
                "                               rules.",
                " -s,--show <RULE[s]>           Lists a verbose rule explanation.",
                " -c,--check <RULE[s]>          Only check for these rules.",
                " -d,--disable <RULE[s]>        Disable the list of rules.",
                " -e,--enable <RULE[s]>         Enable the list of rules.",
                " -y,--category <CATEGORY[s]>   Run all rules of a certain category.",
                " -w,--nowarn                   Only check for errors; ignore warnings.",
                " -Wall,--Wall                  Check all warnings, including those off by",
                "                               default.",
                " -Werror,--Werror              Treat all warnings as errors.",
                " -q,--quiet                    Don't output any progress or reports.",
                " -t,--html <filename>          Create an HTML report.",
                " -x,--xml <filename>           Create an XML (!!) report.",
                " -j,--jenkins-xml <filename>   Create an XML Jenkins format (!!) report.",
                " -sp,--srcpath <PATH-PREFIX>   Local or remote path to the source",
                "                               directory, if not set a relative path to",
                "                               the local file will be computed.",
                "",
                "<RULE[s]> should be comma separated, without spaces.",
                "<PATH-PREFIX>:",
                "Links to the source code files will use this value as value as prefix.",
                "Possible values:",
                " - relative path: ../../my-project/",
                " - absolute path: file:///C:/work/my-project/",
                " - online path: https://github.com/selesse/jxlint/blob/master/jxlint-impl/",
                "",
                "Exit Status:",
                "0                     Success",
                "1                     Failed",
                "2                     Command line error",
                ""
        );

        runExitTest(new String[] { "--help" }, tempDirectory, Joiner.on(newLineSeparator).join(expectedOutput),
                ExitType.SUCCESS);
    }

    @Test
    public void testVersionProperlyExtracted() {
        ProgramSettings programSettings = new JxlintProgramSettings();
        final String expectedOutput = String.format("%s: version %s", programSettings.getProgramName(),
                programSettings.getProgramVersion());

        runExitTest(new String[] { "--version" }, tempDirectory, expectedOutput, ExitType.SUCCESS);
    }

    @Test
    public void testLintFailsOnNonExistentDirectory() {
        final String expectedOutput = "Invalid source directory \"foobar\" : \"foobar\" is not an existing directory.";

        runExitTest(new String[] { "foobar" }, tempDirectory, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testLintFailsOnNonDirectory() {
        try {
            File tempFile = File.createTempFile("jxlint", "tmp");
            tempFile.deleteOnExit();
            final String expectedOutput = "Invalid source directory \"" + tempFile.getAbsolutePath() + "\" : \"" +
                    tempFile.getAbsolutePath() + "\" is not an existing directory.";

            runExitTest(null, tempFile, expectedOutput, ExitType.COMMAND_LINE_ERROR);
        }
        catch (IOException e) {
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
        TestFileCreator.createValidXml(tempDirectory);
        runExitTest(null, tempDirectory, ExitType.SUCCESS);
    }

    @Test
    public void testLintSampleRuleFailsWhenItShould() {
        TestFileCreator.createBadAttributeFile(tempDirectory);
        runExitTest(new String[] { "--check", "Unique attribute" }, tempDirectory, ExitType.FAILED);
    }

    @Test
    public void testLintSampleRulePassesWhenItShould() {
        TestFileCreator.createValidXml(tempDirectory);
        runExitTest(new String[] { "--check", "XML version specified" }, tempDirectory, ExitType.SUCCESS);
    }

    @Test
    public void testWarningsReturnProperReturnCode() {
        TestFileCreator.createBadEncodingFile(tempDirectory);

        // The XML encoding specified is a warning. It should return exit status 0.
        runExitTest(new String[] { "--check", "XML encoding specified" }, tempDirectory, ExitType.SUCCESS);
    }

    @Test
    public void testWarningsBeingErrorsProperlyTriggered() {
        TestFileCreator.createBadEncodingFile(tempDirectory);
        runExitTest(new String[]{"--Werror"}, tempDirectory, ExitType.FAILED);
    }

    public void setupTestLinterAndRunProgramWithArgs(String[] args) {
        Jxlint jxlint = new Jxlint(new XmlLintRulesTestImpl(), new JxlintProgramSettings());
        LintRulesImpl.setExitAfterReporting(false);

        jxlint.parseArgumentsAndDispatch(args);
    }

    @Test
    public void testFailedRulesAreAppropriate() {
        TestFileCreator.createBadAuthorFile(tempDirectory);
        TestFileCreator.createBadVersionFile(tempDirectory);
        TestFileCreator.createBadEncodingFile(tempDirectory);
        TestFileCreator.createBadAttributeFile(tempDirectory);

        setupTestLinterAndRunProgramWithArgs(new String[]{"--Wall", tempDirectory.getAbsolutePath()});
        Linter linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).hasSize(8);
    }

    @Test
    public void testEnablingSpecificRulesEnablesThem() {
        // First, create a bad author file and assert that there are no errors
        File badAuthorFile = TestFileCreator.createBadAuthorFile(tempDirectory);

        setupTestLinterAndRunProgramWithArgs(new String[] { tempDirectory.getAbsolutePath() });
        Linter linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).isEmpty();

        // Now, let's re-run the same program with the bad author file rule enabled... We should flag it!
        setupTestLinterAndRunProgramWithArgs(new String[] { "--enable", "Author tag specified",
                tempDirectory.getAbsolutePath() });
        linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).hasSize(1);
        assertThat(linter.getLintErrors().get(0).getFile().getAbsolutePath()).
                isEqualTo(badAuthorFile.getAbsolutePath());
    }

    @Test
    public void testDisablingSpecificRulesDisablesThem() {
        // First, create a bad encoding file and assert that there are errors
        TestFileCreator.createBadEncodingFile(tempDirectory);

        setupTestLinterAndRunProgramWithArgs(new String[] { tempDirectory.getAbsolutePath() });
        Linter linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).hasSize(1);

        // Now, let's re-run the same program with the bad encoding file rule disabled... It should shut up!
        setupTestLinterAndRunProgramWithArgs(new String[] { "--disable", "XML encoding specified",
                tempDirectory.getAbsolutePath() });
        linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).isEmpty();
    }

    @Test
    public void testNoWarnOptionTriggersCorrectly() {
        // First, create a bad encoding file and assert that there are errors
        TestFileCreator.createBadEncodingFile(tempDirectory);

        setupTestLinterAndRunProgramWithArgs(new String[] { tempDirectory.getAbsolutePath() });
        Linter linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).hasSize(1);

        // Now, let's re-run the same program without pesky warnings... It should shut up!
        setupTestLinterAndRunProgramWithArgs(new String[] { "--nowarn", tempDirectory.getAbsolutePath() });
        linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).isEmpty();
    }

    @Test
    public void testCategoryReturnsRightRules() {
        TestFileCreator.createBadAuthorFile(tempDirectory);
        TestFileCreator.createBadVersionFile(tempDirectory);
        TestFileCreator.createBadEncodingFile(tempDirectory);
        TestFileCreator.createBadAttributeFile(tempDirectory);

        setupTestLinterAndRunProgramWithArgs(new String[]{"--category", Category.LINT.toString(),
                tempDirectory.getAbsolutePath()});

        Linter linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).hasSize(5);

        for (LintError lintError : linter.getLintErrors()) {
            assertThat(lintError.getViolatedRule().getCategory()).isEqualTo(Category.LINT);
        }

        setupTestLinterAndRunProgramWithArgs(new String[]{"--category", Category.PERFORMANCE.toString(),
                tempDirectory.getAbsolutePath()});
        linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).hasSize(2);

        for (LintError lintError : linter.getLintErrors()) {
            assertThat(lintError.getViolatedRule().getCategory()).isEqualTo(Category.PERFORMANCE);
        }
    }

    @Test
    public void testWebStartsJettyWebRunner() {
        ProgramOptions programOptionsMock = Mockito.mock(ProgramOptions.class);
        ProgramSettings programSettings = new JxlintProgramSettings();
        JettyWebRunner jettyWebRunnerMock = Mockito.mock(JettyWebRunner.class);

        Dispatcher dispatcherSpy = spy(new Dispatcher(programOptionsMock, programSettings));

        when(programOptionsMock.hasOption(any(JxlintOption.class))).thenReturn(false);
        when(programOptionsMock.hasOption(JxlintOption.WEB)).thenReturn(true);
        when(dispatcherSpy.getJettyWebRunner(anyString())).thenReturn(jettyWebRunnerMock);

        dispatcherSpy.dispatch();

        verify(jettyWebRunnerMock).start();
    }

    @Test
    public void testBadCategory() throws Exception {
        runExitTest(new String[]{"--category", "abc"}, tempDirectory,
                "Category 'abc' does not exist. Try one of: LINT, CORRECTNESS, PERFORMANCE, SECURITY, STYLE.",
                ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testBadCheckRules() throws Exception {
        runExitTest(new String[]{"--check", "xyz"}, tempDirectory,
                "Lint rule 'xyz' does not exist.",
                ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testBadEnableRules() throws Exception {
        runExitTest(new String[]{"--enable", "abc"}, tempDirectory,
                "Lint rule 'abc' does not exist.",
                ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testBadDisableRules() throws Exception {
        runExitTest(new String[]{"--disable", "zyx"}, tempDirectory,
                "Lint rule 'zyx' does not exist.",
                ExitType.COMMAND_LINE_ERROR);
    }
}
