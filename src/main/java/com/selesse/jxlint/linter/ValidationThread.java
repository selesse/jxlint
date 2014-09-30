package com.selesse.jxlint.linter;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.settings.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class ValidationThread implements Callable<List<LintError>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationThread.class);

    private static final Ordering<LintError> fileThenLineNumberOrdering = new Ordering<LintError>() {
        @Override
        public int compare(LintError left, LintError right) {
            return ComparisonChain.start()
                    .compare(left.getFile(), right.getFile())
                    .compare(left.getLineNumber(), right.getLineNumber())
                    .result();
        }
    };

    private LintRule lintRule;

    public ValidationThread(LintRule lintRule) {
        this.lintRule = lintRule;
    }

    @Override
    public List<LintError> call() throws Exception {
        long startTime = System.currentTimeMillis();
        lintRule.validate();

        List<LintError> lintErrorList = lintRule.getLintErrors();
        Collections.sort(lintErrorList, fileThenLineNumberOrdering);

        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        LOGGER.info("[{}] took {} milliseconds to execute", lintRule.getName(), executionTime);
        Profiler.addExecutionTime(lintRule, executionTime);

        return lintRule.getLintErrors();
    }
}
