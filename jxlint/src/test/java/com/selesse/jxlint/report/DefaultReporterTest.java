package com.selesse.jxlint.report;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRuleTestImpl;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.LintRulesTestImpl;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import com.selesse.jxlint.utils.EnumUtils;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultReporterTest {
    private ByteArrayOutputStream output;
    private Reporter reporter;
    private PrintStream out;

    @Before
    public void setup() throws UnsupportedEncodingException {
        output = new ByteArrayOutputStream();
        out = new PrintStream(output, true, Charsets.UTF_8.displayName());
        reporter = new DefaultReporter(
                out, new JxlintProgramSettings(), new ProgramOptions(), Lists.<LintError>newArrayList()
        );
    }

    @Test
    public void testHeadersPresent() throws Exception {
        reporter.printCategoryHeader(Category.SECURITY);

        String reportOutput = output.toString(Charsets.UTF_8.displayName());

        assertThat(reportOutput).contains(Category.SECURITY.toString());
    }

    @Test
    public void testFooterPrintsSummaryString() throws Exception {
        reporter.printFooter();

        String reportOutput = output.toString(Charsets.UTF_8.displayName());

        // The reporter has an empty list, so there should be no footer
        assertThat(reportOutput).isEmpty();

        LintError lintError = LintError.with(new LintRuleTestImpl(), new File("abc")).create();
        reporter = new DefaultReporter(out, new JxlintProgramSettings(), new ProgramOptions(),
                Lists.newArrayList(lintError));

        reporter.printFooter();

        reportOutput = output.toString(Charsets.UTF_8.displayName());
        assertThat(reportOutput).isNotEmpty().startsWith("There are 1 warning, 0 errors, and 0 fatal errors "
                + "(1 total).");
    }

    @Test
    public void testPrintError_simple() throws Exception {
        File sourceDirectory = setupSourceDirectory();

        File faultyFile = new File(sourceDirectory, "relativePath");
        Severity fatal = Severity.FATAL;

        LintError lintError = LintError.with(new LintRuleTestImpl(), faultyFile)
                .andSeverity(fatal)
                .create();

        reporter.printError(lintError);

        String reportOutput = output.toString(Charsets.UTF_8.displayName());

        assertThat(reportOutput).contains(EnumUtils.toHappyString(fatal));
        assertThat(reportOutput).contains(lintError.getViolatedRule().getName());
        assertThat(reportOutput).contains("in relativePath");
        assertThat(reportOutput).doesNotContain("on line");
        assertThat(reportOutput).doesNotContain("Exception thrown:");
    }

    @Test
    public void testPrintError_withLineNumberAndException() throws Exception {
        File sourceDirectory = setupSourceDirectory();

        File faultyFile = new File(sourceDirectory, "someFileRelativeToRoot");
        Severity error = Severity.ERROR;

        LintError lintError = LintError.with(new LintRuleTestImpl(), faultyFile)
                .andSeverity(error)
                .andLineNumber(9001)
                .andException(new RuntimeException("Runtime exceptions are my favorite!"))
                .create();

        reporter.printError(lintError);

        String reportOutput = output.toString(Charsets.UTF_8.displayName());

        assertThat(reportOutput).contains(EnumUtils.toHappyString(error));
        assertThat(reportOutput).contains(lintError.getViolatedRule().getName());
        assertThat(reportOutput).contains("in someFileRelativeToRoot on line 9001");
        assertThat(reportOutput).contains("Exception thrown:");
        assertThat(reportOutput).contains("java.lang.RuntimeException: Runtime exceptions are my favorite!");

        System.out.println(reportOutput);
    }

    private File setupSourceDirectory() {
        // Need to set up the LintRulesImpl before the reporter can print the error. This is because the
        // reporter relativizes paths from the source directory. This is definitely a code smell, it doesn't make
        // sense to need this kind of setup.
        LintRules lintRulesTest = new LintRulesTestImpl();
        File sourceDirectory = new File(".");
        lintRulesTest.setSourceDirectory(sourceDirectory);

        // Magic!
        LintRulesImpl.setInstance(lintRulesTest);
        return sourceDirectory;
    }
}