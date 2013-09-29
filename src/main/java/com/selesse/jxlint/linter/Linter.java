package com.selesse.jxlint.linter;

import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;

import java.util.List;

public interface Linter {
    void doLint(ProgramOptions programOptions);
    List<LintError> getLintErrors();
}
