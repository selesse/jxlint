package com.selesse.jxlint.report;

import com.selesse.jxlint.TestFileCreator;
import com.selesse.jxlint.linter.LintFactory;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.model.OutputType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseReportTest extends AbstractReportTest {
    @Test(expected = UnableToCreateReportException.class)
    public void testTryingToWriteBadReportThrowsRightException() throws UnableToCreateReportException {
        // First, create a bad encoding file and assert that there are errors
        TestFileCreator.createBadEncodingFile(tempDirectory);

        setupTestLinterAndRunProgramWithArgs(new String[]{"--html", "!@@!@?!@!?@?!@??/////1!?!?!??//",
                tempDirectory.getAbsolutePath()});
        Linter linter = LintFactory.getInstance();
        assertEquals(1, linter.getLintErrors().size());

        Reporters.createReporter(linter.getLintErrors(), OutputType.HTML, "////////////////");
    }
}
