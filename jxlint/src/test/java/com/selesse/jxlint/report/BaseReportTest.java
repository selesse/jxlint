package com.selesse.jxlint.report;

import com.selesse.jxlint.TestFileCreator;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class BaseReportTest extends AbstractReportTest {
    @Test(expected = UnableToCreateReportException.class)
    public void testTryingToWriteBadReportThrowsRightException() throws UnableToCreateReportException {
        // First, create a bad encoding file and assert that there are errors
        TestFileCreator.createBadEncodingFile(tempDirectory);

        setupTestLinterAndRunProgramWithArgs(new String[]{"--html", "index.html",
                tempDirectory.getAbsolutePath()});
        File indexFile = new File("index.html");
        if (indexFile.exists()) {
            indexFile.deleteOnExit();
        }
        Linter linter = LinterFactory.getInstance();
        assertEquals(1, linter.getLintErrors().size());

        Reporters.createReporter(linter.getLintErrors(), new JxlintProgramSettings(),
                OutputType.HTML, "////////////////");
    }
}
