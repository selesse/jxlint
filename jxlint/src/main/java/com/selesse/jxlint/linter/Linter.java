package com.selesse.jxlint.linter;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Simple implementation of a Linter. Goes through all the {@link LintRule}s and calls
 * {@link com.selesse.jxlint.model.rules.LintRule#validate()}. If there are any errors, this class
 * accumulates them. Call {@link #getLintErrors()} to retrieve them.
 */
public class Linter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Linter.class);
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();

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
        lintErrors = Lists.newArrayList();

        try {
            LOGGER.debug("Initializing pool of {} threads", NUMBER_OF_THREADS);
            final ListeningExecutorService executorService =
                    MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(NUMBER_OF_THREADS));

            for (LintRule lintRule : rules) {
                ValidationThread validationThread = new ValidationThread(lintRule);

                ListenableFuture<List<LintError>> lintErrorFuture = executorService.submit(validationThread);
                Futures.addCallback(lintErrorFuture, getFutureCallback(lintRule));
            }

            executorService.shutdown();
            executorService.awaitTermination(24, TimeUnit.HOURS);
        }
        catch (InterruptedException e) {
            LOGGER.error("Thread interrupted while validating", e);
        }
    }

    private FutureCallback<List<LintError>> getFutureCallback(final LintRule lintRule) {
        return new FutureCallback<List<LintError>>() {
            @Override
            public void onSuccess(@Nullable List<LintError> resultErrors) {
                if (resultErrors != null) {
                    LOGGER.info("[{}] found {} errors", lintRule.getName(), resultErrors.size());
                    lintErrors.addAll(resultErrors);
                }
                else {
                    LOGGER.error("[{}] returned null error list", lintRule.getName());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                LOGGER.error("Error running validation thread: {}", t.getMessage(), Throwables.getRootCause(t));
            }
        };
    }

    /**
     * Returns all the {@link com.selesse.jxlint.model.rules.LintError}s that have been found through validations
     * performed in {@link #performLintValidations()}.
     */
    public List<LintError> getLintErrors() {
        return lintErrors;
    }
}
