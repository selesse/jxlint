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

        def constructor = MethodSpec.constructorBuilder()
                .addStatement("this.lintRule = new ${lintRule.getName()}()")
                .addModifiers(Modifier.PUBLIC)
                .build()

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

        def problemsHolderClass = ClassName.get('com.intellij.codeInspection', 'ProblemsHolder')
        def psiElementVisitorClass = ClassName.get('com.intellij.psi', 'PsiElementVisitor')
        def editorClass = ClassName.get('com.intellij.openapi.editor', 'Editor')
        def psiUtilBaseClass = ClassName.get('com.intellij.psi.util', 'PsiUtilBase')
        def psiElementClass = ClassName.get('com.intellij.psi', 'PsiElement')
        def problemHighlightTypeClass = ClassName.get('com.intellij.codeInspection', 'ProblemHighlightType', 'ERROR')
        def localQuickFixClass = ClassName.get('com.intellij.codeInspection', 'LocalQuickFix', 'EMPTY_ARRAY')

        def buildVisitor = MethodSpec.methodBuilder('buildVisitor')
                .returns(psiElementVisitorClass)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(problemsHolderClass, 'holder')
                .addParameter(boolean.class, 'isOnTheFly')
                .addStatement('return new $N($N)', 'MyEventVisitor', 'holder')
                .build()

        def myElementVisitorConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(problemsHolderClass, 'myHolder')
                .addStatement('this.$N = $N', 'myHolder', 'myHolder')
                .build()

        def runThread = TypeSpec.anonymousClassBuilder('')
                .addSuperinterface(Runnable.class)
                .addMethod(
                MethodSpec.methodBuilder('run')
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                        .addStatement('$T $N = $N.getLintErrors($N)',
                            ParameterizedTypeName.get(List.class, LintError.class),
                            'lintErrors', 'lintRule', 'filesToValidate')
                        .addCode(CodeBlock.builder()
                            .beginControlFlow('for ($T $N : $N)', LintError.class, 'lintError', 'lintErrors')
                            .addStatement('$T $N = $T.findEditor($N)', editorClass, 'editor', psiUtilBaseClass, 'file')
                            .add(CodeBlock.builder()
                                .beginControlFlow('if ($N != null)', 'editor')
                                .addStatement('int $N = $N.getDocument().getLineStartOffset($N.getLineNumber())',
                                    'offset', 'editor', 'lintError')
                                .addStatement('$T $N = $T.getElementAtOffset($N, $N)', psiElementClass,
                                    'problemElement', psiUtilBaseClass, 'file', 'offset')
                                .addStatement('$N.registerProblem($N, $N.getMessage(), $L, $L)', 'myHolder',
                                    'problemElement', 'lintError', problemHighlightTypeClass, localQuickFixClass)
                                .endControlFlow()
                                .build())
                            .endControlFlow()
                            .build())
                    .build())
                .build()

        def visitFile = MethodSpec.constructorBuilder()
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get('com.intellij.psi', 'PsiFile'), 'file', Modifier.FINAL)
                .addStatement('String path = file.getVirtualFile().getPath()')
                .addStatement('$T $N = $N.getFilesToValidate()',
                        ParameterizedTypeName.get(List.class, File.class), 'filesToValidate', 'lintRule')
                .addCode(CodeBlock.builder()
                .beginControlFlow('for (final $T $N : $N)', File.class, 'fileToValidate', 'filesToValidate')
                            .add(CodeBlock.builder()
                                    .beginControlFlow('if ($N.equals($N.getAbsolutePath()', 'path', 'filesToValidate')
                                    .addStatement('$T.invokeLater($L)', SwingUtilities.class, runThread)
                                    .endControlFlow()
                                    .build())
                            .build())
                .endControlFlow()
                .build()

        def myElementVisitor = TypeSpec.classBuilder("MyElementVisitor")
                .superclass(psiElementVisitorClass)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .addField(problemsHolderClass, "myHolder", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(myElementVisitorConstructor)
                .addMethod(visitFile)
                .build()

        def inspectionClass = TypeSpec.classBuilder("${className}Inspection")
                .addSuperinterface(ClassName.get('com.intellij.codeInspection.ex', 'BaseLocalInspectionTool'))
                .addModifiers(Modifier.PUBLIC)
                .addField(LintRule.class, "lintRule", Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(constructor)
                .addMethod(isEnabledByDefault)
                .addMethod(getDisplayName)
                .addMethod(getShortName)
                .addMethod(buildVisitor)
                .addType(myElementVisitor)
                .build()

        def javaFile = JavaFile.builder("${properties.namespace}.inspection", inspectionClass)
                .build()

        javaFile.writeTo(out)
    }
}
