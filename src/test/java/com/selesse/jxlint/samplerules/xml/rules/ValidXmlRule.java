package com.selesse.jxlint.samplerules.xml.rules;

import com.selesse.jxlint.model.FileUtils;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class ValidXmlRule extends LintRule {
    public ValidXmlRule() {
        super("Valid XML", "XML must be well-formed, valid.", "The XML needs to be \"valid\" " +
                "XML. This test definition means that the XML can be parsed by any parser. Any tag must be closed.",
                Severity.FATAL, Category.LINT);
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allXmlFilesIn(getSourceDirectory());
    }

    @Override
    public boolean applyRule(File file) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(null); // shut up!
            Document document = documentBuilder.parse(file);

            document.getDocumentElement().normalize();
        } catch (Exception e) {
            failedRules.add(new LintError(this, file, "Malformed XML, could not parse"));
            return false;
        }

        return true;
    }
}
