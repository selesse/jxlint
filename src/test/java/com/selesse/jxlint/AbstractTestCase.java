package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AbstractTestCase {
    @Rule
    public final ExpectedSystemExit expectedSystemExit = ExpectedSystemExit.none();

    protected String checkLint(String[] args, List<File> files) {
        PrintStream previousOut = System.out;
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output));

            Main.main(args);

            return output.toString();
        } finally {
            System.setOut(previousOut);
        }
    }

    protected void runTest(String[] args, List<File> files, String expectedOutput) {
        assertEquals(expectedOutput, checkLint(args, files));
    }

    /**
     * A test that's expected to System.exit() with the {@link com.selesse.jxlint.model.ExitType}, and output {@code expectedOutput}.
     * @param args Command line arguments passed to program
     * @param files List of files
     * @param expectedOutput String of the expected output
     * @param exitType Expected {@link com.selesse.jxlint.model.ExitType}
     */
    protected void runExitTest(String[] args, List<File> files, final String expectedOutput, ExitType exitType) {
        expectedSystemExit.expectSystemExitWithStatus(exitType.getErrorCode());
        expectedSystemExit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                assertEquals(expectedOutput, Main.getOutputMessage());
            }
        });

        checkLint(args, files);
    }
}
