package com.selesse.jxlint.idea.generator

import com.selesse.jxlint.idea.model.PluginProperties
import groovy.util.logging.Slf4j
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader

@Slf4j
class PluginFileGenerator implements FileGenerator {
    private static final String template = 'META-INF/plugin.vm'
    PluginProperties properties
    PrintStream out

    PluginFileGenerator(PluginProperties properties, PrintStream out) {
        this.properties = properties
        this.out = out
    }

    @Override
    def void generate() {
        log.info "Generating plugin.xml file"

        Properties velocityProperties = new Properties()
        velocityProperties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
        velocityProperties.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName())
        VelocityEngine velocityEngine = new VelocityEngine(velocityProperties)
        velocityEngine.init()

        Template template = velocityEngine.getTemplate(template)

        VelocityContext context = getVelocityContext(properties)

        StringWriter stringWriter = new StringWriter()
        template.merge(context, stringWriter)

        out.println(stringWriter.toString())
        out.close()
    }

    private VelocityContext getVelocityContext(PluginProperties properties) {
        VelocityContext velocityContext = new VelocityContext()

        velocityContext.put('pluginName', properties.name)
        velocityContext.put('pluginDescription', properties.description)
        velocityContext.put('pluginVersion', properties.version)
        velocityContext.put('pluginNamespace', properties.namespace)
        velocityContext.put('provider', "${properties.namespace}.provider.Provider")

        return velocityContext
    }
}
