package com.selesse.jxlint.linter;

import com.google.common.base.Stopwatch;
import com.selesse.jxlint.model.LintErrorOrderings;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.settings.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ValidationThread implements Callable<List<LintError>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationThread.class);

    private LintRule lintRule;

    public ValidationThread(LintRule lintRule) {
        this.lintRule = lintRule;
    }

    @Override
    public List<LintError> call() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        lintRule.validate();

        List<LintError> lintErrorList = lintRule.getLintErrors();
        Collections.sort(lintErrorList, LintErrorOrderings.getFileThenLineNumberOrdering());

        stopwatch.stop();
        LOGGER.info("[{}] took {} milliseconds to execute",
                lintRule.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        Profiler.addExecutionTime(lintRule, stopwatch.elapsed(TimeUnit.MILLISECONDS));

        return lintRule.getLintErrors();
    }
}
