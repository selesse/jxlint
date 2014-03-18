package com.selesse.jxlint.actions;

import com.selesse.jxlint.ProgramExitter;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.report.Reporter;
import com.selesse.jxlint.report.Reporters;
import com.selesse.jxlint.report.UnableToCreateReportException;

import java.util.List;

public class LintHandler {
    private final List<LintRule> lintRules;
    private final boolean warningsAreErrors;
    private final ProgramOptions options;

    public LintHandler(List<LintRule> lintRules, boolean warningsAreErrors, ProgramOptions options) {
        this.lintRules = lintRules;
        this.warningsAreErrors = warningsAreErrors;
        this.options = options;
    }

    public void handleLint() {
        Linter linter = LinterFactory.createNewLinter(lintRules);
        linter.doLint(options);
        List<LintError> lintErrors = linter.getLintErrors();

        reportLintErrors(lintErrors);
        if (!LintRulesImpl.isTestMode()) {
            exitWithStatus(lintErrors);
        }
    }

    private void reportLintErrors(List<LintError> lintErrors) {
        OutputType outputType = options.getOutputType();
        String outputTypePath = options.getOption("outputTypePath");

        try {
            Reporter reporter = Reporters.createReporter(lintErrors, outputType, outputTypePath);
            reporter.writeReport();
        } catch (UnableToCreateReportException e) {
            ProgramExitter.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
        }
    }

    private void exitWithStatus(List<LintError> lintErrors) {
        if (warningsAreErrors && lintErrors.size() > 0) {
            ProgramExitter.exitProgram(ExitType.FAILED);
        }

        if (lintErrors.size() > 0) {
            for (LintError error : lintErrors) {
                if (error.getViolatedRule().getSeverity().ordinal() >= Severity.ERROR.ordinal()) {
                    ProgramExitter.exitProgram(ExitType.FAILED);
                }
            }
        }
        ProgramExitter.exitProgram(ExitType.SUCCESS);
    }
}
