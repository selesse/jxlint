package com.selesse.jxlint.report;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.settings.ProgramSettings;
import com.selesse.jxlint.utils.EnumUtils;
import com.selesse.jxlint.utils.HtmlUtils;

import java.io.PrintStream;
import java.util.List;

/**
 * The TableHtmlReporter provides a basic HTML page with the error report included as a table.
 */
public class TableHtmlReporter extends Reporter {

    public TableHtmlReporter(PrintStream out, ProgramSettings programSettings, List<LintError> lintErrorList) {
        super(out, programSettings, lintErrorList);
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
                Joiner.on("\n").join(HtmlUtils.surroundAndHtmlEscapeAll(headers, "<th> ", " </th>")),
                "</tr>"
        );

        out.println(Joiner.on("\n").join(headerStringList));
    }

    @Override
    protected void printCategoryHeader(Enum<?> category) {}

    @Override
    public void printError(LintError error) {
        List<String> parameterValues = getParameterValues(error);

        out.println("<tr>");
        out.println(Joiner.on("\n").join(HtmlUtils.surroundAndHtmlEscapeAll(parameterValues, "<td> ", " </td>")));
        out.println("</tr>");
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
                rule.getCategory().toString(),
                EnumUtils.toHappyString(rule.getSeverity()),
                error.getMessage(),
                rule.getSummary(),
                rule.getDetailedDescription(),
                error.getFile().getAbsolutePath()
        );
    }

}
