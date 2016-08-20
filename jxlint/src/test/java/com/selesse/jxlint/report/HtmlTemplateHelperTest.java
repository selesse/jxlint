package com.selesse.jxlint.report;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlTemplateHelperTest {
    @Test
    public void testErrorWithExceptionWithoutMessageReportsProperly() {
        String stackTrace = HtmlTemplateHelper.sanitizeStackTrace(new NullPointerException());

        assertThat(stackTrace).startsWith("NullPointerException");
    }

}