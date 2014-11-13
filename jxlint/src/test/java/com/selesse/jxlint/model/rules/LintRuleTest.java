package com.selesse.jxlint.model.rules;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LintRuleTest {
    @Test
    public void testValidatingTwiceDoesntChangeValidationSize() {
        final LintRule lintRule = getAlwaysFailingLintRule();

        LintRulesImpl.setInstance(new AbstractLintRules() {
            @Override
            public void initializeLintRules() {
                lintRules.add(lintRule);
            }
        });

        lintRule.validate();
        List<LintError> lintErrors = lintRule.getLintErrors();

        assertThat(lintErrors).hasSize(2);

        lintRule.validate();
        assertThat(lintErrors).hasSize(2);
    }

    private LintRule getAlwaysFailingLintRule() {
        return new LintRule("name", "summary", "description", Severity.ERROR, Category.CORRECTNESS) {
            @Override
            public List<File> getFilesToValidate() {
                return Lists.newArrayList(new File("."), new File(".."));
            }

            @Override
            public List<LintError> getLintErrors(File file) {
                return Lists.newArrayList(LintError.with(this, file).create());
            }
        };
    }

}