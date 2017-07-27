package com.selesse.jxlint;

import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An abstract implementation of a file-based tester. Suppose you have a lint class "XTest".
 * This test will look in a specified directory for the files "XTestPass" and "XTestFail". It will
 * then apply the lint rule to both those files, and expect the test rules to pass and fail
 * accordingly.
 */
public abstract class AbstractPassFailFileTest {
    protected String passFileName;
    protected String failFileName;
    protected LintRule lintRule;
    protected File sourceDirectory;

    public AbstractPassFailFileTest(LintRules lintRules, File sourceDirectory, LintRule lintRule) {
        LintRulesImpl.setInstance(lintRules);
        this.sourceDirectory = sourceDirectory;
        this.lintRule = lintRule;
        this.passFileName = this.getClass().getSimpleName() + "Pass";
        this.failFileName = this.getClass().getSimpleName() + "Fail";
    }

    @Test
    public void testPassesPositiveTestCase() {
        File passFile = new File(sourceDirectory, passFileName);

        assertThat(lintRule.passesValidation(passFile)).
                overridingErrorMessage(this.getClass().getName() + " failed the positive test case.\n"
                        + "It was intended to pass, but it failed.").isTrue();
    }

    @Test
    public void testPassesNegativeTestCase() {
        File failFile = new File(sourceDirectory, failFileName);

        assertThat(lintRule.passesValidation(failFile)).
                overridingErrorMessage(this.getClass().getName() + " failed the negative test case.\n"
                        + "It was intended to fail, but it passed.").isFalse();
    }
}
