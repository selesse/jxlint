package com.selesse.jxlint.model.rules;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

    @Test
    public void testVerifyLintRules() throws Exception {
        //ok:
        AbstractLintRules rules = new AbstractLintRules() {

            @Override
            public void initializeLintRules() {
                lintRules.add(new LintRuleTestImpl("Lint1"));
                lintRules.add(new LintRuleTestImpl("Lint2"));
                lintRules.add(new LintRuleTestImpl("Lint3"));
                lintRules.add(new LintRuleTestImpl("Lint4"));
            }
        };
        assertThat(rules.getAllRules()).hasSize(4);

        //empty:
        AbstractLintRules emptyRules = new AbstractLintRules() {

            @Override
            public void initializeLintRules() {
            }
        };
        assertThat(emptyRules.getAllRules()).hasSize(0);

        //duplicate (same name 3 times):
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
            new AbstractLintRules() {

                @Override
                public void initializeLintRules() {
                    lintRules.add(new LintRuleTestImpl("Lint"));
                    lintRules.add(new LintRuleTestImpl("Lint 1"));
                    lintRules.add(new LintRuleTestImpl("LINT"));
                    lintRules.add(new LintRuleTestImpl("lint"));
                }
            };
        }).withMessage(
                "Some of the rules are sharing the same name: "
                        + "'LINT' used by {com.selesse.jxlint.model.rules.LintRuleTestImpl, "
                        + "com.selesse.jxlint.model.rules.LintRuleTestImpl, "
                        + "com.selesse.jxlint.model.rules.LintRuleTestImpl}");

        //duplicate (2 names 2 times each):
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
            new AbstractLintRules() {

                @Override
                public void initializeLintRules() {
                    lintRules.add(new LintRuleTestImpl("L1"));
                    lintRules.add(new LintRuleTestImpl("L2"));
                    lintRules.add(new LintRuleTestImpl("L2"));
                    lintRules.add(new LintRuleTestImpl("L1"));
                }
            };
        }).withMessage(
                "Some of the rules are sharing the same name: "
                        + "'L1' used by {com.selesse.jxlint.model.rules.LintRuleTestImpl, "
                        + "com.selesse.jxlint.model.rules.LintRuleTestImpl}, "
                        + "'L2' used by {com.selesse.jxlint.model.rules.LintRuleTestImpl, "
                        + "com.selesse.jxlint.model.rules.LintRuleTestImpl}");
    }

    @Test
    public void testRuleName() throws Exception {
        LintRule rule1 = new LintRuleTestImpl("My first rule");
        assertThat(rule1.getName()).isEqualTo("My first rule");

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new LintRuleTestImpl("My first, rule");
        }).withMessage("Rule name can not contain a comma");
    }
}