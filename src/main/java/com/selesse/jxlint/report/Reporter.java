package com.selesse.jxlint.report;

import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;

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
    protected List<LintError> lintErrorList;

    public Reporter(PrintStream out, List<LintError> lintErrorList) {
        this.out = out;
        this.lintErrorList = lintErrorList;
    }

    /**
     * Prints the header, every category-error group, and the footer.
     * The category-error group is defined by a category header and one or more errors,
     */
    public void writeReport() {
        Category lastCategory = null;
        printHeader();
        Collections.sort(lintErrorList, new CategoryThenNameComparator());
        for (LintError error : lintErrorList) {
            Category currentCategory = error.getViolatedRule().getCategory();
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
    protected abstract void printCategoryHeader(Category category);

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

            Category firstCategory = o1ViolatedRule.getCategory();
            Category secondCategory = o2ViolatedRule.getCategory();

            if (firstCategory == secondCategory) {
                return o1ViolatedRule.getName().compareTo(o2ViolatedRule.getName());
            }

            return firstCategory.ordinal() - secondCategory.ordinal();
        }
    }
}
