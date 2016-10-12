package com.selesse.jxlint.report;

import com.selesse.jxlint.model.LintErrorOrderings;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.settings.ProgramSettings;

import java.io.PrintStream;
import java.util.Collections;
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
    protected ProgramOptions options;
    protected List<LintError> lintErrorList;

    public Reporter(PrintStream out, ProgramSettings settings, ProgramOptions options, List<LintError> lintErrorList) {
        this.out = out;
        this.settings = settings;
        this.options = options;
        this.lintErrorList = lintErrorList;
    }

    /**
     * Prints the header, every category-error group, and the footer.
     * The category-error group is defined by a category header and one or more errors,
     */
    public void writeReport() {
        Enum<?> lastCategory = null;
        printHeader();
        Collections.sort(lintErrorList, LintErrorOrderings.getCategoryThenNameOrdering());
        for (LintError error : lintErrorList) {
            Enum<?> currentCategory = error.getViolatedRule().getCategory();
            if (lastCategory == null || currentCategory != lastCategory) {
                printCategoryHeader(error.getViolatedRule().getCategory());
                lastCategory = currentCategory;
            }
            printError(error);
        }
        printFooter();
        if (out != System.out) {
            out.close();
        }
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

    /**
     * Report the number of errors for every category. For example,
     * "There are 4 errors, 0 warnings, and 1 fatal error (5 total)."
     */
    String getErrorReportString() {
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
        return (int) lintErrorList.stream()
                .filter(input -> input.getViolatedRule().getSeverity() == severity)
                .count();
    }
}
