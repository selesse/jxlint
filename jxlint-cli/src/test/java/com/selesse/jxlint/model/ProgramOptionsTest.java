package com.selesse.jxlint.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.AbstractTestCase;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import com.selesse.jxlint.samplerules.xml.rules.UniqueAttributeRule;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramOptionsTest extends AbstractTestCase {
    private static File rootTempDir;

    @BeforeClass
    public static void setup() {
        LintRulesImpl.setInstance(new XmlLintRulesTestImpl());
        rootTempDir = Files.createTempDir();
        rootTempDir.deleteOnExit();
    }

    @Test(expected = NonExistentLintRuleException.class)
    public void testGettingAnInvalidRuleThrowsException() throws NonExistentLintRuleException {
        ProgramOptions.getRuleListFromOptionString("blah");
    }

    @Test
    public void testGettingValidRulesReturnsThem() throws NonExistentLintRuleException {
        LintRule authorTagRule = new AuthorTagRule();
        LintRule uniqueAttributeRule = new UniqueAttributeRule();

        List<String> ruleList = ProgramOptions.getRuleListFromOptionString(
                Joiner.on(", ").join(Lists.newArrayList(authorTagRule.getName(), uniqueAttributeRule.getName()))
        );

        assertThat(ruleList).hasSize(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGettingInvalidCategoriesThrowsAnException() {
        ProgramOptions.getCategoryListFromOptionString("blah");
    }

    @Test
    public void testGettingValidCategoriesReturnsThem() {
        List<String> categoryList = ProgramOptions.getCategoryListFromOptionString(
                Joiner.on(", ").join(Category.CORRECTNESS, Category.LINT, Category.PERFORMANCE, Category.STYLE)
        );

        assertThat(categoryList).hasSize(4);
    }

    @Test
    public void testGetSourcePathPrefix() {
        ProgramOptions option;

        option = new ProgramOptions();
        option.addOption(JxlintOption.SRC_PATH_PREFIX, "https://github.com/selesse/jxlint/blob/master/jxlint-impl/");
        assertThat(option.getSourcePathPrefix())
            .isEqualTo("https://github.com/selesse/jxlint/blob/master/jxlint-impl/");

        option = new ProgramOptions();
        option.setSourceDirectory(null);
        assertThat(option.getSourcePathPrefix()).isEqualTo("");

        option = new ProgramOptions();
        option.setSourceDirectory(rootTempDir.getAbsolutePath());
        assertThat(option.getSourcePathPrefix()).isEqualTo(rootTempDir.toURI().toString());

        option = new ProgramOptions();
        option.setSourceDirectory(rootTempDir.getAbsolutePath());
        option.addOption(JxlintOption.OUTPUT_TYPE_PATH, "report.html");
        assertThat(option.getSourcePathPrefix()).isEqualTo(rootTempDir.toURI().toString());

        option = new ProgramOptions();
        option.setSourceDirectory(rootTempDir.getAbsolutePath());
        option.addOption(JxlintOption.OUTPUT_TYPE_PATH,
            new File(new File(rootTempDir, "target"), "report.html").getAbsolutePath());
        assertThat(option.getSourcePathPrefix()).isEqualTo("../");

        option = new ProgramOptions();
        option.setSourceDirectory(new File(rootTempDir, "src").getAbsolutePath());
        option.addOption(JxlintOption.OUTPUT_TYPE_PATH,
                new File(new File(rootTempDir, "target"), "report.html").getAbsolutePath());
        assertThat(option.getSourcePathPrefix()).isEqualTo("../src/");

        option = new ProgramOptions();
        option.setSourceDirectory(rootTempDir.getAbsolutePath());
        option.addOption(JxlintOption.OUTPUT_TYPE_PATH, new File(rootTempDir, "report.html").getAbsolutePath());
        assertThat(option.getSourcePathPrefix()).isEqualTo("");
    }
}
