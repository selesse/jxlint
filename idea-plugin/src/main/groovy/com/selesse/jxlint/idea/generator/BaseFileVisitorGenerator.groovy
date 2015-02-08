package com.selesse.jxlint.idea.generator

import com.selesse.jxlint.idea.model.PluginProperties
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import groovy.util.logging.Slf4j

import javax.lang.model.element.Modifier

@Slf4j
public class BaseFileVisitorGenerator implements FileGenerator {
    PluginProperties properties
    PrintStream out

    def BaseFileVisitorGenerator(PluginProperties properties, PrintStream out) {
        this.properties = properties
        this.out = out
    }

    @Override
    public void generate() {
        log.info 'Generating base class for inspectors'

        def problemsHolderClass = ClassName.get('com.intellij.codeInspection', 'ProblemsHolder')

        def constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(problemsHolderClass, 'myHolder')
                .addStatement('this.$N = $N', 'myHolder', 'myHolder')
                .build()

        def visitFile = MethodSpec.methodBuilder('visitFile')
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get('com.intellij.psi', 'PsiFile'), 'file', Modifier.FINAL)
                .build()

        def providerClass = TypeSpec.classBuilder('BaseFileVisitor')
                .superclass(ClassName.get('com.intellij.psi', 'PsiElementVisitor'))
                .addModifiers(Modifier.PUBLIC)
                .addField(problemsHolderClass, 'myHolder', Modifier.PROTECTED, Modifier.FINAL)
                .addMethod(constructor)
                .addMethod(visitFile)
                .build()

        def javaFile = JavaFile.builder("${properties.namespace}.inspection", providerClass)
                .build()

        javaFile.writeTo(out)
    }
}
