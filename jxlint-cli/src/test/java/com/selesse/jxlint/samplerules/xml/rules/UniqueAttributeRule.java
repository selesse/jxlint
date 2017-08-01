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

public class UniqueAttributeRule extends LintRule {
    public UniqueAttributeRule() {
        super("Unique attribute", "Attributes within a tag must be unique.",
                "Attributes within an XML tag must be unique. That is, <tag a=\"x\" a=\"y\"> is invalid.",
                Severity.ERROR, Category.PERFORMANCE);
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
            if (errorMessage.matches("Attribute \"([^\"]+)\" was already specified for element \"([^\"]+)\"\\.")) {
                lintErrorList.add(LintError.with(this, file).andErrorMessage(errorMessage.substring(0,
                        errorMessage.length() - 1)).andException(e).create());
            }
            else {
                lintErrorList.add(LintError.with(this, file).andErrorMessage("Error checking rule, " +
                        "could not parse XML").andException(e).create());
            }
        }
        catch (Exception e) {
            // this will catch parser configuration errors as well as I/O exceptions
            lintErrorList.add(LintError.with(this, file).andErrorMessage("Error checking rule, " +
                    "could not parse XML").andException(e).create());
        }

        return lintErrorList;
    }
}
