package com.selesse.jxlint.assertions;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

import static com.selesse.jxlint.assertions.JxlintAssertions.assertThat;
import static org.mockito.Mockito.when;

public class LintErrorAssertionTest {

    private LintError lintError;

    @Before
    public void setup() {
        lintError = Mockito.mock(LintError.class);
        when(lintError.getViolatedRule()).thenReturn(getGenericLintRule());
        when(lintError.getLineNumber()).thenReturn(10);
        when(lintError.getFile()).thenReturn(new File("SamplePrompts.xml"));
        when(lintError.getMessage()).thenReturn("message contains");
    }

    @Test
    public void testAssertion_occursOnLineNumber() {
        assertThat(lintError).occursOnLineNumber(10);
        try {
            assertThat(lintError).occursOnLineNumber(9);
        }
        catch (AssertionError e) {
            assertThat(e).hasMessage("Expected error for \"name\" to occur on line number <9>, but was <10>");
        }
    }

    @Test
    public void testAssertion_isViolatedIn_fileName() {
        assertThat(lintError).isViolatedIn("SamplePrompts.xml");
        try {
            assertThat(lintError).isViolatedIn("SamplePrompts2.xml");
        }
        catch (AssertionError e) {
            assertThat(e).hasMessage("Expected error for \"name\" to have been violated in <\"SamplePrompts2.xml\">," +
                    " but was <\"SamplePrompts.xml\">");
        }
    }

    @Test
    public void testAssertion_isViolatedIn_file() {
        assertThat(lintError).isViolatedIn(new File("SamplePrompts.xml"));
        try {
            assertThat(lintError).isViolatedIn(new File("SamplePrompts2.xml"));
        }
        catch (AssertionError e) {
            assertThat(e).hasMessage("Expected error for \"name\" to have been violated in <\"SamplePrompts2.xml\">," +
                    " but was <\"SamplePrompts.xml\">");
        }
    }

    @Test
    public void testAssertion_hasMessage() {
        assertThat(lintError).hasErrorMessage("message contains");
        try {
            assertThat(lintError).hasErrorMessage("does not contain");
        }
        catch (AssertionError e) {
            assertThat(e).hasMessage("Expected error message for \"name\" to be <\"does not contain\">, but was " +
                    "<\"message contains\">");
        }
    }

    @Test
    public void testAssertion_hasMessageContaining() {
        assertThat(lintError).hasErrorMessageContaining("message contains");
        try {
            assertThat(lintError).hasErrorMessageContaining("message does not contain");
        }
        catch (AssertionError e) {
            assertThat(e).hasMessage("Expected error message for \"name\" to contain <\"message does not contain\">," +
                    " but was <\"message contains\">");
        }
    }

    private LintRule getGenericLintRule() {
        return new LintRule("name", "summary", "detailed desc", Severity.ERROR, Category.LINT) {
            @Override
            public List<File> getFilesToValidate() {
                return Lists.newArrayList();
            }

            @Override
            public List<LintError> getLintErrors(File file) {
                return null;
            }
        };
    }
}
