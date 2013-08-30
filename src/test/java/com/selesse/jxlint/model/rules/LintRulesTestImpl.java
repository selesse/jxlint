package com.selesse.jxlint.model.rules;

import com.selesse.jxlint.model.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * Implementation of a {@link LintRules} used for testing.
 */
public class LintRulesTestImpl extends AbstractLintRules {
    @Override
    public void initializeLintTasks() {
        // Example rule saying that XML must be valid
        lintRules.add(new LintRule("Valid XML", "XML must be well-formed, valid.", "The XML needs to be \"valid\" " +
                "XML. This test definition means that the XML can be parsed by any parser. Any tag must be closed.",
                Severity.FATAL, Category.DEFAULT) {

            @Override
            public boolean validate(String sourceDirectory) {
                return true;
            }
        });

        // Example rule saying that duplicate attribute tags within XML are bad
        lintRules.add(new LintRule("Unique attribute", "Attributes within a tag must be unique.",
                "Attributes within an XML tag must be unique. That is, <tag a=\"x\" a=\"y\"> is invalid.",
                Severity.WARNING, Category.DEFAULT) {
            @Override
            public boolean validate(String sourceDirectory) {
                return true;
            }
        });

        // Example (disabled-by-default) rule
        lintRules.add(new LintRule("XML version specified", "Version of XML must be specified.",
                "The xml version should be specified. For example, <?xml version=\"1.0\">.",
                Severity.WARNING, Category.DEFAULT, false) {
            @Override
            public boolean validate(String sourceDirectory) {
                List<File> xmlFiles = FileUtils.allXmlFilesIn(new File(sourceDirectory));

                for (File xmlFile : xmlFiles) {
                    try {
                        List<String> xmlFileLines = Files.readAllLines(xmlFile.toPath(), Charset.defaultCharset());
                        for (String line : xmlFileLines) {
                            if (line.contains("<?xml")) {
                                return line.contains("version=\"");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });
    }
}
