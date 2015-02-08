package com.selesse.jxlint.idea.generator

import com.selesse.jxlint.idea.model.PluginProperties
import groovy.util.logging.Slf4j
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader

@Slf4j
class IdeaFileGenerator implements FileGenerator {
    private static final String template = 'iml.vm'
    PluginProperties properties
    PrintStream out

    def IdeaFileGenerator(PluginProperties properties, PrintStream out) {
        this.properties = properties
        this.out = out
    }

    @Override
    void generate() {
        log.info "Generating iml file"

        Properties velocityProperties = new Properties()
        velocityProperties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
        velocityProperties.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName())
        VelocityEngine velocityEngine = new VelocityEngine(velocityProperties)
        velocityEngine.init()

        Template template = velocityEngine.getTemplate(template)

        VelocityContext context = new VelocityContext()

        StringWriter stringWriter = new StringWriter()
        template.merge(context, stringWriter)

        out.println(stringWriter.toString())
        out.close()
    }
}
