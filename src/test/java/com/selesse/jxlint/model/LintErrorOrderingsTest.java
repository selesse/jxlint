package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
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
import java.util.Comparator;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
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

        Collections.sort(lintErrorList, new Comparator<LintError>() {
            @Override
            public int compare(LintError left, LintError right) {
                return LintErrorOrderings.compareLineNumbers(left, right);
            }
        });

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

        Collections.sort(lintErrorList, new Comparator<LintError>() {
            @Override
            public int compare(LintError left, LintError right) {
                return LintErrorOrderings.compareByCategoryNameThenFileThenLineNumber(left, right);
            }
        });

        assertThat(lintErrorList).
                containsExactly(lintError2, lintError1, lintError6, lintError4, lintError3, lintError5);
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