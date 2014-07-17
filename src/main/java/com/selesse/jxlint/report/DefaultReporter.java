package com.selesse.jxlint.report;

import com.google.common.base.Strings;
import com.selesse.jxlint.model.rules.*;
import com.selesse.jxlint.report.color.Color;
import com.selesse.jxlint.settings.ProgramSettings;
import com.selesse.jxlint.utils.EnumUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

/**
 * The DefaultReporter is the default CLI {@link com.selesse.jxlint.report.Reporter}.
 */
public class DefaultReporter extends Reporter {

    public DefaultReporter(PrintStream out, ProgramSettings programSettings, List<LintError> lintErrorList) {
        super(out, programSettings, lintErrorList);
    }

    @Override
    public void printHeader() {
    }

    @Override
    protected void printCategoryHeader(Enum<?> category) {
        out.println();
        out.println("    " + Color.GREEN.wrapAround("-- " + category + " --"));
        out.println();
    }

    @Override
    public void printError(LintError error) {
        File sourceDirectory = LintRulesImpl.getInstance().getSourceDirectory();
        String relativePath = sourceDirectory.toURI().relativize(error.getFile().toURI()).getPath();

        int errorLineNumber = error.getLineNumber();
        String lineNumberString = "";

        if (errorLineNumber > 0) {
            lineNumberString = " on line " + errorLineNumber;
        }

        LintRule violatedRule = error.getViolatedRule();
        out.println(String.format("[%s] \"%s\" in %s%s",  colorSeverity(error.getSeverity()),
                Color.WHITE.wrapAround(violatedRule.getName()), relativePath, lineNumberString));
        if (!Strings.isNullOrEmpty(error.getMessage())) {
            out.println("    " + error.getMessage());
        }
        out.println();
    }

    private String colorSeverity(Severity severity) {
        return severity.getColor().wrapAround(EnumUtils.toHappyString(severity));
    }

    @Override
    public void printFooter() {
        if (lintErrorList.size() > 0) {
            out.println(getErrorReportString());
        }
    }
}
