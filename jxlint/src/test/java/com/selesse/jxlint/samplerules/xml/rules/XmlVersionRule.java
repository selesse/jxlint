package com.selesse.jxlint.samplerules.xml.rules;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.utils.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class XmlVersionRule extends LintRule {
    public XmlVersionRule() {
        super("XML version specified", "Version of XML must be specified.",
                "The XML version should be specified. For example, <?xml version=\"1.0\" encoding=\"UTF-8\"?>.",
                Severity.FATAL, Category.LINT);
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allFilesWithExtension(getSourceDirectory(), "xml");
    }

    @Override
    public List<LintError> getLintErrors(File file) {
        List<LintError> lintErrorList = Lists.newArrayList();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(null); // shut up!
            Document document = documentBuilder.parse(file);

            document.getDocumentElement().normalize();
        }
        catch (SAXException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.matches("The version is required in the XML declaration.")) {
                lintErrorList.add(LintError.with(this, file).andErrorMessage(errorMessage.substring(0,
                        errorMessage.length() - 1)).create());
            }
            else {
                lintErrorList.add(LintError.with(this, file).andErrorMessage("Error checking rule, " +
                        "" + "could not parse XML").andException(e).create());
            }
        }
        catch (Exception e) {
            lintErrorList.add(LintError.with(this, file).andErrorMessage("Error checking rule, " +
                    "could not parse XML").andException(e).create());
        }

        return lintErrorList;
    }
}
