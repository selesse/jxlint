package com.selesse.jxlint.report;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.AbstractTestCase;
import com.selesse.jxlint.Jxlint;
import com.selesse.jxlint.TestFileCreator;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import com.selesse.jxlint.settings.JxlintProgramSettings;

import org.junit.Before;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractReportTest extends AbstractTestCase {
    protected File tempDirectory;

    @Before
    public void setup() {
        tempDirectory = Files.createTempDir();
        tempDirectory.deleteOnExit();

        LintRulesImpl.setInstance(new XmlLintRulesTestImpl());
    }

    protected void setupTestLinterAndRunProgramWithArgs(String[] args) {
        Jxlint jxlint = new Jxlint(new XmlLintRulesTestImpl(), new JxlintProgramSettings(), false);
        jxlint.parseArgumentsAndDispatch(args);
    }

    protected File ensureReportGetsCreatedFor2Errors(OutputType outputType) throws UnableToCreateReportException {
        Map<JxlintOption, String> additionalOptions = Collections.singletonMap(JxlintOption.ALL_WARNINGS, null);
        return ensureReportGetsCreated(outputType, additionalOptions, 2);
    }

    protected File ensureReportGetsCreated(OutputType outputType, Map<JxlintOption, String> additionalOptions,
            int expectedErrors) throws UnableToCreateReportException {

        // Create some files that will trigger rule violations
        TestFileCreator.createBadAuthorFile(tempDirectory);
        TestFileCreator.createBadEncodingFile(tempDirectory);

        String desiredName = "test." + outputType.extension();
        File desiredFile = new File(tempDirectory, desiredName);
        String desiredOutputPath = desiredFile.getAbsolutePath();

        assertThat(desiredFile).doesNotExist();

        setupTestLinterAndRunProgramWithArgs(
                createArgs("--" + outputType.extension(), desiredOutputPath, additionalOptions));
        Linter linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors()).hasSize(expectedErrors);

        ProgramOptions programOptions = new ProgramOptions();
        addOptions(programOptions, additionalOptions);
        programOptions.addOption(JxlintOption.OUTPUT_TYPE, outputType.extension());
        programOptions.addOption(JxlintOption.OUTPUT_TYPE_PATH, desiredOutputPath);

        Reporter reporter = Reporters.createReporter(linter.getLintErrors(), new JxlintProgramSettings(),
                programOptions);
        reporter.writeReport();

        assertThat(desiredFile).exists();
        desiredFile.deleteOnExit();

        return desiredFile;
    }

    private void addOptions(ProgramOptions programOptions, Map<JxlintOption, String> additionalOptions) {
        for (Entry<JxlintOption, String> e : additionalOptions.entrySet()) {
            if (e.getValue() != null) {
                programOptions.addOption(e.getKey(), e.getValue());
            }
            else {
                programOptions.addOption(e.getKey());
            }
        }
    }

    private String[] createArgs(String outputTypeOption, String outputTypeValue,
            Map<JxlintOption, String> additionalOptions) {
        List<String> list = Lists.newArrayList();
        for (Entry<JxlintOption, String> e : additionalOptions.entrySet()) {
            list.add("--" + e.getKey().getOptionString());
            if (e.getValue() != null) {
                list.add(e.getValue());
            }
        }
        list.add(outputTypeOption);
        list.add(outputTypeValue);
        list.add(tempDirectory.getAbsolutePath());
        return list.toArray(new String[list.size()]);
    }
}
