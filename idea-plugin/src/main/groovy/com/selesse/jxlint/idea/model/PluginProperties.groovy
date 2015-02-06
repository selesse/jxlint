package com.selesse.jxlint.idea.model

import com.google.common.base.MoreObjects

class PluginProperties {
    String name
    String version
    String description
    String vendor
    String namespace
    List<String> ruleClasses


    @Override
    public String toString() {
        MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("version", version)
            .add("description", description)
            .add("vendor", vendor)
            .add("namespace", namespace)
            .add("ruleClasses", ruleClasses)
            .toString()
    }
}
