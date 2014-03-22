package com.selesse.jxlint.samplerules.xml.rules;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.utils.FileUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class XmlEncodingRule extends LintRule {
    public XmlEncodingRule() {
        super("XML encoding specified", "Encoding of the XML must be specified.",
                "The XML encoding should be specified. For example, <?xml version=\"1.0\" encoding=\"UTF-8\"?>.",
                Severity.WARNING, Category.LINT);
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

            if (Strings.isNullOrEmpty(document.getXmlEncoding())) {
                lintErrorList.add(LintError.with(this, file).andErrorMessage("Encoding wasn't specified").create());
            }
        }
        catch (Exception e) {
            lintErrorList.add(LintError.with(this, file).andErrorMessage("Error checking rule, " +
                    "could not parse XML").andException(e).create());
        }

        return lintErrorList;
    }
}
