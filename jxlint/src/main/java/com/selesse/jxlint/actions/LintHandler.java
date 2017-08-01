package com.selesse.jxlint.actions;

import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.ExitException;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.report.Reporter;
import com.selesse.jxlint.report.Reporters;
import com.selesse.jxlint.report.UnableToCreateReportException;
import com.selesse.jxlint.settings.ProgramSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handler of action-based logic relating to linting. This particular LintHandler's core logic is in
 * {@link #lintAndReportAndExit(boolean)}.
 */
public class LintHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LintHandler.class);

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
     * {@link com.selesse.jxlint.ProgramExitter}, if exitAfterReport is true. The
     * {@link com.selesse.jxlint.report.Reporter} created is based on the
     * {@link com.selesse.jxlint.model.ProgramOptions} passed in the constructor.
     *
     * @throws ExitException
     */
    public void lintAndReportAndExit(boolean exitAfterReport) throws ExitException {
        LOGGER.debug("Performing validations against these lint rules: {}", lintRules);
        Linter linter = LinterFactory.createNewLinter(lintRules);
        linter.performLintValidations();
        List<LintError> lintErrors = linter.getLintErrors();

        reportLintErrors(lintErrors, settings, options);
        LOGGER.debug("Exiting? {}", exitAfterReport);
        if (exitAfterReport) {
            exitWithAppropriateStatus(lintErrors);
        }
    }

    private void reportLintErrors(List<LintError> lintErrors, ProgramSettings settings, ProgramOptions options)
            throws ExitException {
        try {
            Reporter reporter = Reporters.createReporter(lintErrors, settings, options);
            reporter.writeReport();
        }
        catch (UnableToCreateReportException e) {
            throw new ExitException(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
        }
    }

    private void exitWithAppropriateStatus(List<LintError> lintErrors) throws ExitException {
        if (warningsAreErrors && lintErrors.size() > 0) {
            throw new ExitException("", ExitType.FAILED);
        }

        if (lintErrors.size() > 0) {
            for (LintError error : lintErrors) {
                if (error.getSeverity().ordinal() >= Severity.ERROR.ordinal()) {
                    throw new ExitException("", ExitType.FAILED);
                }
            }
        }
        throw new ExitException("", ExitType.SUCCESS);
    }
}
