package com.selesse.jxlint.testassertions;

import com.selesse.jxlint.ProgramExitter;
import org.junit.contrib.java.lang.system.Assertion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class RegularExpressionAssertion implements Assertion {
    private final Pattern pattern;

    public RegularExpressionAssertion(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void checkAssertion() throws Exception {
        Matcher matcher = pattern.matcher(ProgramExitter.getOutputMessage());
        assertTrue(matcher.find());
    }
}

