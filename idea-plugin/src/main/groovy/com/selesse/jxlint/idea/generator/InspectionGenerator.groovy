package com.selesse.jxlint.idea.generator

import com.selesse.jxlint.idea.model.PluginProperties
import com.selesse.jxlint.idea.resolver.ShortNameResolver
import com.selesse.jxlint.model.rules.LintError
import com.selesse.jxlint.model.rules.LintRule
import com.squareup.javapoet.*
import groovy.util.logging.Slf4j

import javax.lang.model.element.Modifier
import javax.swing.SwingUtilities

@Slf4j
class InspectionGenerator implements FileGenerator {
    PluginProperties properties
    Class<? extends LintRule> lintRule
    PrintStream out

    def InspectionGenerator(PluginProperties properties, Class<? extends LintRule> lintRule, PrintStream out) {
        this.properties = properties
        this.lintRule = lintRule
        this.out = out
    }

    @Override
    void generate() {
        log.info "Generating inspection for: ${lintRule.getName()}"

        def lintRuleImpl = lintRule.newInstance()

        def className = lintRule.getName().substring(lintRule.getName().lastIndexOf(".") + 1)

        def isEnabledByDefault = MethodSpec.methodBuilder("isEnabledByDefault")
                .returns(boolean.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement('return $N.isEnabled()', "lintRule")
                .build()

        def getDisplayName = MethodSpec.methodBuilder("getDisplayName")
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement('return $N.getSummary()', "lintRule")
                .build()

        def getShortName = MethodSpec.methodBuilder("getShortName")
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement('return $S', ShortNameResolver.getShortName(lintRuleImpl))
                .build()

        def getGroupDisplayName = MethodSpec.methodBuilder("getGroupDisplayName")
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement('return $S', properties.name)
                .build()

        def problemsHolderClass = ClassName.get('com.intellij.codeInspection', 'ProblemsHolder')
        def psiElementVisitorClass = ClassName.get('com.intellij.psi', 'PsiElementVisitor')

        def buildVisitor = MethodSpec.methodBuilder('buildVisitor')
                .returns(psiElementVisitorClass)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(problemsHolderClass, 'myHolder')
                .addParameter(boolean.class, 'isOnTheFly')
                .addStatement('return new $N($N, $N)', 'BaseFileVisitor', 'myHolder', 'lintRule')
                .build()

        def lintRuleField = FieldSpec.builder(LintRule.class, "lintRule")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new ${lintRule.getName()}()")
                .build()

        def inspectionClass = TypeSpec.classBuilder("${className}Inspection")
                .superclass(ClassName.get('com.intellij.codeInspection.ex', 'BaseLocalInspectionTool'))
                .addModifiers(Modifier.PUBLIC)
                .addField(lintRuleField)
                .addMethod(isEnabledByDefault)
                .addMethod(getDisplayName)
                .addMethod(getShortName)
                .addMethod(getGroupDisplayName)
                .addMethod(buildVisitor)
                .build()

        def javaFile = JavaFile.builder("${properties.namespace}.inspection", inspectionClass)
                .build()

        javaFile.writeTo(out)
    }
}
