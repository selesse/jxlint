package com.selesse.jxlint.linter;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class ValidationThreadTest {
    private LintRule mockLintRule;

    @Before
    public void setup() {
        mockLintRule = Mockito.mock(LintRule.class);

        doNothing().when(mockLintRule).validate();
    }

    @Test
    public void testCall_ordersLintErrorsByFileNameThenLineNumber() throws Exception {
        ValidationThread validationThread = new ValidationThread(mockLintRule);

        LintError lintError1 = LintError.with(mockLintRule, new File("./b")).andLineNumber(3).create();
        LintError lintError2 = LintError.with(mockLintRule, new File("./a")).create();
        LintError lintError3 = LintError.with(mockLintRule, new File("./b")).create();
        LintError lintError4 = LintError.with(mockLintRule, new File("./b")).andLineNumber(5).create();
        LintError lintError5 = LintError.with(mockLintRule, new File("./b")).andLineNumber(1).create();

        List<LintError> unsortedLintErrorList = Lists.newArrayList(lintError1, lintError2, lintError3,
                lintError4, lintError5);
        when(mockLintRule.getLintErrors()).thenReturn(unsortedLintErrorList);

        List<LintError> lintErrors = validationThread.call();

        assertThat(lintErrors).hasSameSizeAs(unsortedLintErrorList).containsAll(unsortedLintErrorList);
        assertThat(lintErrors).containsExactly(lintError2, lintError3, lintError5, lintError1, lintError4);
    }
}