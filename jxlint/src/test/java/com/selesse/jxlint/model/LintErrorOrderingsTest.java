package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
import com.selesse.jxlint.linter.ValidationThread;
import com.selesse.jxlint.model.rules.Categories;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class LintErrorOrderingsTest {
    private static final File faultyFile = new File("./a");

    @Test
    public void testCompareLineNumbers() throws Exception {

        LintError lintError1 = LintError.with(null, faultyFile).andLineNumber(3).create();
        LintError lintError2 = LintError.with(null, faultyFile).andLineNumber(1).create();
        LintError lintError3 = LintError.with(null, faultyFile).andLineNumber(10).create();
        LintError lintError4 = LintError.with(null, faultyFile).andLineNumber(9).create();

        List<LintError> lintErrorList = Lists.newArrayList(lintError1, lintError2, lintError3, lintError4);

        Collections.sort(lintErrorList, LintErrorOrderings.getLineNumberOrdering());

        assertThat(lintErrorList).containsExactly(lintError2, lintError1, lintError4, lintError3);
    }

    @Test
    public void testCompareByCategoryNameThenFileThenLineNumber() throws Exception {
        File faultyFile2 = new File("./zzz");
        Categories.setCategories(Category.class);

        LintRule lintRule = Mockito.mock(LintRule.class);
        when(lintRule.getName()).thenReturn("lint rule");
        when(lintRule.getCategory()).thenAnswer(getAnswer(Category.SECURITY));

        LintRule lintRule2 = Mockito.mock(LintRule.class);
        when(lintRule2.getName()).thenReturn("lint rule number 2");
        when(lintRule2.getCategory()).thenAnswer(getAnswer(Category.LINT));

        LintError lintError1 = LintError.with(lintRule2, faultyFile).andLineNumber(3).create();
        LintError lintError2 = LintError.with(lintRule2, faultyFile).andLineNumber(1).create();
        LintError lintError3 = LintError.with(lintRule, faultyFile).andLineNumber(10).create();
        LintError lintError4 = LintError.with(lintRule, faultyFile).andLineNumber(9).create();
        LintError lintError5 = LintError.with(lintRule, faultyFile2).andLineNumber(9).create();
        LintError lintError6 = LintError.with(lintRule2, faultyFile2).andLineNumber(9).create();

        List<LintError> lintErrorList = Lists.newArrayList(
                lintError1, lintError2, lintError3, lintError4, lintError5, lintError6
        );

        Collections.sort(lintErrorList, LintErrorOrderings.getCategoryNameFileLineNumberOrdering());

        assertThat(lintErrorList).
                containsExactly(lintError2, lintError1, lintError6, lintError4, lintError3, lintError5);
    }

    @Test
    public void testCall_ordersLintErrorsByFileNameThenLineNumber() throws Exception {
        LintRule mockLintRule = Mockito.mock(LintRule.class);

        doNothing().when(mockLintRule).validate();
        when(mockLintRule.getName()).thenReturn("mock lint rule");

        ValidationThread validationThread = new ValidationThread(mockLintRule);

        LintError lintError1 = LintError.with(mockLintRule, new File("./b")).andLineNumber(3).create();
        LintError lintError2 = LintError.with(mockLintRule, new File("./a")).andLineNumber(1).create();
        LintError lintError3 = LintError.with(mockLintRule, new File("./a")).create();
        LintError lintError4 = LintError.with(mockLintRule, new File("./b")).create();
        LintError lintError5 = LintError.with(mockLintRule, new File("./b")).andLineNumber(5).create();
        LintError lintError6 = LintError.with(mockLintRule, new File("./b")).andLineNumber(1).create();

        List<LintError> unsortedLintErrorList = Lists.newArrayList(lintError1, lintError2, lintError3, lintError4,
                lintError5, lintError6);
        when(mockLintRule.getLintErrors()).thenReturn(unsortedLintErrorList);

        List<LintError> lintErrors = validationThread.call();

        assertThat(lintErrors).hasSameSizeAs(unsortedLintErrorList).containsAll(unsortedLintErrorList);
        assertThat(lintErrors).containsExactly(lintError3, lintError2, lintError4, lintError6, lintError1, lintError5);
    }

    private Answer<Enum<?>> getAnswer(final Category category) {
        return new Answer<Enum<?>>() {
            @Override
            public Enum<?> answer(InvocationOnMock invocation) throws Throwable {
                return category;
            }
        };
    }
}