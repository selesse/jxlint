package com.selesse.jxlint.report;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlTemplatedReporterTest {

    private ByteArrayOutputStream output;
    private Reporter htmlReporter;

    @Before
    public void setup() throws UnsupportedEncodingException {
        output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output, true, Charsets.UTF_8.displayName());
        htmlReporter = new HtmlTemplatedReporter(
                out, new JxlintProgramSettings(), Lists.<LintError>newArrayList()
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
    }
}