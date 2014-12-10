package com.selesse.jxlint.report;

import com.selesse.jxlint.TestFileCreator;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(linter.getLintErrors()).hasSize(1);

        ProgramOptions options = new ProgramOptions();
        options.addOption(JxlintOption.OUTPUT_TYPE, "html");
        options.addOption(JxlintOption.OUTPUT_TYPE_PATH, "/////////////////");
        Reporters.createReporter(linter.getLintErrors(), new JxlintProgramSettings(), options);
    }
}
