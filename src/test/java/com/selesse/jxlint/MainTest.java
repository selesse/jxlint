package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;
import org.junit.Test;

public class MainTest extends AbstractTestCase {
    @Test
    public void testMutuallyExclusiveArgumentsGivesError() {
        final String expectedOutput = "Only one of --html, --quiet, --xml must be selected.";
        runExitTest(new String[]{"--quiet", "--xml"}, null, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testBadArgumentGivesProperErrorCode() {
        expectedSystemExit.expectSystemExitWithStatus(ExitType.COMMAND_LINE_ERROR.getErrorCode());

        checkLint(new String[]{"---"}, null);
    }
}
