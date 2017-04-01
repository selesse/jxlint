package com.selesse.jxlintimpl.rules.impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.utils.FileUtils;
import com.selesse.jxlintimpl.CustomCategories;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Slf4jLoggerStringFormat extends LintRule {
    private static final String name = "SLF4J loggers should not use String.format";
    private static final String summary = "SLF4J loggers should use parametrized logging, not String.format";

    public Slf4jLoggerStringFormat() {
        super(name, summary, "", Severity.WARNING, CustomCategories.PROBABLY_ACCIDENT);
        setDetailedDescription(getMarkdownDescription());
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allFilesWithExtension(getSourceDirectory(), "java");
    }

    @Override
    public List<LintError> getLintErrors(File file) {
        List<LintError> lintErrors = Lists.newArrayList();
        try {
            CompilationUnit compilationUnit = JavaParser.parse(file);
            boolean importsSlf4j = compilationUnit.getImports()
                    .stream()
                    .anyMatch(importDeclaration -> importDeclaration.getNameAsString().startsWith("org.slf4j"));

            if (importsSlf4j) {
                Optional<String> variableNameMaybe = determineLoggerVariableName(compilationUnit);
                variableNameMaybe.ifPresent(variable -> {
                    for (MethodCallExpr expression : compilationUnit.getChildNodesByType(MethodCallExpr.class)) {
                        boolean isExpressionThatUsesLoggingVariable = expression.getChildNodesByType(SimpleName.class)
                                .stream()
                                .anyMatch(name -> name.getIdentifier().equals(variable));

                        if (isExpressionThatUsesLoggingVariable) {
                            List<MethodCallExpr> methodCalls = expression.getChildNodesByType(MethodCallExpr.class);
                            List<MethodCallExpr> stringFormatViolations =
                                    methodCalls
                                            .stream()
                                            .filter(this::isStringFormatMethod)
                                            .collect(Collectors.toList());

                            if (stringFormatViolations.size() > 0) {
                                for (MethodCallExpr stringFormatViolation : stringFormatViolations) {
                                    Range range = stringFormatViolation.getRange().orElse(null);
                                    int lineNumber = -1;
                                    if (range != null) {
                                        lineNumber = range.begin.line;
                                    }
                                    lintErrors.add(
                                            LintError.with(this, file)
                                                    .andErrorMessage(expression.toString())
                                                    .andLineNumber(lineNumber)
                                                    .create()
                                    );
                                }
                            }
                        }
                    }
                });

            }
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return lintErrors;
    }

    private Optional<String> determineLoggerVariableName(CompilationUnit compilationUnit) {
        for (final FieldDeclaration field : compilationUnit.getChildNodesByType(FieldDeclaration.class)) {
            for (final VariableDeclarator variable : field.getChildNodesByType(VariableDeclarator.class)) {
                for (MethodCallExpr method : variable.getChildNodesByType(MethodCallExpr.class)) {
                    if (isSlf4jLoggerInitialization(method)) {
                        return Optional.of(variable.getNameAsString());
                    }
                }
            }
        }
        return Optional.empty();
    }

    private boolean isSlf4jLoggerInitialization(MethodCallExpr method) {
        Expression expression = method.getScope().orElse(null);
        if (expression != null && expression instanceof NameExpr) {
            String name = ((NameExpr) expression).getNameAsString();
            if (name.equals("LoggerFactory") && method.getNameAsString().equals("getLogger")) {
                return true;
            }
        }
        return false;
    }

    private boolean isStringFormatMethod(MethodCallExpr methodCallExpr) {
        Expression expression = methodCallExpr.getScope().orElse(null);
        if (expression != null && expression instanceof NameExpr) {
            String className = ((NameExpr) expression).getNameAsString();
            String functionName = methodCallExpr.getNameAsString();

            return className.equals("String") && functionName.equals("format");
        }
        return false;
    }
}
