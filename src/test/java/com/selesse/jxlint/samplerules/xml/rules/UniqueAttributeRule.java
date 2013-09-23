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

public class UniqueAttributeRule extends LintRule {
    public UniqueAttributeRule() {
        super("Unique attribute", "Attributes within a tag must be unique.",
                "Attributes within an XML tag must be unique. That is, <tag a=\"x\" a=\"y\"> is invalid.",
                Severity.ERROR, Category.LINT);
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
            if (errorMessage.matches("Attribute \"([^\"]+)\" was already specified for element \"([^\"])+\"\\.")) {
                failedRules.add(new LintError(this, file, errorMessage.substring(0, errorMessage.length() - 1)));
            }
            else {
                failedRules.add(new LintError(this, file, "Error checking rule, could not parse xml"));
            }
            return false;
        } catch (Exception e) {
            // this will catch parser configuration errors as well as I/O exceptions
            failedRules.add(new LintError(this, file, "Error checking rule, could not parse xml"));
            return false;
        }

        return true;
    }
}
