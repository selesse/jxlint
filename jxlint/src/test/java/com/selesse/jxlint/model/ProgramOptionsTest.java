package com.selesse.jxlint.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.model.rules.AbstractLintRules;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRuleTestImpl;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ProgramOptionsTest {
    private static final String RULE_NAME1 = "L one";
    private static final String RULE_NAME2 = "Lint 2";
    private static final String RULE_NAME3 = "LINT_3";
    private static final String RULE_NAME4 = "L4";

    private static final LintRule RULE1 = new LintRuleTestImpl(RULE_NAME1);
    private static final LintRule RULE2 = new LintRuleTestImpl(RULE_NAME2);
    private static final LintRule RULE3 = new LintRuleTestImpl(RULE_NAME3);
    private static final LintRule RULE4 = new LintRuleTestImpl(RULE_NAME4);

    private static File rootTempDir;

    @BeforeClass
    public static void setup() {
        LintRulesImpl.setInstance(new FourLintRulesTestImpl());
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
                    Joiner.on(", ").join(Lists.newArrayList(RULE_NAME1, "yxz", RULE_NAME2))
            );
        }).withMessage("Lint rule 'yxz' does not exist.");
    }

    @Test
    public void testGettingValidRulesReturnsThem() throws NonExistentLintRuleException {
        List<LintRule> ruleList = ProgramOptions.getRuleListFromOptionString(
                Joiner.on(", ").join(Lists.newArrayList(RULE_NAME3, RULE_NAME4))
        );

        assertThat(ruleList).hasSize(2);
        assertThat(ruleList).isEqualTo(Lists.newArrayList(RULE3, RULE4));
    }

    @Test
    public void testGettingValidRulesFromEmptyString() throws Exception {
        List<LintRule> categoryList = ProgramOptions.getRuleListFromOptionString("");
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
        List<Enum<?>> categoryList = ProgramOptions.getCategoryListFromOptionString(
                Joiner.on(", ").join(Category.CORRECTNESS, Category.LINT, Category.PERFORMANCE, Category.STYLE)
        );

        assertThat(categoryList).hasSize(4);
        assertThat(categoryList)
                .isEqualTo(Arrays.asList(Category.CORRECTNESS, Category.LINT, Category.PERFORMANCE, Category.STYLE));
    }

    @Test
    public void testGettingValidCategoriesFromEmptyString() throws Exception {
        List<Enum<?>> categoryList = ProgramOptions.getCategoryListFromOptionString("");
        assertThat(categoryList).hasSize(0);
    }

    @Test
    public void testSetAndGetEnabledCategories() throws Exception {
        ProgramOptions options = new ProgramOptions();
        //Option not set:
        assertThat(options.hasOption(JxlintOption.CATEGORY)).isEqualTo(false);
        assertThat(options.getOption(JxlintOption.CATEGORY)).isEqualTo(null);
        assertThat(options.getEnabledCategories()).isNotPresent();

        //Set with addOption(JxlintOption.CATEGORY, ..):
        options.addOption(JxlintOption.CATEGORY, Category.LINT.toString());
        assertThat(options.hasOption(JxlintOption.CATEGORY)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.CATEGORY)).isEqualTo(Category.LINT.toString());
        assertThat(options.getEnabledCategories()).isPresent();
        assertThat(options.getEnabledCategories().get()).isEqualTo(Collections.singletonList(Category.LINT));

        //Set with setEnabledCategories(..):
        options.setEnabledCategories(Arrays.asList(Category.CORRECTNESS, Category.PERFORMANCE));
        assertThat(options.hasOption(JxlintOption.CATEGORY)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.CATEGORY))
                .isEqualTo(Category.CORRECTNESS.toString() + "," + Category.PERFORMANCE.toString());
        assertThat(options.getEnabledCategories()).isPresent();
        assertThat(options.getEnabledCategories().get())
                .isEqualTo(Arrays.asList(Category.CORRECTNESS, Category.PERFORMANCE));

        //Remove with setEnabledCategories(null):
        options.setEnabledCategories(null);
        assertThat(options.hasOption(JxlintOption.CATEGORY)).isEqualTo(false);
        assertThat(options.getOption(JxlintOption.CATEGORY)).isEqualTo(null);
        assertThat(options.getEnabledCategories()).isNotPresent();

        //Set to an invalid value:
        options.addOption(JxlintOption.CATEGORY, "xyz");
        assertThat(options.hasOption(JxlintOption.CATEGORY)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.CATEGORY)).isEqualTo("xyz");
        assertThatExceptionOfType(NonExistentCategoryException.class).isThrownBy(() -> {
            options.getEnabledCategories();
        }).withMessage(
                "Category 'xyz' does not exist. Try one of: LINT, CORRECTNESS, PERFORMANCE, SECURITY, STYLE.");
    }

    @Test
    public void testSetAndGetCheckRules() throws Exception {
        ProgramOptions options = new ProgramOptions();
        //Option not set:
        assertThat(options.hasOption(JxlintOption.CHECK)).isEqualTo(false);
        assertThat(options.getOption(JxlintOption.CHECK)).isEqualTo(null);
        assertThat(options.getCheckRules()).isNotPresent();

        //Set with addOption(JxlintOption.CHECK, ..):
        options.addOption(JxlintOption.CHECK, RULE_NAME3);
        assertThat(options.hasOption(JxlintOption.CHECK)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.CHECK)).isEqualTo(RULE_NAME3);
        assertThat(options.getCheckRules()).isPresent();
        assertThat(options.getCheckRules().get()).isEqualTo(Collections.singletonList(RULE3));

        //Set with setCheckRules(..):
        options.setCheckRules(Arrays.asList(RULE1, RULE2));
        assertThat(options.hasOption(JxlintOption.CHECK)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.CHECK))
                .isEqualTo(RULE_NAME1 + "," + RULE_NAME2);
        assertThat(options.getCheckRules()).isPresent();
        assertThat(options.getCheckRules().get())
                .isEqualTo(Arrays.asList(RULE1, RULE2));

        //Remove with setCheckRules(null):
        options.setCheckRules(null);
        assertThat(options.hasOption(JxlintOption.CHECK)).isEqualTo(false);
        assertThat(options.getOption(JxlintOption.CHECK)).isEqualTo(null);
        assertThat(options.getCheckRules()).isNotPresent();

        //Set to an invalid value:
        options.addOption(JxlintOption.CHECK, "xyz");
        assertThat(options.hasOption(JxlintOption.CHECK)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.CHECK)).isEqualTo("xyz");
        assertThatExceptionOfType(NonExistentLintRuleException.class).isThrownBy(() -> {
            options.getCheckRules();
        }).withMessage(
                "Lint rule 'xyz' does not exist.");
    }

    @Test
    public void testSetAndGetDisableRules() throws Exception {
        ProgramOptions options = new ProgramOptions();
        //Option not set:
        assertThat(options.hasOption(JxlintOption.DISABLE)).isEqualTo(false);
        assertThat(options.getOption(JxlintOption.DISABLE)).isEqualTo(null);
        assertThat(options.getDisabledRules()).isNotPresent();

        //Set with addOption(JxlintOption.DISABLE, ..):
        options.addOption(JxlintOption.DISABLE, RULE_NAME3);
        assertThat(options.hasOption(JxlintOption.DISABLE)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.DISABLE)).isEqualTo(RULE_NAME3);
        assertThat(options.getDisabledRules()).isPresent();
        assertThat(options.getDisabledRules().get()).isEqualTo(Collections.singletonList(RULE3));

        //Set with setDisabledRules(..):
        options.setDisabledRules(Arrays.asList(RULE1, RULE2));
        assertThat(options.hasOption(JxlintOption.DISABLE)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.DISABLE))
                .isEqualTo(RULE_NAME1 + "," + RULE_NAME2);
        assertThat(options.getDisabledRules()).isPresent();
        assertThat(options.getDisabledRules().get())
                .isEqualTo(Arrays.asList(RULE1, RULE2));

        //Remove with setDisabledRules(null):
        options.setDisabledRules(null);
        assertThat(options.hasOption(JxlintOption.DISABLE)).isEqualTo(false);
        assertThat(options.getOption(JxlintOption.DISABLE)).isEqualTo(null);
        assertThat(options.getEnabledCategories()).isNotPresent();

        //Set to an invalid value:
        options.addOption(JxlintOption.DISABLE, "xyz");
        assertThat(options.hasOption(JxlintOption.DISABLE)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.DISABLE)).isEqualTo("xyz");
        assertThatExceptionOfType(NonExistentLintRuleException.class).isThrownBy(() -> {
            options.getDisabledRules();
        }).withMessage(
                "Lint rule 'xyz' does not exist.");
    }

    @Test
    public void testSetAndGetEnableRules() throws Exception {
        ProgramOptions options = new ProgramOptions();
        //Option not set:
        assertThat(options.hasOption(JxlintOption.ENABLE)).isEqualTo(false);
        assertThat(options.getOption(JxlintOption.ENABLE)).isEqualTo(null);
        assertThat(options.getEnabledRules()).isNotPresent();

        //Set with addOption(JxlintOption.ENABLE, ..):
        options.addOption(JxlintOption.ENABLE, RULE_NAME3);
        assertThat(options.hasOption(JxlintOption.ENABLE)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.ENABLE)).isEqualTo(RULE_NAME3);
        assertThat(options.getEnabledRules()).isPresent();
        assertThat(options.getEnabledRules().get()).isEqualTo(Collections.singletonList(RULE3));

        //Set with setEnabledRules(..):
        options.setEnabledRules(Arrays.asList(RULE1, RULE2));
        assertThat(options.hasOption(JxlintOption.ENABLE)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.ENABLE))
                .isEqualTo(RULE_NAME1 + "," + RULE_NAME2);
        assertThat(options.getEnabledRules()).isPresent();
        assertThat(options.getEnabledRules().get())
                .isEqualTo(Arrays.asList(RULE1, RULE2));

        //Remove with setEnabledRules(null):
        options.setEnabledRules(null);
        assertThat(options.hasOption(JxlintOption.ENABLE)).isEqualTo(false);
        assertThat(options.getOption(JxlintOption.ENABLE)).isEqualTo(null);
        assertThat(options.getEnabledCategories()).isNotPresent();

        //Set to an invalid value:
        options.addOption(JxlintOption.ENABLE, "xyz");
        assertThat(options.hasOption(JxlintOption.ENABLE)).isEqualTo(true);
        assertThat(options.getOption(JxlintOption.ENABLE)).isEqualTo("xyz");
        assertThatExceptionOfType(NonExistentLintRuleException.class).isThrownBy(() -> {
            options.getEnabledRules();
        }).withMessage(
                "Lint rule 'xyz' does not exist.");
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

    public static class FourLintRulesTestImpl extends AbstractLintRules {

        @Override
        public void initializeLintRules() {
            lintRules.add(RULE1);
            lintRules.add(RULE2);
            lintRules.add(RULE3);
            lintRules.add(RULE4);
        }
    }

}
