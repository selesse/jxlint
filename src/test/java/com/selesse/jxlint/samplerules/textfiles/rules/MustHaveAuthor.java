package com.selesse.jxlint.samplerules.textfiles.rules;

import com.google.common.base.Optional;
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
    public Optional<LintError> getLintError(File file) {
        try {
            List<String> fileContents = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            for (String line : fileContents) {
                if (line.contains("@author")) {
                    return Optional.absent();
                }
            }
            return Optional.of(LintError.with(this, file).create());
        } catch (IOException e) {
            return Optional.of(LintError.with(this, file).andException(e).create());
        }
    }
}
