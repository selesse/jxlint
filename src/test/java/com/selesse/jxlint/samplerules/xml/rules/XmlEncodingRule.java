package com.selesse.jxlint.samplerules.xml.rules;

import com.google.common.base.Strings;
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

public class XmlEncodingRule extends LintRule {
    public XmlEncodingRule() {
        super("XML encoding specified", "Encoding of the XML must be specified.",
                "The xml version should be specified. For example, <?xml version=\"1.0\" encoding=\"UTF-8\"?>.",
                Severity.WARNING, Category.LINT, false);
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

            if (Strings.isNullOrEmpty(document.getXmlEncoding())) {
                failedRules.add(new LintError(this, file, "Encoding wasn't specified"));
            }
            return !Strings.isNullOrEmpty(document.getXmlEncoding());
        }
        catch (Exception e) {
            failedRules.add(new LintError(this, file, "Error checking rule, could not parse xml"));
            return false;
        }
    }
}
