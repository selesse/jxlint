package com.selesse.jxlint.linter;

import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.settings.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

public class ValidationThread implements Callable<List<LintError>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationThread.class);

    private LintRule lintRule;

    public ValidationThread(LintRule lintRule) {
        this.lintRule = lintRule;
    }

    @Override
    public List<LintError> call() throws Exception {
        long startTime = System.currentTimeMillis();
        lintRule.validate();

        List<LintError> lintErrorList = lintRule.getLintErrors();
        Collections.sort(lintErrorList, fileThenLineNumberComparator);

        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        LOGGER.info("[{}] took {} milliseconds to execute", lintRule.getName(), executionTime);
        Profiler.addExecutionTime(lintRule, executionTime);

        return lintRule.getLintErrors();
    }

    private static final Comparator<LintError> fileThenLineNumberComparator = new Comparator<LintError>() {
        @Override
        public int compare(LintError o1, LintError o2) {
            if (o1.getFile().compareTo(o2.getFile()) == 0) {
                return o1.getLineNumber() - o2.getLineNumber();
            }
            return o1.getFile().compareTo(o2.getFile());
        }
    };

}
