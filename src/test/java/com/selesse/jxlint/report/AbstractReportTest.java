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

import static org.junit.Assert.*;

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

        String desiredOutput = tempDirectory.getAbsolutePath() + File.separator + "test." + type.extension();
        File desiredFile = new File(desiredOutput);

        assertFalse(desiredFile.exists());

        setupTestLinterAndRunProgramWithArgs(new String[]{"--" + type.extension(), desiredOutput,
                tempDirectory.getAbsolutePath()});
        Linter linter = LinterFactory.getInstance();
        assertEquals(1, linter.getLintErrors().size());

        Reporter reporter = Reporters.createReporter(linter.getLintErrors(), new JxlintProgramSettings(),
                type, desiredOutput);
        reporter.writeReport();

        assertTrue(desiredFile.exists());
        desiredFile.deleteOnExit();

        return desiredFile;
    }
}
