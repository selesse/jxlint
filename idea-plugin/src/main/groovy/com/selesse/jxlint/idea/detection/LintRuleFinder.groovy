package com.selesse.jxlint.idea.detection

import com.selesse.jxlint.model.rules.LintRule
import groovy.util.logging.Slf4j
import org.reflections.Reflections

@Slf4j
class LintRuleFinder {
    static def Set<Class<? extends LintRule>> getLintRules(String basePackage) {
        def reflections = new Reflections(basePackage)
        def lintRuleSubclasses = reflections.getSubTypesOf(LintRule.class)

        log.info "Got ${lintRuleSubclasses.size()} lintrule subclasses"
        lintRuleSubclasses.each { log.info "Found class: $it" }

        lintRuleSubclasses
    }
}
