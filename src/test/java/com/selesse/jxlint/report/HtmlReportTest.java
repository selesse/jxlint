package com.selesse.jxlint.report;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.selesse.jxlint.model.OutputType;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class HtmlReportTest extends AbstractReportTest {
    @Test
    public void makeSureHtmlReportGetsCreated() throws IOException {
        File createdFile = ensureReportGetsCreatedWithType(OutputType.HTML);

        // TODO: verify this with a bit more robustness
        List<String> fileContents = Files.readLines(createdFile, Charsets.UTF_8);
        assertTrue(fileContents.get(0).toLowerCase().contains("html>"));
    }
}
