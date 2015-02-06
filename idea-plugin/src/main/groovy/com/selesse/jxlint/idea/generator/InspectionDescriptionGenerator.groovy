package com.selesse.jxlint.idea.generator

import com.selesse.jxlint.idea.model.PluginProperties
import com.selesse.jxlint.model.rules.LintRule
import groovy.util.logging.Slf4j
import org.pegdown.PegDownProcessor

@Slf4j
class InspectionDescriptionGenerator implements FileGenerator {
    PluginProperties properties
    Class<? extends LintRule> lintRule
    PrintStream out

    def InspectionDescriptionGenerator(PluginProperties properties, Class<? extends LintRule> lintRule,
                                       PrintStream out) {
        this.properties = properties
        this.lintRule = lintRule
        this.out = out
    }

    @Override
    void generate() {
        log.info "Generating"

        def markdownDescription = lintRule.newInstance().getDetailedOutput()
        PegDownProcessor pegDownProcessor = new PegDownProcessor()
        def htmlDescription = pegDownProcessor.markdownToHtml(markdownDescription)

        String template = "<html>\n<body>\n" + htmlDescription + "</body></html>"

        out.println(template)
        out.flush()
    }
}
