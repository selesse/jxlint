package com.selesse.jxlint.idea.generator

import com.google.common.base.Joiner
import com.selesse.jxlint.idea.model.PluginProperties
import com.selesse.jxlint.model.rules.LintRule
import com.squareup.javapoet.*
import groovy.util.logging.Slf4j

import javax.lang.model.element.Modifier

@Slf4j
class ProviderGenerator implements FileGenerator {
    PluginProperties properties
    Set<Class<? extends LintRule>> classes
    PrintStream out

    def ProviderGenerator(PluginProperties properties, Set<Class<? extends LintRule>> classes, PrintStream out) {
        this.properties = properties
        this.classes = classes
        this.out = out
    }

    @Override
    void generate() {
        log.info "Generating"

        def classNames = classes.collect { "${it.getName()}Inspection.class" }
        def classes = Joiner.on(", ").join(classNames)

        def getInspectionClasses = MethodSpec.methodBuilder("getInspectionClasses")
            .returns(Class[].class)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .addStatement("return [ $classes ]")
            .build()

        def providerClass = TypeSpec.classBuilder("Provider")
            .addSuperinterface(ClassName.get('com.intellij.codeInspection', 'InspectionToolProvider'))
            .addModifiers(Modifier.PUBLIC)
            .addMethod(getInspectionClasses)
            .build()

        def javaFile = JavaFile.builder("${properties.namespace}.provider", providerClass)
            .build()

        javaFile.writeTo(out)
    }
}
