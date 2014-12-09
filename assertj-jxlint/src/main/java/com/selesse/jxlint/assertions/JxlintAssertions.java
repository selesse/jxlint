package com.selesse.jxlint.assertions;

import com.selesse.jxlint.model.rules.LintError;
import org.assertj.core.api.Assertions;

public class JxlintAssertions extends Assertions {
    public static LintErrorAssertion assertThat(LintError actual) {
        return new LintErrorAssertion(actual);
    }
}
