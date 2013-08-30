package com.selesse.jxlint.report;

import com.selesse.jxlint.model.rules.LintError;

import java.io.PrintStream;
import java.util.List;

public class DefaultReporter extends Reporter {

    public DefaultReporter(PrintStream out, List<LintError> lintErrorList) {
        super(out, lintErrorList);
    }

    @Override
    public void printHeader() {
    }

    @Override
    public void printError(LintError error) {
        out.println(error.toString());
    }

    @Override
    public void printFooter() {
    }
}
