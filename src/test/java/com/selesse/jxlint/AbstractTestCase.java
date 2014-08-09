package com.selesse.jxlint;

import com.google.common.base.Charsets;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import com.selesse.jxlint.testassertions.EqualsAssertion;
import com.selesse.jxlint.testassertions.RegularExpressionAssertion;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

public class AbstractTestCase {
    @Rule
    public final ExpectedSystemExit expectedSystemExit = ExpectedSystemExit.none();
    public final File pwd = new File("./");

    protected String checkLint(String[] args, File sourceDirectory, boolean useSystemExit) {
        PrintStream previousOut = System.out;
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output, true, Charsets.UTF_8.displayName()));

            String[] newArgs = Arrays.copyOfRange(args, 0, args.length + 1);
            newArgs[args.length] = sourceDirectory.getAbsolutePath();

            Jxlint jxlint = new Jxlint(new XmlLintRulesTestImpl(), new JxlintProgramSettings(), useSystemExit);
            jxlint.parseArgumentsAndDispatch(newArgs);

            return output.toString(Charsets.UTF_8.displayName());
        }
        catch (UnsupportedEncodingException e) {
            fail("Threw exception while setting up test:\n" + e.getMessage());
        }
        finally {
            System.setOut(previousOut);
        }
        return "";
    }

    protected String checkLint(String[] args, File sourceDirectory) {
        return checkLint(args, sourceDirectory, LintRulesImpl.willExitAfterReporting());
    }

    /**
     * A test that's expected to System.exit() with the {@link com.selesse.jxlint.model.ExitType},
     * and output {@code expectedOutput}.
     * @param args Command line arguments passed to program
     * @param sourceDirectory Source directory where we're running tests
     * @param expectedOutput String of the expected output
     * @param exitType Expected {@link com.selesse.jxlint.model.ExitType}
     */
    protected void runExitTest(String[] args, File sourceDirectory, final String expectedOutput, ExitType exitType) {
        runExitTestWithAssertion(args, sourceDirectory, new EqualsAssertion(expectedOutput), exitType);
    }

    protected void runExitTestRegex(String[] args, File sourceDirectory, final Pattern pattern, ExitType exitType) {
        runExitTestWithAssertion(args, sourceDirectory, new RegularExpressionAssertion(pattern), exitType);
    }

    private void runExitTestWithAssertion(String[] args, File sourceDirectory, Assertion assertion, ExitType exitType) {
        if (args == null) {
            args = new String[] {};
        }

        expectedSystemExit.expectSystemExitWithStatus(exitType.getErrorCode());
        expectedSystemExit.checkAssertionAfterwards(assertion);

        checkLint(args, sourceDirectory, true);
    }

    protected void runExitTest(String[] args, File sourceDirectory, ExitType exitType) {
        if (args == null) {
            args = new String[] {};
        }

        expectedSystemExit.expectSystemExitWithStatus(exitType.getErrorCode());
        checkLint(args, sourceDirectory, true);
    }
}
