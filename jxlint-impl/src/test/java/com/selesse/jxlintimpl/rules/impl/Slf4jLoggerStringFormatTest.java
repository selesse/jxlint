package com.selesse.jxlintimpl.rules.impl;

import com.google.common.io.Resources;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlintimpl.JxlintImplTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

import static com.selesse.jxlint.assertions.JxlintAssertions.assertThat;
import static org.mockito.Mockito.when;

public class Slf4jLoggerStringFormatTest extends JxlintImplTest {
    private Slf4jLoggerStringFormat slf4jLoggerStringFormat;

    @Before
    @Override
    public void setup() {
        super.setup();

        slf4jLoggerStringFormat = Mockito.spy(new Slf4jLoggerStringFormat());

        File sampleTestDirectory = new File(Resources.getResource("sample-test/").getPath());
        when(slf4jLoggerStringFormat.getSourceDirectory()).thenReturn(sampleTestDirectory);
    }

    @Test
    public void testGetFilesToValidate() throws Exception {
        List<File> filesToValidate = slf4jLoggerStringFormat.getFilesToValidate();

        assertThat(filesToValidate).hasSize(3);
    }

    @Test
    public void testGetLintErrors() throws Exception {
        slf4jLoggerStringFormat.validate();

        List<LintError> lintErrors = slf4jLoggerStringFormat.getLintErrors();

        assertThat(lintErrors).hasSize(1);
        LintError lintError = lintErrors.get(0);

        assertThat(lintError)
                .occursOnLineNumber(10)
                .isViolatedIn("SomeJuicyTest.java")
                .hasErrorMessageContaining("String.format");
    }
}