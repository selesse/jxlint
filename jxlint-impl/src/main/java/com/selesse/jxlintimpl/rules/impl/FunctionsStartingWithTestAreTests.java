package com.selesse.jxlintimpl.rules.impl;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.utils.FileUtils;
import com.selesse.jxlintimpl.CustomCategories;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.type.VoidType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class FunctionsStartingWithTestAreTests extends LintRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionsStartingWithTestAreTests.class);

    private static final String name = "Functions starting with 'test' are tests";
    private static final String summary = "Functions in tests starting with 'test' are annotated with @Test";

    public FunctionsStartingWithTestAreTests() {
        super(name, summary, "", Severity.ERROR, CustomCategories.PROBABLY_ACCIDENT);
        setDetailedDescription(getMarkdownDescription());
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allFilesMatching(getSourceDirectory(), ".*Test\\.java");
    }

    @Override
    public List<LintError> getLintErrors(File file) {
        List<LintError> lintErrors = Lists.newArrayList();
        LOGGER.info("Validating {}", file.getAbsolutePath());

        try {
            CompilationUnit compilationUnit = JavaParser.parse(file);
            List<TypeDeclaration> compilationUnitTypes = compilationUnit.getTypes();
            for (TypeDeclaration typeDeclaration : compilationUnitTypes) {
                for (Node node : typeDeclaration.getChildrenNodes()) {
                    if (node instanceof MethodDeclaration) {
                        MethodDeclaration methodDeclaration = (MethodDeclaration) node;

                        if (isPublicVoidTestFunction(methodDeclaration)) {
                            String methodName = methodDeclaration.getName();

                            LOGGER.info("Found public void method {}", methodName);

                            List<AnnotationExpr> annotationExprs = methodDeclaration.getAnnotations();
                            boolean hasTestAnnotation = hasTestAnnotation(annotationExprs);

                            if (!hasTestAnnotation) {
                                String errorMessage = "public void " + methodName + " does not have @Test annotation";
                                lintErrors.add(
                                        LintError.with(this, file)
                                                .andErrorMessage(errorMessage)
                                                .andLineNumber(methodDeclaration.getBeginLine())
                                                .create()
                                );
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Error parsing Java file", e);
        }

        return lintErrors;
    }

    private boolean isPublicVoidTestFunction(MethodDeclaration methodDeclaration) {
        return ModifierSet.isPublic(methodDeclaration.getModifiers())
                && methodDeclaration.getType() instanceof VoidType
                && methodDeclaration.getName().startsWith("test");
    }

    private boolean hasTestAnnotation(List<AnnotationExpr> annotationExprs) {
        if (annotationExprs != null) {
            for (AnnotationExpr annotationExpr : annotationExprs) {
                String annotationName = annotationExpr.getName().getName();
                if (annotationName.equals("Test") || annotationName.equals("org.junit.Test")) {
                    return true;
                }
            }
        }
        return false;
    }
}
