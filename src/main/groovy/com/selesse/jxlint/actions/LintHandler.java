package com.selesse.jxlint.actions;

import com.selesse.jxlint.ProgramExitter;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.report.Reporter;
import com.selesse.jxlint.report.Reporters;
import com.selesse.jxlint.report.UnableToCreateReportException;
import com.selesse.jxlint.settings.ProgramSettings;

import java.util.List;

/**
 * Handler of action-based logic relating to linting. This particular LintHandler's core logic is in
 * {@link #lintAndReportAndExit(boolean)}.
 */
public class LintHandler {
    private final List<LintRule> lintRules;
    private final boolean warningsAreErrors;
    private final ProgramOptions options;
    private final ProgramSettings settings;

    public LintHandler(List<LintRule> lintRules, boolean warningsAreErrors, ProgramOptions options,
                       ProgramSettings settings) {
        this.lintRules = lintRules;
        this.warningsAreErrors = warningsAreErrors;
        this.options = options;
        this.settings = settings;
    }

    /**
     * Performs the linting via the {@link Linter}, reports the errors, and exits the program via
     * {@link com.selesse.jxlint.ProgramExitter}, if exitAfterReport is true.
     * The {@link com.selesse.jxlint.report.Reporter} created is based on the
     * {@link com.selesse.jxlint.model.ProgramOptions} passed in the constructor.
     */
    public void lintAndReportAndExit(boolean exitAfterReport) {
        Linter linter = LinterFactory.createNewLinter(lintRules);
        linter.performLintValidations();
        List<LintError> lintErrors = linter.getLintErrors();

        reportLintErrors(lintErrors, settings, options);
        if (exitAfterReport) {
            exitWithAppropriateStatus(lintErrors);
        }
    }

    private void reportLintErrors(List<LintError> lintErrors, ProgramSettings settings, ProgramOptions options) {
        OutputType outputType = options.getOutputType();
        String outputTypePath = options.getOption(JxlintOption.OUTPUT_TYPE_PATH);

        try {
            Reporter reporter = Reporters.createReporter(lintErrors, settings, outputType, outputTypePath);
            reporter.writeReport();
        }
        catch (UnableToCreateReportException e) {
            ProgramExitter.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
        }
    }

    private void exitWithAppropriateStatus(List<LintError> lintErrors) {
        if (warningsAreErrors && lintErrors.size() > 0) {
            ProgramExitter.exitProgram(ExitType.FAILED);
        }

        if (lintErrors.size() > 0) {
            for (LintError error : lintErrors) {
                if (error.getSeverity().ordinal() >= Severity.ERROR.ordinal()) {
                    ProgramExitter.exitProgram(ExitType.FAILED);
                }
            }
        }
        ProgramExitter.exitProgram(ExitType.SUCCESS);
    }
}
