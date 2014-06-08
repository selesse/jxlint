package com.selesse.jxlint.testassertions;

import com.selesse.jxlint.ProgramExitter;
import org.junit.contrib.java.lang.system.Assertion;

import static org.junit.Assert.assertEquals;

public class EqualsAssertion implements Assertion {
    private final String expectedOutput;

    public EqualsAssertion(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    @Override
    public void checkAssertion() throws Exception {
        assertEquals(expectedOutput, ProgramExitter.getOutputMessage());
    }
}
