package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;
import org.junit.Test;

public class JxlintTest extends AbstractTestCase {
    @Test
    public void testMutuallyExclusiveArgumentsGivesError() {
        final String expectedOutput = "Only one of --html, --quiet, --xml must be selected.";
        runExitTest(new String[]{"--quiet", "--xml"}, pwd, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testBadArgumentGivesProperErrorCode() {
        expectedSystemExit.expectSystemExitWithStatus(ExitType.COMMAND_LINE_ERROR.getErrorCode());

        checkLint(new String[]{"---"}, pwd);
    }

    @Test
    public void testMissingArgumentIsDisplayed() {
        final String expectedOutput = "Missing argument for option '--check'.";
        runExitTest(new String[]{"--check", "--Wall"}, pwd, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }
}
