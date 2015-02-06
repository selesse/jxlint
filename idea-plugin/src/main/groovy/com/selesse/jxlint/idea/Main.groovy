package com.selesse.jxlint.idea

import com.selesse.jxlint.idea.detection.LintRuleFinder
import com.selesse.jxlint.idea.generator.PluginGenerator
import com.selesse.jxlint.idea.model.PluginProperties

class Main {
    public static void main(String[] args) {
        def rules = LintRuleFinder.getLintRules()
        def pluginProperties = new PluginProperties()
        pluginProperties.with {
            name: 'test'
            name = 'name'
            version = 'version'
            description = 'description'
            vendor = 'vendor'
            namespace = 'com.selesse'
            ruleClasses = rules.collect( { it.getName() } )
        }

        def outputDirectory = new File('temp')

        def pluginGenerator = new PluginGenerator(pluginProperties, rules, outputDirectory)
        pluginGenerator.generatePlugin()
    }
}
