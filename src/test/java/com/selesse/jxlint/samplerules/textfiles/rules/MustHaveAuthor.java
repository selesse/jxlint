package com.selesse.jxlint.samplerules.textfiles.rules;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class MustHaveAuthor extends LintRule {
    public MustHaveAuthor() {
        super("Author tag required", "Every file must have an @author tag.",
                "Every file in this project requires an \"@author\" tag.",
                Severity.WARNING, Category.STYLE);
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allFiles(getSourceDirectory());
    }

    @Override
    public List<LintError> getLintErrors(File file) {
        List<LintError> lintErrorList = Lists.newArrayList();
        try {
            List<String> fileContents = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            for (String line : fileContents) {
                if (line.contains("@author")) {
                    return lintErrorList;
                }
            }
            lintErrorList.add(LintError.with(this, file).create());
        } catch (IOException e) {
            lintErrorList.add(LintError.with(this, file).andException(e).create());
        }

        return lintErrorList;
    }
}
