package com.selesse.jxlint.samplerules.xml.rules;

import com.selesse.jxlint.model.FileUtils;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class XmlVersionRule extends LintRule {
    public XmlVersionRule() {
        super("XML version specified", "Version of XML must be specified.",
                "The xml version should be specified. For example, <?xml version=\"1.0\">.",
                Severity.WARNING, Category.DEFAULT, false);
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allXmlFilesIn(getSourceDirectory());
    }

    @Override
    public boolean applyRule(File file) {
        try {
            List<String> fileContents = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            for (String line : fileContents) {
                if (line.contains("<?xml")) {
                    if (line.contains("version=\"")) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            // do nothing, it has erred
        }

        failedRules.add(new LintError());
        return false;
    }
}
