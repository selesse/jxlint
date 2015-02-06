package com.selesse.jxlint.idea.generator

import com.google.common.base.Charsets
import com.selesse.jxlint.idea.model.PluginProperties
import spock.lang.Specification

class PluginFileGeneratorTest extends Specification {
    def "WritePlugin"() {
        setup:
        def pluginProperties = new PluginProperties()
        pluginProperties.with {
            name = 'name'
            version = 'version'
            description = 'description'
            vendor = 'vendor'
            namespace = 'selesse.com'
            ruleClasses = [ 'com.selesse.One', 'com.selesse.Two', 'com.selesse.Three' ]
        }


        def stream = new ByteArrayOutputStream()
        def stdOutStream = new PrintStream(stream)

        def pluginGenerator = new PluginFileGenerator(pluginProperties, stdOutStream)
        when:
            pluginGenerator.generate()
        then:
            stream.toString(Charsets.UTF_8.displayName()) == """<idea-plugin version="2">
    <name> ${pluginProperties.name} </name>
    <description> ${pluginProperties.description} </description>
    <version> ${pluginProperties.version} </version>
    <vendor> ${pluginProperties.vendor} </vendor>

    <extensions defaultExtensionNs="${pluginProperties.namespace}">
        <inspectionToolProvider implementation="${pluginProperties.namespace}.provider.Provider" />
    </extensions>

</idea-plugin>
"""
    }
}
