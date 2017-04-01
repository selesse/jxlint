package com.selesse.jxlintimpl.rules.impl;

import com.google.common.io.Resources;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlintimpl.JxlintImplTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

public class FunctionsStartingWithTestAreTestsTest extends JxlintImplTest {

    private FunctionsStartingWithTestAreTests functionsStartingWithTestAreTests;

    @Before
    @Override
    public void setup() {
        super.setup();

        functionsStartingWithTestAreTests = Mockito.spy(new FunctionsStartingWithTestAreTests());

        File sampleTestDirectory = new File(Resources.getResource("sample-test/").getPath());
        when(functionsStartingWithTestAreTests.getSourceDirectory()).thenReturn(sampleTestDirectory);
    }

    @Test
    public void testGetFilesToValidate() throws Exception {
        List<File> filesToValidate = functionsStartingWithTestAreTests.getFilesToValidate();

        assertThat(filesToValidate).hasSize(1);
        assertThat(filesToValidate.get(0).getName()).isEqualTo("SomeJuicyTest.java");
    }

    @Test
    public void testGetLintErrors() throws Exception {
        functionsStartingWithTestAreTests.validate();

        List<LintError> lintErrors = functionsStartingWithTestAreTests.getLintErrors();

        assertThat(lintErrors).hasSize(2);

        for (LintError lintError : lintErrors) {
            String fileName = lintError.getFile().getName();
            String errorMessage = lintError.getMessage();

            switch (lintError.getLineNumber()) {
                case 8:
                    assertThat(fileName).isEqualTo("SomeJuicyTest.java");
                    assertThat(errorMessage).
                            isEqualTo("public void testThisShouldFail does not have @Test annotation");
                    break;
                case 13:
                    assertThat(fileName).isEqualTo("SomeJuicyTest.java");
                    assertThat(errorMessage).
                            isEqualTo("public void testThisShouldFailToo does not have @Test annotation");
                    break;
                default:
                    fail("Did not expect LintError " + lintError);
            }
        }
    }
}