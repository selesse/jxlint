package com.selesse.jxlint.report;

import com.selesse.jxlint.model.rules.LintError;

import java.io.PrintStream;
import java.util.List;

public abstract class Reporter {
    protected PrintStream out;
    protected List<LintError> lintErrorList;

    public Reporter(PrintStream out, List<LintError> lintErrorList) {
        this.out = out;
        this.lintErrorList = lintErrorList;
    }

    public void outputReport() {
        printHeader();
        for (LintError error : lintErrorList) {
            printError(error);
        }
        printFooter();
    }

    protected abstract void printHeader();
    protected abstract void printError(LintError error);
    protected abstract void printFooter();
}
