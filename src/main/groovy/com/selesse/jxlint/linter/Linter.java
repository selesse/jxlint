package com.selesse.jxlint.linter;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;

import java.util.List;
import java.util.concurrent.*;

/**
 * Simple implementation of a Linter. Goes through all the {@link LintRule}s and calls
 * {@link com.selesse.jxlint.model.rules.LintRule#validate()}. If there are any errors, this class
 * accumulates them. Call {@link #getLintErrors()} to retrieve them.
 */
public class Linter {
    private List<LintRule> rules;
    private List<LintError> lintErrors;
    private static final int THREADS = Runtime.getRuntime().availableProcessors();

    public Linter(List<LintRule> rules) {
        this.rules = rules;
        lintErrors = Lists.newArrayList();
    }

    /**
     * This validates (or invalidates) every lint rule. {@link com.selesse.jxlint.model.rules.LintError}s may arise
     * through failed validations. For every rule that fails a validation, there should be a corresponding
     * {@link com.selesse.jxlint.model.rules.LintError}.
     */
    public void performLintValidations() {
        List<ValidationThread> lintRuleThreads = getValidationThreads(rules);

        try {
            final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
            List<Future<List<LintError>>> futures = executorService.invokeAll(lintRuleThreads);
            executorService.shutdown();
            executorService.awaitTermination(24, TimeUnit.HOURS);

            for (Future<List<LintError>> ruleLintErrors : futures) {
                lintErrors.addAll(ruleLintErrors.get());
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
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
