package com.selesse.jxlint.report;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.report.color.Color;
import com.selesse.jxlint.utils.EnumUtils;

import java.io.PrintStream;
import java.util.List;

/**
 * The DefaultReporter is the default CLI {@link com.selesse.jxlint.report.Reporter}.
 */
public class DefaultReporter extends Reporter {

    public DefaultReporter(PrintStream out, List<LintError> lintErrorList) {
        super(out, lintErrorList);
    }

    @Override
    public void printHeader() {
    }

    @Override
    protected void printCategoryHeader(Category category) {
        out.println();
        out.println("    " + Color.GREEN.wrapAround("-- " + EnumUtils.toHappyString(category) + " --"));
        out.println();
    }

    @Override
    public void printError(LintError error) {
        LintRule violatedRule = error.getViolatedRule();
        out.println(String.format("[%s] \"%s\" in %s",  colorSeverity(violatedRule.getSeverity()),
                Color.WHITE.wrapAround(violatedRule.getName()), error.getFile().getAbsolutePath()));
        out.println("    " + error.getMessage());
        out.println();
    }

    private String colorSeverity(Severity severity) {
        return severity.getColor().wrapAround(EnumUtils.toHappyString(severity));
    }

    @Override
    public void printFooter() {
        int numberOfErrors = getNumberOfSeverityErrors(Severity.ERROR);
        int numberOfWarnings = getNumberOfSeverityErrors(Severity.WARNING);
        int numberOfFatal = getNumberOfSeverityErrors(Severity.FATAL);

        if (lintErrorList.size() > 0) {
            out.println(String.format("There are %s, %s, and %s (%d total).",
                    pluralize(numberOfWarnings, "warning"),
                    pluralize(numberOfErrors, "error"),
                    pluralize(numberOfFatal, "fatal error"),
                    lintErrorList.size()
            ));
        }
    }

    private String pluralize(int numberOfErrors, String error) {
        return numberOfErrors + " " + (numberOfErrors == 1 ? error : error + "s");
    }

    private int getNumberOfSeverityErrors(final Severity severity) {
        Iterable<LintError> severityList = Iterables.filter(lintErrorList, new Predicate<LintError>() {
            @Override
            public boolean apply(LintError input) {
                return input.getViolatedRule().getSeverity() == severity;
            }
        });

        int errors = 0;

        for (LintError ignored : severityList) {
            errors++;
        }

        return errors;
    }
}
