package com.selesse.jxlint.idea.generator

import com.google.common.base.Joiner
import com.google.common.base.Splitter
import com.selesse.jxlint.idea.model.PluginProperties
import com.selesse.jxlint.idea.resolver.ShortNameResolver
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

        // Generate a plugin.xml file that describes the IntelliJ plugin
        def pluginFileGenerator = new PluginFileGenerator(pluginProperties, getPluginStream())

        // Generate the provider for all the rules
        def providerGenerator = new ProviderGenerator(pluginProperties, rules, getProviderStream())

        List<FileGenerator> generators = [pluginFileGenerator, providerGenerator]

        rules.each {
            // Generate an inspection description (appears in the help message of error message)
            def descriptionGenerator =
                    new InspectionDescriptionGenerator(pluginProperties, it, getInspectionDescriptionStream(it))
            // Generate the inspection (the logic for the actual validation)
            def inspectionGenerator =
                    new InspectionGenerator(pluginProperties, it, getInspectionStream(it))

            generators += [descriptionGenerator, inspectionGenerator]
        }

        generators.each {
            it.generate()
        }
    }

    PrintStream getInspectionStream(Class<? extends LintRule> rule) {
        LintRule lintRuleImpl = rule.newInstance()

        def baseSourcePath = getBaseSourcePath(pluginProperties.namespace) + '/inspection/'
        def inspectionDirectory = new File(outputDirectory, baseSourcePath)
        inspectionDirectory.mkdirs()
        def inspectionFile = new File(inspectionDirectory, lintRuleImpl.class.simpleName + "Inspection.java")

        new PrintStream(new FileOutputStream(inspectionFile))
    }

    PrintStream getInspectionDescriptionStream(Class<? extends LintRule> rule) {
        LintRule lintRuleImpl = rule.newInstance()
        def pluginFile = new File(outputDirectory, 'src/main/resources/inspectionDescriptions')
        pluginFile.mkdirs()
        def descriptionFile = new File(pluginFile, ShortNameResolver.getShortName(lintRuleImpl) + '.html')

        new PrintStream(new FileOutputStream(descriptionFile))
    }

    PrintStream getPluginStream() {
        def pluginFile = new File(outputDirectory, 'src/main/resources/META-INF')
        pluginFile.mkdirs()
        def pluginOutputStream = new FileOutputStream(new File(pluginFile, 'plugin.xml'))

        new PrintStream(pluginOutputStream)
    }

    PrintStream getProviderStream() {
        def baseSourcePath = getBaseSourcePath(pluginProperties.namespace) + '/provider/'
        def providerFile = new File(outputDirectory, baseSourcePath)
        providerFile.mkdirs()

        new PrintStream(new FileOutputStream(new File(providerFile, "Provider.java")))
    }

    static String getBaseSourcePath(String namespace) {
        // i.e. com.selesse.jxlint => ['com', 'selesse', 'jxlint']
        def packages = Splitter.on(".").split(namespace)
        // i.e. ['com', 'selesse', 'jxlint' => 'com/selesse/jxlint'
        def packageDirectories = Joiner.on("/").join(packages)

        '/src/main/java/' + packageDirectories
    }
}
