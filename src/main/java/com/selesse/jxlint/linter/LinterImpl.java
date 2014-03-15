package com.selesse.jxlint.linter;

import com.google.common.collect.Lists;
import com.selesse.jxlint.ProgramExitter;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.report.Reporter;
import com.selesse.jxlint.report.Reporters;
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

        OutputType outputType = programOptions.getOutputType();
        String outputPath = programOptions.getOption("outputTypePath");
        reportAndExit(failedRules, outputType, outputPath);
    }

    private void reportAndExit(List<LintError> failedRules, OutputType outputType, String outputTypePath) {
        errors = failedRules;

        try {
            Reporter reporter = Reporters.createReporter(failedRules, outputType, outputTypePath);
            reporter.writeReport();
        } catch (UnableToCreateReportException e) {
            ProgramExitter.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
        }

        if (warningsAreErrors && failedRules.size() > 0) {
            ProgramExitter.exitProgram(ExitType.FAILED);
        }

        if (failedRules.size() > 0) {
            for (LintError error : failedRules) {
                if (error.getViolatedRule().getSeverity().ordinal() >= Severity.ERROR.ordinal()) {
                    ProgramExitter.exitProgram(ExitType.FAILED);
                }
            }
        }
        ProgramExitter.exitProgram(ExitType.SUCCESS);
    }

    @Override
    public List<LintError> getLintErrors() {
        return errors;
    }
}
