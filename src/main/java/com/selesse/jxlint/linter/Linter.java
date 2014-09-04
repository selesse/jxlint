package com.selesse.jxlint.linter;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * Simple implementation of a Linter. Goes through all the {@link LintRule}s and calls
 * {@link com.selesse.jxlint.model.rules.LintRule#validate()}. If there are any errors, this class
 * accumulates them. Call {@link #getLintErrors()} to retrieve them.
 */
public class Linter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Linter.class);
    private static final int THREADS = Runtime.getRuntime().availableProcessors();

    private List<LintRule> rules;
    private List<LintError> lintErrors;

    public Linter(List<LintRule> rules) {
        this.rules = rules;
        this.lintErrors = Lists.newArrayList();
    }

    /**
     * This validates (or invalidates) every lint rule. {@link com.selesse.jxlint.model.rules.LintError}s may arise
     * through failed validations. For every rule that fails a validation, there should be a corresponding
     * {@link com.selesse.jxlint.model.rules.LintError}.
     */
    public void performLintValidations() {
        List<ValidationThread> lintRuleThreads = getValidationThreads(rules);

        try {
            LOGGER.debug("Initializing pool of {} threads", THREADS);
            final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
            List<Future<List<LintError>>> futures = executorService.invokeAll(lintRuleThreads);
            executorService.shutdown();
            executorService.awaitTermination(24, TimeUnit.HOURS);

            for (Future<List<LintError>> ruleLintErrors : futures) {
                lintErrors.addAll(ruleLintErrors.get());
            }
        }
        catch (InterruptedException e) {
            LOGGER.error("Thread interrupted while validating", e);
        }
        catch (ExecutionException e) {
            LOGGER.error("Execution exception thrown while validating", e);
        }
    }

    private List<ValidationThread> getValidationThreads(List<LintRule> rules) {
        List<ValidationThread> validationThreads = Lists.newArrayList();

        for (LintRule lintRule : rules) {
            validationThreads.add(new ValidationThread(lintRule));
        }

        return validationThreads;
    }

    /**
     * Returns all the {@link com.selesse.jxlint.model.rules.LintError}s that have been found through validations
     * performed in {@link #performLintValidations()}.
     */
    public List<LintError> getLintErrors() {
        return lintErrors;
    }
}
