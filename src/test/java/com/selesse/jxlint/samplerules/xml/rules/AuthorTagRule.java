package com.selesse.jxlint.samplerules.xml.rules;

import com.google.common.base.Optional;
import com.selesse.jxlint.model.FileUtils;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class AuthorTagRule extends LintRule {
    public AuthorTagRule() {
        super("Author tag specified", "author.xml files must contain a valid root-element <author> tag.",
                "For style purposes, every author.xml file must contain an <author> tag as the root element. " +
                "This tag should also have the 'name' and 'creationDate' attributes. " +
                "For example:\n" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\">\n" +
                "<author name=\"Steve Holt\" creationDate=\"2013-09-28\">\n" +
                "  .. content ..\n" +
                "</author>",
                Severity.WARNING, Category.STYLE, false);
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allFilesWithFilenameIn(getSourceDirectory(), "author.xml");
    }

    @Override
    public Optional<LintError> getLintError(File file) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(null); // shut up!
            Document document = documentBuilder.parse(file);

            document.getDocumentElement().normalize();

            Node node = document.getFirstChild();
            if (node.getNodeName().equals("author")) {
                NamedNodeMap namedNodeMap = node.getAttributes();
                if (namedNodeMap.getNamedItem("name") == null || namedNodeMap.getNamedItem("creationDate") == null) {
                    // report that "name", "creationDate", or both attributes are missing
                    String failedRuleString = "";
                    if (namedNodeMap.getNamedItem("name") == null) {
                        failedRuleString += "Author element does not contain \"name\" attribute";
                    }
                    if (namedNodeMap.getNamedItem("creationDate") == null) {
                        if (failedRuleString.length() > 0) {
                            failedRuleString += ". Also, author ";
                        }
                        else {
                            failedRuleString += "Author ";
                        }
                        failedRuleString += "element does not contain \"creationDate\" attribute";
                    }
                    return Optional.of(new LintError(this, file, failedRuleString));
                }
                return Optional.absent();
            }
        } catch (Exception e) {
            // this will catch parser configuration errors, XML parse errors, as well as I/O exceptions
            return Optional.of(new LintError(this, file, "Error checking rule, could not parse XML", e));
        }

        return Optional.of(new LintError(this, file, "Author element was not root element"));
    }
}
