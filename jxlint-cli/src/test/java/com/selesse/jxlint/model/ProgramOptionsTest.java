package com.selesse.jxlint.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.AbstractTestCase;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import com.selesse.jxlint.samplerules.xml.rules.UniqueAttributeRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlEncodingRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlVersionRule;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ProgramOptionsTest extends AbstractTestCase {
    private static File rootTempDir;

    @BeforeClass
    public static void setup() {
        LintRulesImpl.setInstance(new XmlLintRulesTestImpl());
        rootTempDir = Files.createTempDir();
        rootTempDir.deleteOnExit();
    }

    @Test
    public void testGettingInvalidRuleThrowsException() throws NonExistentLintRuleException {
        assertThatExceptionOfType(NonExistentLintRuleException.class).isThrownBy(() -> {
            ProgramOptions.getRuleListFromOptionString("blah");
        }).withMessage("Lint rule 'blah' does not exist.");
    }

    @Test
    public void testGettingOneInvalidRuleThrowsException() {
        assertThatExceptionOfType(NonExistentLintRuleException.class).isThrownBy(() -> {
            ProgramOptions.getRuleListFromOptionString(
                    Joiner.on(", ").join(Lists.newArrayList(XmlEncodingRule.NAME, "yxz", XmlVersionRule.NAME))
            );
        }).withMessage("Lint rule 'yxz' does not exist.");
    }

    @Test
    public void testGettingValidRulesReturnsThem() throws NonExistentLintRuleException {
        List<String> ruleList = ProgramOptions.getRuleListFromOptionString(
                Joiner.on(", ").join(Lists.newArrayList(AuthorTagRule.NAME, UniqueAttributeRule.NAME))
        );

        assertThat(ruleList).hasSize(2);
        assertThat(ruleList).isEqualTo(Lists.newArrayList(AuthorTagRule.NAME, UniqueAttributeRule.NAME));
    }

    @Test
    public void testGettingValidRulesFromEmptyString() throws Exception {
        List<String> categoryList = ProgramOptions.getRuleListFromOptionString("");
        assertThat(categoryList).hasSize(0);
    }

    @Test
    public void testGettingInvalidCategoryThrowsAnException() {
        assertThatExceptionOfType(NonExistentCategoryException.class).isThrownBy(() -> {
            ProgramOptions.getCategoryListFromOptionString("blah");
        }).withMessage(
                "Category 'blah' does not exist. Try one of: LINT, CORRECTNESS, PERFORMANCE, SECURITY, STYLE.");
    }

    @Test
    public void testGettingOneInvalidCategoryThrowsAnException() {
        assertThatExceptionOfType(NonExistentCategoryException.class).isThrownBy(() -> {
            ProgramOptions.getCategoryListFromOptionString(
                    Joiner.on(", ").join(Category.CORRECTNESS, Category.LINT, "xyz")
            );
        }).withMessage(
                "Category 'xyz' does not exist. Try one of: LINT, CORRECTNESS, PERFORMANCE, SECURITY, STYLE.");
    }

    @Test
    public void testGettingValidCategoriesReturnsThem() throws Exception {
        List<String> categoryList = ProgramOptions.getCategoryListFromOptionString(
                Joiner.on(", ").join(Category.CORRECTNESS, Category.LINT, Category.PERFORMANCE, Category.STYLE)
        );

        assertThat(categoryList).hasSize(4);
        assertThat(categoryList)
                .isEqualTo(Arrays.asList(Category.CORRECTNESS.toString(), Category.LINT.toString(),
                        Category.PERFORMANCE.toString(), Category.STYLE.toString()));
    }

    @Test
    public void testGettingValidCategoriesFromEmptyString() throws Exception {
        List<String> categoryList = ProgramOptions.getCategoryListFromOptionString("");
        assertThat(categoryList).hasSize(0);
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
