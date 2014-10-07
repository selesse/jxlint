package com.selesse.jxlint;

import com.google.common.io.Files;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfilerTest extends AbstractTestCase {
    private File tempDirectory;
    private Pattern programCompletedPattern = Pattern.compile("Program completed in (.*) seconds.", Pattern.DOTALL);

    @Before
    public void setup() {
        tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();

        LintRulesImpl.setInstance(new XmlLintRulesTestImpl());
        LintRulesImpl.setExitAfterReporting(true);
    }

    @Test
    public void testProfileIsReportedWithSystemExit() {
        runExitTestRegex(new String[]{"--list", "-p"}, tempDirectory, programCompletedPattern, ExitType.SUCCESS);
    }

    @Test
    public void testProfileIsReportedWithoutSystemExit() {
        String profileJxlintOutput = checkLint(new String[]{"-p"}, tempDirectory, false);
        Matcher matcher = programCompletedPattern.matcher(profileJxlintOutput);

        assertThat(matcher.find()).isTrue();
    }

    @Test
    public void testProfileReportsRuleNumbers() {
        String profileJxlintOutput = checkLint(new String[]{"-p"}, tempDirectory, false);

        // Make sure there are some rules, otherwise this test is invalid
        assertThat(LintRulesImpl.getInstance().getAllEnabledRules().size()).isGreaterThan(0);

        // Make sure all the rules appear in the output
        for (LintRule lintRule : LintRulesImpl.getInstance().getAllEnabledRules()) {
            String ruleName = lintRule.getName();

            Pattern rulePattern = Pattern.compile(ruleName, Pattern.DOTALL);
            Matcher matcher = rulePattern.matcher(profileJxlintOutput);

            assertThat(matcher.find()).isTrue();
        }
    }
}
