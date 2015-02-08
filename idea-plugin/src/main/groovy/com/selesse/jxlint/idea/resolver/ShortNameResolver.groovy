package com.selesse.jxlint.idea.resolver

import com.selesse.jxlint.model.rules.LintRule

class ShortNameResolver {
    static getShortName(LintRule lintRule) {
        lintRule.name.replaceAll(/[^0-9a-zA-Z]/, '')
    }
}
