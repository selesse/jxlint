package com.selesse.jxlint.report;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.settings.ProgramSettings;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract reporter that displays the results of an execution of jxlint. Custom reporters can easily be created by
 * extending this class and overriding the appropriate methods.
 */
public abstract class Reporter {
    /**
     * The stream to write to when reporting.
     */
    protected PrintStream out;
    protected ProgramSettings settings;
    protected List<LintError> lintErrorList;

    public Reporter(PrintStream out, ProgramSettings settings, List<LintError> lintErrorList) {
        this.out = out;
        this.settings = settings;
        this.lintErrorList = lintErrorList;
    }

    /**
     * Prints the header, every category-error group, and the footer.
     * The category-error group is defined by a category header and one or more errors,
     */
    public void writeReport() {
        Enum<?> lastCategory = null;
        printHeader();
        Collections.sort(lintErrorList, new CategoryThenNameComparator());
        for (LintError error : lintErrorList) {
            Enum<?> currentCategory = error.getViolatedRule().getCategory();
            if (lastCategory == null || currentCategory != lastCategory) {
                printCategoryHeader(error.getViolatedRule().getCategory());
                lastCategory = currentCategory;
            }
            printError(error);
        }
        printFooter();
    }

    /**
     * Print the header. This is printed once at the beginning of the document. Refer to {@link #writeReport()} for
     * the print order.
     */
    protected abstract void printHeader();

    /**
     * Print a header for a particular category. Refer to {@link #writeReport()} for the print order.
     */
    protected abstract void printCategoryHeader(Enum<?> category);

    /**
     * Print a particular {@link LintError}. Refer to {@link #writeReport()} for the print order.
     */
    protected abstract void printError(LintError error);

    /**
     * Print the header. This is printed once at the end of the document. Refer to {@link #writeReport()} for the
     * print order.
     */
    protected abstract void printFooter();

    private static class CategoryThenNameComparator implements Comparator<LintError>, Serializable {
        @Override
        public int compare(LintError o1, LintError o2) {
            LintRule o1ViolatedRule = o1.getViolatedRule();
            LintRule o2ViolatedRule = o2.getViolatedRule();

            Enum<?> firstCategory = o1ViolatedRule.getCategory();
            Enum<?> secondCategory = o2ViolatedRule.getCategory();

            if (firstCategory == secondCategory) {
                return o1ViolatedRule.getName().compareTo(o2ViolatedRule.getName());
            }

            return firstCategory.toString().compareToIgnoreCase(secondCategory.toString());
        }
    }

    /**
     * Report the number of errors for every category. For example,
     * "There are 4 errors, 0 warnings, and 1 fatal error (5 total)."
     */
    public String getErrorReportString() {
        int numberOfErrors = getNumberOfSeverityErrors(Severity.ERROR);
        int numberOfWarnings = getNumberOfSeverityErrors(Severity.WARNING);
        int numberOfFatal = getNumberOfSeverityErrors(Severity.FATAL);

        return String.format("There are %s, %s, and %s (%d total).",
                    pluralize(numberOfWarnings, "warning"),
                    pluralize(numberOfErrors, "error"),
                    pluralize(numberOfFatal, "fatal error"),
                    lintErrorList.size()
            );
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
