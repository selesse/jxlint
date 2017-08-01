package com.selesse.jxlint.model.rules;

import com.selesse.jxlint.utils.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class LintRuleTestImpl extends LintRule {
    public LintRuleTestImpl() {
        super("lint", "lint rule.", "A rule that does nothing", Severity.WARNING, Category.LINT);
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allFilesWithExtension(getSourceDirectory(), "xxx");
    }

    @Override
    public List<LintError> getLintErrors(File file) {
        return Collections.emptyList();
    }
}
