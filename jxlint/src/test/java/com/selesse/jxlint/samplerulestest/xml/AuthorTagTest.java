package com.selesse.jxlint.samplerulestest.xml;

import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;

public class AuthorTagTest extends AbstractPassFailFileXmlFileTest {
    public AuthorTagTest() {
        super(new AuthorTagRule());
    }

    @Test
    public void testAuthorTagTestSecondFail() {
        File file = new File(sourceDirectory, "2");

        assertFalse(lintRule.passesValidation(file));
    }

    @Test
    public void testAuthorTagTestThirdFail() {
        File file = new File(sourceDirectory, "3");

        assertFalse(lintRule.passesValidation(file));
    }
}
