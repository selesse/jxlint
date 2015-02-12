package com.selesse.jxlint.idea

import com.selesse.jxlint.idea.detection.LintRuleFinder
import com.selesse.jxlint.idea.generator.PluginGenerator
import com.selesse.jxlint.idea.model.PluginProperties
import groovy.util.logging.Slf4j

@Slf4j
class Main {
    public static void main(String[] args) {
        def pluginNamespace = 'com.selesse'
        def pluginName = 'PluginName'
        def outputDirectory = new File(System.getProperty("user.home"), pluginName)

        def rules = LintRuleFinder.getLintRules(pluginNamespace)
        def pluginProperties = new PluginProperties()
        pluginProperties.with {
            name = pluginName
            version = 'PluginVersion'
            description = 'PluginDescription'
            vendor = 'PluginVendor'
            namespace = pluginNamespace
            ruleClasses = rules.collect( { it.getName() } )
        }

        if (outputDirectory.isDirectory()) {
            log.warn "Deleting {$outputDirectory.absolutePath}"
            outputDirectory.deleteDir()
        }

        def pluginGenerator = new PluginGenerator(pluginProperties, rules, outputDirectory)
        pluginGenerator.generatePlugin()

        log.info "Done generating $pluginName at $outputDirectory"
    }
}
