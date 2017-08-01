package com.selesse.jxlint.maven;

import com.google.common.io.Files;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.settings.ProgramSettings;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class AbstractJxlintMojoTest {

    private static final String SRC_PATH_PREFIX_VALUE = "https://github.com/selesse/jxlint/blob/master/jxlint-impl/";
    private static final String CATEGORY_NAME = Category.CORRECTNESS.toString();
    private static final String RULE_NAME = (new SampleRule()).getName();
    private TestJxlintImplMojo mojoUnderTest;
    protected File tempDirectory;

    @Before
    public void setup() {
        tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();

        mojoUnderTest = new TestJxlintImplMojo();
        mojoUnderTest.callInitJxlint();
    }

    private void initValidConfiguration() {
        mojoUnderTest.setSourceDirectory(tempDirectory);
        mojoUnderTest.setDisableRules(null);
        mojoUnderTest.setEnableOnlyRules(null);
        mojoUnderTest.setEnableRules(null);
        mojoUnderTest.setEnableCategories(null);
        mojoUnderTest.setNoWarnings(false);
        mojoUnderTest.setAllWarnings(false);
        mojoUnderTest.setWaringsAreErrors(false);
        mojoUnderTest.setOutputType("html");
        mojoUnderTest.setOutputFile(new File(tempDirectory, "my-report.html"));
        mojoUnderTest.setSrcPathPrefix(null);
    }

    @Test
    public void testValidConfiguration() throws Exception {
        initValidConfiguration();

        ProgramOptions options = mojoUnderTest.callCreateProgramOptions();
        assertThat(options.getSourceDirectory()).isEqualTo(tempDirectory.getAbsolutePath());
        assertThat(options.hasOption(JxlintOption.DISABLE)).isEqualTo(false);
        assertThat(options.hasOption(JxlintOption.CHECK)).isEqualTo(false);
        assertThat(options.hasOption(JxlintOption.ENABLE)).isEqualTo(false);
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

        mojoUnderTest.setSourceDirectory(srcFolder);
        mojoUnderTest.setDisableRules(Collections.singletonList(RULE_NAME));
        mojoUnderTest.setEnableOnlyRules(Collections.singletonList(RULE_NAME));
        mojoUnderTest.setEnableRules(Collections.singletonList(RULE_NAME));
        mojoUnderTest.setEnableCategories(Collections.singletonList(CATEGORY_NAME));
        mojoUnderTest.setNoWarnings(true);
        mojoUnderTest.setAllWarnings(true);
        mojoUnderTest.setWaringsAreErrors(true);
        mojoUnderTest.setOutputType("xml");
        mojoUnderTest.setOutputFile(reportFile);
        mojoUnderTest.setSrcPathPrefix(SRC_PATH_PREFIX_VALUE);

        ProgramOptions options = mojoUnderTest.callCreateProgramOptions();
        assertThat(options.getSourceDirectory()).isEqualTo(srcFolder.getAbsolutePath());
        assertThat(options.getOption(JxlintOption.DISABLE)).isEqualTo(RULE_NAME);
        assertThat(options.getOption(JxlintOption.CHECK)).isEqualTo(RULE_NAME);
        assertThat(options.getOption(JxlintOption.ENABLE)).isEqualTo(RULE_NAME);
        assertThat(options.getOption(JxlintOption.CATEGORY)).isEqualTo(CATEGORY_NAME);
        assertThat(options.hasOption(JxlintOption.NO_WARNINGS)).isEqualTo(true);
        assertThat(options.hasOption(JxlintOption.ALL_WARNINGS)).isEqualTo(true);
        assertThat(options.hasOption(JxlintOption.WARNINGS_ARE_ERRORS)).isEqualTo(true);
        assertThat(options.getOutputType()).isEqualTo(OutputType.XML);
        assertThat(options.getOption(JxlintOption.OUTPUT_TYPE_PATH)).isEqualTo(reportFile.getAbsolutePath());
        assertThat(options.getOption(JxlintOption.SRC_PATH_PREFIX)).isEqualTo(SRC_PATH_PREFIX_VALUE);
    }

    @Test
    public void testOutputTypeQuiet() throws Exception {
        initValidConfiguration();
        mojoUnderTest.setOutputType("quiet");

        ProgramOptions options = mojoUnderTest.callCreateProgramOptions();
        assertThat(options.getOutputType()).isEqualTo(OutputType.QUIET);
    }

    @Test
    public void testOutputTypeDefault() throws Exception {
        initValidConfiguration();
        mojoUnderTest.setOutputType("default");

        ProgramOptions options = mojoUnderTest.callCreateProgramOptions();
        assertThat(options.getOutputType()).isEqualTo(OutputType.DEFAULT);
    }

    @Test
    public void testInvalidOutputType() throws Exception {
        initValidConfiguration();
        mojoUnderTest.setOutputType("xxx");

        ProgramOptions options = mojoUnderTest.callCreateProgramOptions();
        assertThat(options.getOutputType()).isEqualTo(OutputType.DEFAULT);
    }

    @Test
    public void testInvalidRuleNameType() throws Exception {
        try {
            initValidConfiguration();
            mojoUnderTest.setEnableOnlyRules(Collections.singletonList("abcd"));

            mojoUnderTest.callCreateProgramOptions();
            fail("MojoExecutionException expected");
        }
        catch (MojoExecutionException e) {
            assertThat(e.getMessage()).isEqualTo("Lint rule 'abcd' does not exist.");
        }
    }

    @Test
    public void testInvalidCategoryNameType() throws Exception {
        try {
            initValidConfiguration();
            mojoUnderTest.setEnableCategories(Collections.singletonList("xyz"));

            mojoUnderTest.callCreateProgramOptions();
            fail("MojoExecutionException expected");
        }
        catch (MojoExecutionException e) {
            assertThat(e.getMessage()).isEqualTo(
                    "Category \"xyz\" does not exist. Try one of: LINT, CORRECTNESS, PERFORMANCE, SECURITY, STYLE.");
        }
    }

    private static class TestJxlintImplMojo extends AbstractJxlintMojo {

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

        public void setSourceDirectory(File sourceDirectory) {
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

        public void setWaringsAreErrors(boolean waringsAreErrors) {
            this.waringsAreErrors = waringsAreErrors;
        }

        public void setOutputType(String outputType) {
            this.outputType = outputType;
        }

        public void setOutputFile(File outputFile) {
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
