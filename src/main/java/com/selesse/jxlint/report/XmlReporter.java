package com.selesse.jxlint.report;

import com.selesse.jxlint.model.rules.LintError;

import java.io.PrintStream;
import java.util.List;

public class XmlReporter extends Reporter {

    public XmlReporter(PrintStream out, List<LintError> lintErrorList) {
        super(out, lintErrorList);
    }

    @Override
    public void printHeader() {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\">");
        out.println("<issues>");
    }

    @Override
    public void printError(LintError error) {
        out.println("<issue>" + error + "</issue>");
    }

    @Override
    public void printFooter() {
        out.println("</issues>");
    }
}
