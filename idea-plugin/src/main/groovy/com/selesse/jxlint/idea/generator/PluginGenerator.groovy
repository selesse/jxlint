package com.selesse.jxlint.idea.generator

import com.selesse.jxlint.idea.model.PluginProperties
import com.selesse.jxlint.model.rules.LintRule

class PluginGenerator {
    PluginProperties pluginProperties
    Set<Class<? extends LintRule>> rules
    File outputDirectory

    PluginGenerator(def pluginProperties, def rules, File outputDirectory) {
        this.pluginProperties = pluginProperties
        this.rules = rules
        this.outputDirectory = outputDirectory
    }

    def generatePlugin() {
        println "Generating ${pluginProperties}"

        // Generate the plugin.xml file
        def file = new File(outputDirectory, "META-INF")
        file.mkdirs()
        def outputStream = new FileOutputStream(new File(file, "plugin.xml"))
        def pluginFileGenerator = new PluginFileGenerator(pluginProperties, new PrintStream(outputStream))

        // Generate the provider for all the rules
        def providerGenerator = new ProviderGenerator(pluginProperties, rules, System.out)

        List<FileGenerator> generators = [pluginFileGenerator, providerGenerator]

        rules.each {
            def descriptionGenerator = new InspectionDescriptionGenerator(pluginProperties, it, System.out)
            def inspectionGenerator = new InspectionGenerator(pluginProperties, it, System.out)

            generators += [descriptionGenerator, inspectionGenerator]
        }

        generators.each {
            it.generate()
        }
    }
}
