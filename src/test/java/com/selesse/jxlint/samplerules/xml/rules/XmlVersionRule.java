package com.selesse.jxlint.samplerules.xml.rules;

import com.selesse.jxlint.model.FileUtils;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class XmlVersionRule extends LintRule {
    public XmlVersionRule() {
        super("XML version specified", "Version of XML must be specified.",
                "The xml version should be specified. For example, <?xml version=\"1.0\" encoding=\"UTF-8\"?>.",
                Severity.WARNING, Category.DEFAULT, false);
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
        } catch (SAXException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.matches("The version is required in the XML declaration.")) {
                failedRules.add(new LintError(this, e));
                return false;
            }
        } catch (Exception e) {
            failedRules.add(new LintError(this, e));
            return false;
        }

        return true;
    }
}
