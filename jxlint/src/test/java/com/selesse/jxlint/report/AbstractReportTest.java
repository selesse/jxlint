package com.selesse.jxlint.report;

import com.google.common.io.Files;
import com.selesse.jxlint.AbstractTestCase;
import com.selesse.jxlint.Jxlint;
import com.selesse.jxlint.TestFileCreator;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import org.junit.Before;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

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

    public File ensureReportGetsCreatedFor2Errors(OutputType outputType) throws UnableToCreateReportException {
        // Create some files that will trigger rule violations
        TestFileCreator.createBadAuthorFile(tempDirectory);
        TestFileCreator.createBadEncodingFile(tempDirectory);

        String desiredOutputPath = tempDirectory.getAbsolutePath() + File.separator + "test." + outputType.extension();
        File desiredFile = new File(desiredOutputPath);

        assertThat(desiredFile.exists()).isFalse();

        setupTestLinterAndRunProgramWithArgs(new String[]{"--Wall", "--" + outputType.extension(), desiredOutputPath,
                tempDirectory.getAbsolutePath()});
        Linter linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors().size()).isEqualTo(2);

        Reporter reporter = Reporters.createReporter(linter.getLintErrors(), new JxlintProgramSettings(),
                outputType, desiredOutputPath);
        reporter.writeReport();

        assertThat(desiredFile.exists()).isTrue();
        desiredFile.deleteOnExit();

        return desiredFile;
    }
}
