package com.selesse.jxlint.report;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.samplerules.xml.rules.XmlEncodingRule;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import com.selesse.jxlint.settings.ProgramSettings;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportersTest {

    @Test
    public void testDefaultReporters() {
        Class<? extends Reporter> defaultReporter = Reporters.getReporter(OutputType.DEFAULT);
        Class<? extends Reporter> defaultHtmlReporter = Reporters.getReporter(OutputType.HTML);
        Class<? extends Reporter> defaultXmlReporter = Reporters.getReporter(OutputType.XML);
        Class<? extends Reporter> quietReporter = Reporters.getReporter(OutputType.QUIET);

        assertThat(defaultReporter.getName()).isEqualTo(DefaultReporter.class.getName());
        assertThat(defaultHtmlReporter.getName()).isEqualTo(HtmlTemplatedReporter.class.getName());
        assertThat(defaultXmlReporter.getName()).isEqualTo(XmlReporter.class.getName());
        assertThat(quietReporter.getName()).isEqualTo(DefaultReporter.class.getName());
    }

    @Test
    public void testSetCustomReporter() throws Exception {
        assertThat(Reporters.getReporter(OutputType.QUIET)).isNotNull();

        Reporters.setCustomReporter(OutputType.QUIET, null);

        assertThat(Reporters.getReporter(OutputType.QUIET)).isNull();
    }

    @Test
    public void testCreateReporter() throws Exception {
        Reporter defaultReporter = Reporters.createReporter(null, null, null, null);
        assertThat(defaultReporter).isInstanceOf(DefaultReporter.class);
        assertThat(defaultReporter.out).isEqualTo(System.out);

        Reporter quietReporter = Reporters.createReporter(null, null, OutputType.QUIET, null);
        assertThat(quietReporter).isInstanceOf(DefaultReporter.class);
        assertThat(quietReporter.out).isNotEqualTo(System.out);

        List<LintError> lintErrors = Lists.newArrayList(
                LintError.with(new XmlEncodingRule(), new File(".")).create()
        );
        ProgramSettings programSettings = new JxlintProgramSettings();

        Reporter htmlReporter = Reporters.createReporter(lintErrors, programSettings, OutputType.HTML, null);
        assertThat(htmlReporter).isInstanceOf(HtmlTemplatedReporter.class);

        Reporter xmlReporter = Reporters.createReporter(lintErrors, programSettings, OutputType.XML, null);
        assertThat(xmlReporter).isInstanceOf(XmlReporter.class);
    }
}