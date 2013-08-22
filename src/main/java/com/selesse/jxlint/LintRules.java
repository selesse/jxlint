package com.selesse.jxlint;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.Category;
import com.selesse.jxlint.model.LintRule;
import com.selesse.jxlint.model.NonExistentLintRuleException;
import com.selesse.jxlint.model.Severity;

import java.util.Collections;
import java.util.List;

public class LintRules {
    private List<LintRule> lintRules;
    private static LintRules instance;

    public static LintRules getInstance() {
        if (instance == null) {
            instance = new LintRules();
        }
        return instance;
    }

    public LintRules() {
        this.lintRules = Lists.newArrayList();
        initializeLintTasks();
    }

    private void initializeLintTasks() {
        lintRules.addAll(
                Lists.newArrayList(
                    new LintRule("bob", "joe", "steve", Severity.WARNING, Category.DEFAULT)
                    , new LintRule("blah", "blah", "blah", Severity.ERROR, Category.DEFAULT)
                )
        );
    }

    public LintRule getLintRule(String ruleName) throws NonExistentLintRuleException {
        LintRule lintRule = null;

        for (LintRule rule : lintRules) {
            if (rule.getName().equalsIgnoreCase(ruleName)) {
                lintRule = rule;
                break;
            }
        }

        if (lintRule == null) {
            throw new NonExistentLintRuleException(ruleName);
        }

        return lintRule;
    }

    public List<LintRule> getAllRules() {
        return Collections.unmodifiableList(lintRules);
    }
}
