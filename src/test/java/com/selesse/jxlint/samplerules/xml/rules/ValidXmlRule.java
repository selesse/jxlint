package com.selesse.jxlint.samplerules.xml.rules;

import com.selesse.jxlint.model.FileUtils;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;

import java.io.File;
import java.util.List;

public class ValidXmlRule extends LintRule {
    public ValidXmlRule() {
        super("Valid XML", "XML must be well-formed, valid.", "The XML needs to be \"valid\" " +
                "XML. This test definition means that the XML can be parsed by any parser. Any tag must be closed.",
                Severity.FATAL, Category.DEFAULT);
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allXmlFilesIn(getSourceDirectory());
    }

    @Override
    public boolean applyRule(File file) {
        return true;
    }
}
