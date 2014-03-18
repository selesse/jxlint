package com.selesse.jxlint.report;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.html.HtmlEscapers;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.utils.EnumUtils;

import java.io.PrintStream;
import java.util.List;

/**
 * The HtmlReporter provides a basic HTML page with the error report included as a table.
 */
public class HtmlReporter extends Reporter {

    public HtmlReporter(PrintStream out, List<LintError> lintErrorList) {
        super(out, lintErrorList);
    }

    @Override
    public void printHeader() {
        List<String> headers = getKeysList();

        List<String> headerStringList = Lists.newArrayList(
                "<html>",
                "<head>",
                "<title> JXLint - Report </title>",
                "</head>",
                "<body>",
                "<table>",
                "<tr>",
                Joiner.on("\n").join(surroundAndHtmlEscapeAll(headers, "<th> ", " </th>")),
                "</tr>"
        );

        out.println(Joiner.on("\n").join(headerStringList));
    }

    private Iterable<String> surroundAndHtmlEscapeAll(Iterable<String> iterable,
                                                      final String before, final String after) {
        return Iterables.transform(iterable, new Function<String, String>() {

            @Override
            public String apply(String input) {
                return before + htmlEncode(input) + after;
            }
        });
    }

    @Override
    protected void printCategoryHeader(Category category) {}

    @Override
    public void printError(LintError error) {
        List<String> parameterValues = getParameterValues(error);

        out.println("<tr>");
        out.println(Joiner.on("\n").join(surroundAndHtmlEscapeAll(parameterValues, "<td> ", " </td>")));
        out.println("</tr>");
    }

    private String htmlEncode(String string) {
        return HtmlEscapers.htmlEscaper().escape(string);
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

    private List<String> getKeysList() {
        return Lists.newArrayList("Name", "Category", "Severity", "Message", "Summary", "Explanation", "Location");
    }

    private List<String> getParameterValues(LintError error) {
        LintRule rule = error.getViolatedRule();

        return Lists.newArrayList(
                rule.getName(),
                EnumUtils.toHappyString(rule.getCategory()),
                EnumUtils.toHappyString(rule.getSeverity()),
                error.getMessage(),
                rule.getSummary(),
                rule.getDetailedDescription(),
                error.getFile().getAbsolutePath()
        );
    }

}
