package com.selesse.jxlint.report;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.samplerules.textfiles.rules.MustHaveAuthor;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlTemplatedReporterTest {

    private ByteArrayOutputStream output;
    private Reporter htmlReporter;

    @Before
    public void setup() throws UnsupportedEncodingException {
        output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output, true, Charsets.UTF_8.displayName());
        List<LintError> lintErrorList = createSampleLintErrors();

        htmlReporter = new HtmlTemplatedReporter(
                out, new JxlintProgramSettings(), new ProgramOptions(), lintErrorList
        );
    }

    @Test
    public void testWriteReport() throws Exception {
        htmlReporter.writeReport();

        String reportOutput = output.toString(Charsets.UTF_8.displayName());

        // A bit rudimentary, but surprisingly effective
        assertThat(reportOutput).isNotNull();
        assertThat(reportOutput).contains("Bootstrap");
        assertThat(reportOutput).contains("prettify");
        assertThat(reportOutput).contains("TableSorter");
        assertThat(reportOutput).contains("jQuery");

        // This will catch any errors with velocity <-> template linking
        assertThat(reportOutput).doesNotContain("$TemplateHelper");
    }

    private List<LintError> createSampleLintErrors() {
        return Lists.newArrayList(
                LintError.with(new MustHaveAuthor(), new File("."))
                        .andErrorMessage("Must have author")
                        .andLineNumber(10)
                        .andSeverity(Severity.FATAL)
                        .andException(new NullPointerException())
                        .create()
        );
    }
}