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

import static org.fest.assertions.api.Assertions.assertThat;

public class HtmlTemplatedReporterTest {

    private ByteArrayOutputStream output;

    @Before
    public void setup() throws UnsupportedEncodingException {
        output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output, true, Charsets.UTF_8.displayName());
        Reporter htmlReporter = new HtmlTemplatedReporter(
                out, new JxlintProgramSettings(), Lists.<LintError>newArrayList()
        );

        htmlReporter.writeReport();
    }

    @Test
    public void testWriteReport() throws Exception {
        String reportOutput = output.toString();

        assertThat(reportOutput).isNotNull();
        assertThat(reportOutput).contains("Bootstrap");
        assertThat(reportOutput).contains("prettify");
        assertThat(reportOutput).contains("TableSorter");
        assertThat(reportOutput).contains("jQuery");
    }
}