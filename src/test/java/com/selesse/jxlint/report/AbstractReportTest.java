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

    public File ensureReportGetsCreatedWithType(OutputType type) throws UnableToCreateReportException {
        // First, create a bad encoding file and assert that there are errors
        TestFileCreator.createBadEncodingFile(tempDirectory);

        String desiredOutputPath = tempDirectory.getAbsolutePath() + File.separator + "test." + type.extension();
        File desiredFile = new File(desiredOutputPath);

        assertThat(desiredFile.exists()).isFalse();

        setupTestLinterAndRunProgramWithArgs(new String[]{"--" + type.extension(), desiredOutputPath,
                tempDirectory.getAbsolutePath()});
        Linter linter = LinterFactory.getInstance();
        assertThat(linter.getLintErrors().size()).isEqualTo(1);

        Reporter reporter = Reporters.createReporter(linter.getLintErrors(), new JxlintProgramSettings(),
                type, desiredOutputPath);
        reporter.writeReport();

        assertThat(desiredFile.exists()).isTrue();
        desiredFile.deleteOnExit();

        return desiredFile;
    }
}
