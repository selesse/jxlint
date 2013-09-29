package com.selesse.jxlint.linter;

import com.google.common.collect.Lists;
import com.selesse.jxlint.Main;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.report.Reporter;
import com.selesse.jxlint.report.UnableToCreateReportException;

import java.util.List;

public class LinterImpl implements Linter {
    private List<LintRule> rules;
    private List<LintError> errors;
    private boolean warningsAreErrors;

    public LinterImpl(List<LintRule> rules, boolean warningsAreErrors) {
        this.rules = rules;
        this.warningsAreErrors = warningsAreErrors;
        errors = Lists.newArrayList();
    }

    @Override
    public void doLint(ProgramOptions programOptions) {
        List<LintError> failedRules = Lists.newArrayList();

        for (LintRule lintRule : rules) {
            lintRule.validate();
            failedRules.addAll(lintRule.getFailedRules());
        }

        reportAndExit(programOptions, failedRules);
    }

    private void reportAndExit(ProgramOptions programOptions, List<LintError> failedRules) {
        errors = failedRules;

        try {
            Reporter reporter = programOptions.createReporterFor(failedRules);
            reporter.outputReport();
        } catch (UnableToCreateReportException e) {
            Main.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
        }

        if (warningsAreErrors && failedRules.size() > 0) {
            Main.exitProgram(ExitType.FAILED);
        }

        if (failedRules.size() > 0) {
            for (LintError error : failedRules) {
                if (error.getViolatedRule().getSeverity().ordinal() >= Severity.ERROR.ordinal()) {
                    Main.exitProgram(ExitType.FAILED);
                }
            }
        }
        Main.exitProgram(ExitType.SUCCESS);
    }

    @Override
    public List<LintError> getLintErrors() {
        return errors;
    }
}
