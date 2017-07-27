package com.selesse.jxlint.samplerulestest.textfiles;

import com.selesse.jxlint.samplerules.textfiles.rules.MustHaveAuthor;

public class MustHaveAuthorTest extends AbstractPassFailFileTextFileTest {
    public MustHaveAuthorTest() {
        super(new MustHaveAuthor());
    }
}
