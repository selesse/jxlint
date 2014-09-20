package com.selesse.jxlint.report;

import com.google.common.io.Files;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.samplerules.xml.rules.XmlEncodingRule;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import com.selesse.jxlint.settings.ProgramSettings;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.fest.assertions.api.Assertions.assertThat;

public class HtmlReportTest extends AbstractReportTest {
    @Test
    public void makeSureHtmlReportGetsCreated() throws IOException {
        File createdFile = ensureReportGetsCreatedWithType(OutputType.HTML);

        assertThat(createdFile).exists();

        String fileContents = new String(Files.toByteArray(createdFile), Charset.defaultCharset());

        ProgramSettings programSettings = new JxlintProgramSettings();

        assertThat(fileContents).contains(programSettings.getProgramName());
        assertThat(fileContents).contains(programSettings.getProgramVersion());

        assertThat(fileContents).contains("There are 1 warning, 0 errors, and 0 fatal errors (1 total).");
        // Ensure a summary report is printed
        assertThat(fileContents).contains(
                "                            <td> XML encoding specified </td>\n" +
                "                            <td> LINT </td>\n" +
                "                            <td> 1 </td>"
        );

        XmlEncodingRule xmlEncodingRule = new XmlEncodingRule();

        assertThat(fileContents).contains(xmlEncodingRule.getName());
        assertThat(fileContents).contains(xmlEncodingRule.getSummary());
        assertThat(fileContents).contains(xmlEncodingRule.getSeverity().toString());
        assertThat(fileContents).contains(xmlEncodingRule.getCategory().toString());
        assertThat(fileContents).contains("<tr> <th> Enabled by default? </th> <td> Yes </tr>");
    }
}
