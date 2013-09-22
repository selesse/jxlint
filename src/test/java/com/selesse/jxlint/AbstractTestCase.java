package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class AbstractTestCase {
    @Rule
    public final ExpectedSystemExit expectedSystemExit = ExpectedSystemExit.none();
    public final File pwd = new File("./");

    protected String checkLint(String[] args, File sourceDirectory) {
        PrintStream previousOut = System.out;
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output));

            String[] newArgs = Arrays.copyOfRange(args, 0, args.length + 1);
            newArgs[args.length] = sourceDirectory.getAbsolutePath();

            Main.main(newArgs);

            return output.toString();
        } finally {
            System.setOut(previousOut);
        }
    }

    /**
     * A test that's expected to System.exit() with the {@link com.selesse.jxlint.model.ExitType}, and output {@code expectedOutput}.
     * @param args Command line arguments passed to program
     * @param sourceDirectory Source directory where we're running tests
     * @param expectedOutput String of the expected output
     * @param exitType Expected {@link com.selesse.jxlint.model.ExitType}
     */
    protected void runExitTest(String[] args, File sourceDirectory, final String expectedOutput, ExitType exitType) {
        if (args == null) {
            args = new String[] {};
        }
        expectedSystemExit.expectSystemExitWithStatus(exitType.getErrorCode());
        expectedSystemExit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                assertEquals(expectedOutput, Main.getOutputMessage());
            }
        });

        checkLint(args, sourceDirectory);
    }
}
