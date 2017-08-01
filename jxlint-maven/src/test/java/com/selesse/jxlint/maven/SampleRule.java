package com.selesse.jxlint.maven;

import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.utils.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class SampleRule extends LintRule {
    public SampleRule() {
        super("Sample", "Sample Rule.", "A rule that does nothing", Severity.ERROR, Category.CORRECTNESS);
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allFilesWithExtension(getSourceDirectory(), "tst");
    }

    @Override
    public List<LintError> getLintErrors(File file) {
        return Collections.emptyList();
    }
}
