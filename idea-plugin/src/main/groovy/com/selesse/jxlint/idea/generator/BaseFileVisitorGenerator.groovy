package com.selesse.jxlint.idea.generator

import com.selesse.jxlint.idea.model.PluginProperties
import com.selesse.jxlint.model.rules.LintError
import com.selesse.jxlint.model.rules.LintRule
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import groovy.util.logging.Slf4j

import javax.lang.model.element.Modifier

@Slf4j
public class BaseFileVisitorGenerator implements FileGenerator {
    PluginProperties properties
    PrintStream out
    private static final def DOCUMENT = 'document'
    private static final def FILE = 'file'
    private static final def PROBLEM_ELEMENT = 'problemElement'
    private static final def LINT_ERROR = 'lintError'
    private static final def ZERO_INDEX_LINE_NUMBER = 'zeroIndexLineNumber'
    private static final def START_OFFSET = 'startOffset'

    def BaseFileVisitorGenerator(PluginProperties properties, PrintStream out) {
        this.properties = properties
        this.out = out
    }

    @Override
    public void generate() {
        log.info 'Generating base class for inspectors'

        def psiFileClass = ClassName.get('com.intellij.psi', 'PsiFile')
        def documentClass = ClassName.get('com.intellij.openapi.editor', 'Document')
        def psiDocumentManagerClass = ClassName.get('com.intellij.psi', 'PsiDocumentManager')
        def psiUtilClass = ClassName.get('com.intellij.psi.util', 'PsiUtil')
        def psiElementClass = ClassName.get('com.intellij.psi', 'PsiElement')
        def problemHighlightTypeClass = ClassName.get('com.intellij.codeInspection', 'ProblemHighlightType', 'ERROR')
        def localQuickFixClass = ClassName.get('com.intellij.codeInspection', 'LocalQuickFix', 'EMPTY_ARRAY')
        def problemsHolderClass = ClassName.get('com.intellij.codeInspection', 'ProblemsHolder')

        def constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(problemsHolderClass, 'myHolder')
                .addParameter(LintRule.class, 'lintRule')
                .addStatement('this.$N = $N', 'myHolder', 'myHolder')
                .addStatement('this.$N = $N', 'lintRule', 'lintRule')
                .build()


        def visitFile = MethodSpec.methodBuilder('visitFile')
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(psiFileClass, FILE)
                .addStatement('validateRule($N)', FILE)
                .build()

        def validateRule = MethodSpec.methodBuilder('validateRule')
                .addModifiers(Modifier.PUBLIC)
                .addParameter(psiFileClass, 'file', Modifier.FINAL)
                .addStatement('String path = file.getVirtualFile().getPath()')
                .addStatement('$T $N = $N.getFilesToValidate()',
                ParameterizedTypeName.get(List.class, File.class), 'filesToValidate', 'lintRule')
                .addCode(CodeBlock.builder()
                .beginControlFlow('for (final $T $N : $N)', File.class, 'fileToValidate', 'filesToValidate')
                .add(CodeBlock.builder()
                .beginControlFlow('if ($N.equals($N.getAbsolutePath()))', 'path', 'fileToValidate')
                .addStatement('$T $N = $N.getLintErrors($N)', ParameterizedTypeName.get(List.class, LintError.class), 'lintErrors', 'lintRule', 'fileToValidate')
                .add(CodeBlock.builder()
                .beginControlFlow('for ($T $N : $N)', LintError.class, LINT_ERROR, 'lintErrors')
                .addStatement('$T $N = $T.getInstance($N.getProject()).getDocument($N)', documentClass, DOCUMENT, psiDocumentManagerClass, FILE, FILE)
                .add(CodeBlock.builder()
                .beginControlFlow('if ($N != null)', DOCUMENT)
                    .addStatement('$T $N', psiElementClass, PROBLEM_ELEMENT)
                    .add(CodeBlock.builder()
                        .beginControlFlow('if ($N.getLineNumber() > 0)', LINT_ERROR)
                            .addStatement('int $N = $N.getLineNumber() - 1', ZERO_INDEX_LINE_NUMBER, LINT_ERROR)
                            .addStatement('int $N = $N.getLineStartOffset($N)', START_OFFSET, DOCUMENT, ZERO_INDEX_LINE_NUMBER)
                            .addStatement('$N = $T.getElementAtOffset($N, $N)', PROBLEM_ELEMENT, psiUtilClass, FILE, START_OFFSET)
                        .nextControlFlow('else ')
                            .addStatement('$N = $N.getOriginalElement()', PROBLEM_ELEMENT, FILE)
                        .endControlFlow()
                    .build())
                .addStatement('$N.registerProblem($N, $N.getMessage(), $L, $L)', 'myHolder',
                'problemElement', LINT_ERROR, problemHighlightTypeClass, localQuickFixClass)
                .endControlFlow()
                .build())
                .endControlFlow()
                .build())
                .endControlFlow()
                .build())
                .build())
                .endControlFlow()
                .build()

        def providerClass = TypeSpec.classBuilder('BaseFileVisitor')
                .superclass(ClassName.get('com.intellij.psi', 'PsiElementVisitor'))
                .addModifiers(Modifier.PUBLIC)
                .addField(problemsHolderClass, 'myHolder', Modifier.PROTECTED, Modifier.FINAL)
                .addField(LintRule.class, 'lintRule', Modifier.PROTECTED, Modifier.FINAL)
                .addMethod(constructor)
                .addMethod(visitFile)
                .addMethod(validateRule)
                .build()

        def javaFile = JavaFile.builder("${properties.namespace}.inspection", providerClass)
                .build()

        javaFile.writeTo(out)
    }
}
