package com.selesse.jxlint.report;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintError;

import java.io.PrintStream;
import java.util.List;

public class HtmlReporter extends Reporter {

    public HtmlReporter(PrintStream out, List<LintError> lintErrorList) {
        super(out, lintErrorList);
    }

    @Override
    public void printHeader() {
        List<String> headerStringList = Lists.newArrayList(
            "<html>",
            "<head>",
            "<title> JXLint - Report </title>",
            "</head>",
            "<body>",
            "<table>"
        );

        out.println(Joiner.on("\n").join(headerStringList));
    }

    @Override
    public void printError(LintError error) {
        out.println("<tr> <td> " + error + "</td> </tr>");
    }

    @Override
    public void printFooter() {
        List<String> footerStringList = Lists.newArrayList(
                "</table>",
                "</body>",
                "</html>"
        );

        out.println(Joiner.on("\n").join(footerStringList));
    }
}
