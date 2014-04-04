package com.selesse.jxlint.report;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.settings.ProgramSettings;
import com.selesse.jxlint.utils.EnumUtils;
import com.selesse.jxlint.utils.FileUtils;
import org.pegdown.PegDownProcessor;

import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequentialHtmlReporter extends Reporter {
    private final String nameAndVersion = settings.getProgramName() + " " + settings.getProgramVersion();
    private final Pattern alphanumeric = Pattern.compile("[a-zA-Z0-9_]");

    public SequentialHtmlReporter(PrintStream out, ProgramSettings programSettings, List<LintError> lintErrorList) {
        super(out, programSettings, lintErrorList);
    }

    @Override
    protected void printHeader() {
        out.println("<!doctype HTML>");
        out.println("<head>");
        out.println("<title> " + nameAndVersion + " - Report </title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1> <center> " + nameAndVersion + " - Lint Report </center> </h1>");
        out.println("<h2> <center> " + new Date() + " </center> </h2>");
        out.println("<div>" + getErrorReportString() + "</div>");
    }

    @Override
    protected void printCategoryHeader(Category category) {
        out.println("<h2> <u> " + EnumUtils.toHappyString(category) + " </u> </h2>");
    }

    @Override
    protected void printError(LintError error) {
        out.println("<b>[" + error.getViolatedRule().getSeverity() + "] <a href=\"#" + getHrefSafeName(error) + "\">" +
                error.getViolatedRule().getName() + "</a></b>");
        out.print("violated in <a href=\"" + error.getFile() + "\">" +
                FileUtils.getRelativePath(LintRulesImpl.getInstance().getSourceDirectory(), error.getFile()) + "</a>");
        if (error.getLineNumber() > 0) {
            out.print(" on line " + error.getLineNumber());
        }
        out.println("<br/>");
        out.println(error.getMessage() + "<br>");
        if (error.getException() != null) {
            String exceptionLines = Joiner.on("<br>").join(error.getException().getStackTrace());
            out.println("<pre>" + exceptionLines + "</pre>");
        }
        out.println("<hr>");
    }

    /**
     * Returns a href-safe name. Something that can be put in 'a href="HERE"'.
     */
    private String getHrefSafeName(LintError lintError) {
        String hrefSafeName = "" + lintError.getViolatedRule().hashCode();

        Matcher matcher = alphanumeric.matcher(lintError.getViolatedRule().getName());
        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find()) {
            stringBuilder.append(matcher.group());
        }

        if (stringBuilder.length() > 0) {
            hrefSafeName = stringBuilder.toString();
        }

        return hrefSafeName.toLowerCase();
    }

    @Override
    protected void printFooter() {
        out.println("<hr>");
        out.println("<div>");
        printUniqueErrors();
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private void printUniqueErrors() {
        List<String> uniqueErrorTypes = Lists.newArrayList();

        for (int i = 0; i < lintErrorList.size(); i++) {
            LintError lintError = lintErrorList.get(i);
            String className = lintError.getViolatedRule().getClass().getSimpleName();

            if (!uniqueErrorTypes.contains(className)) {
                uniqueErrorTypes.add(className);
                printDetailedHtmlError(lintError);
            }
        }

    }

    private void printDetailedHtmlError(LintError lintError) {
        out.println("<h3 id=\"" + getHrefSafeName(lintError) + "\"> " + lintError.getViolatedRule().getName() +
                "</h3>");
        out.println("<h4>" + lintError.getViolatedRule().getSummary() + "</h4>");
        out.println("<b>Category</b> : " + lintError.getViolatedRule().getCategory() + " <br>");
        out.println("<b>Severity</b> : " + lintError.getViolatedRule().getSeverity() + " <br>");
        if (!lintError.getViolatedRule().isEnabled()) {
            out.println("<b> This rule is disabled by default </b> <br>");
        }
        PegDownProcessor pegDownProcessor = new PegDownProcessor();
        out.println(pegDownProcessor.markdownToHtml(lintError.getViolatedRule().getDetailedDescription()));
    }
}
