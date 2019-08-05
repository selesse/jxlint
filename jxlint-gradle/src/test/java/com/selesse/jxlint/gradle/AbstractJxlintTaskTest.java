package com.selesse.jxlint.gradle;

import com.google.common.io.Files;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.settings.ProgramSettings;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.internal.impldep.org.apache.maven.plugin.MojoExecutionException;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class AbstractJxlintTaskTest {

    private static final String SRC_PATH_PREFIX_VALUE = "https://github.com/selesse/jxlint/blob/master/jxlint-impl/";
    private static final String CATEGORY_NAME = Category.CORRECTNESS.toString();
    private static final String RULE_NAME = (new SampleRule()).getName();
    private TestJxlintImplTask taskUnderTest;
    protected File tempDirectory;

    @Before
    public void setup() {
        tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();

        Project project = ProjectBuilder.builder().build();
        taskUnderTest = project.getTasks().create("samplejxlint", TestJxlintImplTask.class);
        taskUnderTest.callInitJxlint();
    }

    private void initValidConfiguration() {
        taskUnderTest.setSourceDirectory(tempDirectory.getAbsolutePath());
        taskUnderTest.setDisableRules(null);
        taskUnderTest.setEnableOnlyRules(null);
        taskUnderTest.setEnableRules(null);
        taskUnderTest.setEnableCategories(null);
        taskUnderTest.setNoWarnings(false);
        taskUnderTest.setAllWarnings(false);
        taskUnderTest.setWarningsAreErrors(false);
        taskUnderTest.setOutputType("html");
        taskUnderTest.setOutputFile(new File(tempDirectory, "my-report.html").getAbsolutePath());
        taskUnderTest.setSrcPathPrefix(null);
    }

    @Test
    public void testValidConfiguration() throws Exception {
        initValidConfiguration();

        ProgramOptions options = taskUnderTest.callCreateProgramOptions();
        assertThat(options.getSourceDirectory()).isEqualTo(tempDirectory.getAbsolutePath());
        assertThat(options.hasOption(JxlintOption.DISABLE)).isEqualTo(false);
        assertThat(options.getDisabledRules()).isNotPresent();
        assertThat(options.hasOption(JxlintOption.CHECK)).isEqualTo(false);
        assertThat(options.getCheckRules()).isNotPresent();
        assertThat(options.hasOption(JxlintOption.ENABLE)).isEqualTo(false);
        assertThat(options.getEnabledRules()).isNotPresent();
        assertThat(options.hasOption(JxlintOption.CATEGORY)).isEqualTo(false);
        assertThat(options.getEnabledCategories()).isNotPresent();
        assertThat(options.hasOption(JxlintOption.NO_WARNINGS)).isEqualTo(false);
        assertThat(options.hasOption(JxlintOption.ALL_WARNINGS)).isEqualTo(false);
        assertThat(options.hasOption(JxlintOption.WARNINGS_ARE_ERRORS)).isEqualTo(false);
        assertThat(options.getOutputType()).isEqualTo(OutputType.HTML);
        assertThat(options.getOption(JxlintOption.OUTPUT_TYPE_PATH))
                .isEqualTo(new File(tempDirectory, "my-report.html").getAbsolutePath());
        assertThat(options.hasOption(JxlintOption.SRC_PATH_PREFIX)).isEqualTo(false);
    }

    @Test
    public void testAlternativeValidConfiguration() throws Exception {
        File reportFile = new File(tempDirectory, "report.xml");
        File srcFolder = new File(tempDirectory, "src");

        taskUnderTest.setSourceDirectory(srcFolder.getAbsolutePath());
        taskUnderTest.setDisableRules(Collections.singletonList(RULE_NAME));
        taskUnderTest.setEnableOnlyRules(Collections.singletonList(RULE_NAME));
        taskUnderTest.setEnableRules(Collections.singletonList(RULE_NAME));
        taskUnderTest.setEnableCategories(Collections.singletonList(CATEGORY_NAME));
        taskUnderTest.setNoWarnings(true);
        taskUnderTest.setAllWarnings(true);
        taskUnderTest.setWarningsAreErrors(true);
        taskUnderTest.setOutputType("xml");
        taskUnderTest.setOutputFile(reportFile.getAbsolutePath());
        taskUnderTest.setSrcPathPrefix(SRC_PATH_PREFIX_VALUE);

        ProgramOptions options = taskUnderTest.callCreateProgramOptions();
        assertThat(options.getSourceDirectory()).isEqualTo(srcFolder.getAbsolutePath());
        assertThat(options.getOption(JxlintOption.DISABLE)).isEqualTo(RULE_NAME);
        assertThat(options.getDisabledRules()).isPresent()
                .contains(Collections.singletonList(new SampleRule()));
        assertThat(options.getOption(JxlintOption.CHECK)).isEqualTo(RULE_NAME);
        assertThat(options.getDisabledRules()).isPresent()
                .contains(Collections.singletonList(new SampleRule()));
        assertThat(options.getOption(JxlintOption.ENABLE)).isEqualTo(RULE_NAME);
        assertThat(options.getEnabledRules()).isPresent()
                .contains(Collections.singletonList(new SampleRule()));
        assertThat(options.getOption(JxlintOption.CATEGORY)).isEqualTo(CATEGORY_NAME);
        assertThat(options.getEnabledCategories()).isPresent()
                .contains(Collections.singletonList(Category.CORRECTNESS));
        assertThat(options.hasOption(JxlintOption.NO_WARNINGS)).isEqualTo(true);
        assertThat(options.hasOption(JxlintOption.ALL_WARNINGS)).isEqualTo(true);
        assertThat(options.hasOption(JxlintOption.WARNINGS_ARE_ERRORS)).isEqualTo(true);
        assertThat(options.getOutputType()).isEqualTo(OutputType.XML);
        assertThat(options.getOption(JxlintOption.OUTPUT_TYPE_PATH)).isEqualTo(reportFile.getAbsolutePath());
        assertThat(options.getOption(JxlintOption.SRC_PATH_PREFIX)).isEqualTo(SRC_PATH_PREFIX_VALUE);
    }

    @Test
    public void testJenkinsXmlValidConfiguration() throws Exception {
        File reportFile = new File(tempDirectory, "jenkins-report.xml");
        File srcFolder = new File(tempDirectory, "src");

        taskUnderTest.setSourceDirectory(srcFolder.getAbsolutePath());
        taskUnderTest.setOutputType("jenkins-xml");
        taskUnderTest.setOutputFile(reportFile.getAbsolutePath());

        ProgramOptions options = taskUnderTest.callCreateProgramOptions();
        assertThat(options.getSourceDirectory()).isEqualTo(srcFolder.getAbsolutePath());
        assertThat(options.getOutputType()).isEqualTo(OutputType.JENKINS_XML);
        assertThat(options.getOption(JxlintOption.OUTPUT_TYPE_PATH)).isEqualTo(reportFile.getAbsolutePath());
    }

    @Test
    public void testOutputTypeQuiet() throws Exception {
        initValidConfiguration();
        taskUnderTest.setOutputType("quiet");

        ProgramOptions options = taskUnderTest.callCreateProgramOptions();
        assertThat(options.getOutputType()).isEqualTo(OutputType.QUIET);
    }

    @Test
    public void testOutputTypeDefault() throws Exception {
        initValidConfiguration();
        taskUnderTest.setOutputType("default");

        ProgramOptions options = taskUnderTest.callCreateProgramOptions();
        assertThat(options.getOutputType()).isEqualTo(OutputType.DEFAULT);
    }

    @Test
    public void testInvalidOutputType() throws Exception {
        initValidConfiguration();
        taskUnderTest.setOutputType("xxx");

        ProgramOptions options = taskUnderTest.callCreateProgramOptions();
        assertThat(options.getOutputType()).isEqualTo(OutputType.DEFAULT);
    }

    @Test
    public void testInvalidRuleNameType() throws Exception {
        assertThatExceptionOfType(InvalidUserDataException.class).isThrownBy(() -> {
            initValidConfiguration();
            taskUnderTest.setEnableOnlyRules(Collections.singletonList("abcd"));

            taskUnderTest.callCreateProgramOptions();
        }).withMessage("Lint rule 'abcd' does not exist.");
    }

    @Test
    public void testInvalidCategoryNameType() throws Exception {
        assertThatExceptionOfType(InvalidUserDataException.class).isThrownBy(() -> {
            initValidConfiguration();
            taskUnderTest.setEnableCategories(Collections.singletonList("xyz"));

            taskUnderTest.callCreateProgramOptions();
        }).withMessage(
                "Category 'xyz' does not exist. Try one of: LINT, CORRECTNESS, PERFORMANCE, SECURITY, STYLE.");
    }

    @Test
    public void testPublicConstantsCorrespondToFieldNames() throws Exception {
        Field[] fields = AbstractJxlintTask.class.getDeclaredFields();

        //get all protected fields:
        List<String> parameterFields = Arrays.asList(fields).stream()
                .filter(f -> Modifier.isProtected(f.getModifiers()))
                .map(f -> f.getName()).collect(Collectors.toList());

        //Compare the list against the constants defined in the class:
        assertThat(parameterFields).containsOnly(
                AbstractJxlintTask.ALL_WARNINGS,
                AbstractJxlintTask.DISABLE_RULES,
                AbstractJxlintTask.ENABLE_CATEGORY,
                AbstractJxlintTask.ENABLE_ONLY_RULES,
                AbstractJxlintTask.ENABLE_RULES,
                AbstractJxlintTask.NO_WARNINGS,
                AbstractJxlintTask.OUTPUT_FILE,
                AbstractJxlintTask.OUTPUT_TYPE,
                AbstractJxlintTask.SOURCE_DIRECTORY,
                AbstractJxlintTask.SRC_PATH_PREFIX,
                AbstractJxlintTask.WARNINGS_ARE_ERRORS);
    }

    public static class TestJxlintImplTask extends AbstractJxlintTask {

        @Override
        protected ProgramSettings provideProgramSettings() {
            return new SampleProgramSettings();
        }

        @Override
        protected LintRules provideLintRules() {
            return new SampleLintRules();
        }

        @Override
        protected Class<? extends Enum<?>> provideCategories() {
            return Category.class;
        }

        public void setSourceDirectory(String sourceDirectory) {
            this.sourceDirectory = sourceDirectory;
        }

        public void setDisableRules(List<String> disableRules) {
            this.disableRules = disableRules;
        }

        public void setEnableOnlyRules(List<String> enableOnlyRules) {
            this.enableOnlyRules = enableOnlyRules;
        }

        public void setEnableRules(List<String> enableRules) {
            this.enableRules = enableRules;
        }

        public void setEnableCategories(List<String> enableCategories) {
            this.enableCategories = enableCategories;
        }

        public void setNoWarnings(boolean noWarnings) {
            this.noWarnings = noWarnings;
        }

        public void setAllWarnings(boolean allWarnings) {
            this.allWarnings = allWarnings;
        }

        public void setWarningsAreErrors(boolean waringsAreErrors) {
            this.warningsAreErrors = waringsAreErrors;
        }

        public void setOutputType(String outputType) {
            this.outputType = outputType;
        }

        public void setOutputFile(String outputFile) {
            this.outputFile = outputFile;
        }

        public void setSrcPathPrefix(String srcPathPrefix) {
            this.srcPathPrefix = srcPathPrefix;
        }

        public void callInitJxlint() {
            initJxlint();
        }

        public ProgramOptions callCreateProgramOptions() throws MojoExecutionException {
            return createProgramOptions();
        }
    }
}