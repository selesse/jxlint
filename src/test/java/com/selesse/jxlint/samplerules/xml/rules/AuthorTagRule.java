package com.selesse.jxlint.samplerules.xml.rules;

import com.google.common.collect.Lists;
import com.selesse.jxlint.model.rules.Category;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.utils.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class AuthorTagRule extends LintRule {
    public AuthorTagRule() {
        super("Author tag specified", "author.xml files must contain a valid root-element <author> tag.", "",
                Severity.WARNING, Category.STYLE, false);
        setDetailedDescription(getMarkdownDescription());
    }

    @Override
    public List<File> getFilesToValidate() {
        return FileUtils.allFilesWithFilename(getSourceDirectory(), "author.xml");
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
                    lintErrorList.add(LintError.with(this, file).andErrorMessage(failedRuleString).create());
                    return lintErrorList;
                }
                // If we make it to this path, we've passed the rule: return the empty list
                return lintErrorList;
            }
        }
        catch (Exception e) {
            // this will catch parser configuration errors, XML parse errors, as well as I/O exceptions
            lintErrorList.add(LintError.with(this, file).andErrorMessage("Error checking rule, could not parse XML")
                    .andException(e).create());
            return lintErrorList;
        }

        lintErrorList.add(LintError.with(this, file).andErrorMessage("Author element was not root element").create());
        return lintErrorList;
    }
}
