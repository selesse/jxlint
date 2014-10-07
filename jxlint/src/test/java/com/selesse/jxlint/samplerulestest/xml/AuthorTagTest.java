package com.selesse.jxlint.samplerulestest.xml;

import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorTagTest extends AbstractPassFailFileXmlFileTest {
    public AuthorTagTest() {
        super(new AuthorTagRule());
    }

    @Test
    public void testAuthorTagTestSecondFail() {
        File file = new File(sourceDirectory, "2");

        assertThat(lintRule.passesValidation(file)).isFalse();
    }

    @Test
    public void testAuthorTagTestThirdFail() {
        File file = new File(sourceDirectory, "3");

        assertThat(lintRule.passesValidation(file)).isFalse();
    }
}
