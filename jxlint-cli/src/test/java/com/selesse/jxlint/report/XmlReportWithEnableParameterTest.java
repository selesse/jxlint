package com.selesse.jxlint.report;

import com.selesse.jxlint.TestFileCreator;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlEncodingRule;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class XmlReportWithEnableParameterTest extends AbstractReportTest {

    @Test
    public void makeSureXmlReportGetsCreated() throws IOException, ParserConfigurationException, SAXException {
        Map<JxlintOption, String> options = Collections.singletonMap(JxlintOption.ENABLE, "\"XML encoding specified\"");
        File createdFile = ensureReportGetsCreated(OutputType.XML, options, 1);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(createdFile);

        NodeList nodeList = document.getElementsByTagName("issue");

        assertThat(nodeList.getLength()).isEqualTo(1);

        XmlEncodingRule xmlEncodingRule = new XmlEncodingRule();
        AuthorTagRule authorTagRule = new AuthorTagRule();
        Linter linter = LinterFactory.getInstance();

        for (int i = 0; i < nodeList.getLength(); i++) {
            LintRule rule;
            File ruleLocation;
            LintError lintError = linter.getLintErrors().get(i);
            if (i == 0) {
                rule = xmlEncodingRule;
                ruleLocation = new File(createdFile.getParentFile(), TestFileCreator.getBadEncodingFileName());
            }
            else {
                rule = authorTagRule;
                ruleLocation = new File(createdFile.getParentFile(), TestFileCreator.getBadAuthorFileName());
            }

            Node node = nodeList.item(i);
            NamedNodeMap namedNodeMap = node.getAttributes();

            String name = getAttributeValue(namedNodeMap, "name");
            String severity = getAttributeValue(namedNodeMap, "severity");
            String message = getAttributeValue(namedNodeMap, "message");
            String category = getAttributeValue(namedNodeMap, "category");
            String summary = getAttributeValue(namedNodeMap, "summary");
            String explanation = getAttributeValue(namedNodeMap, "explanation");
            String location = getAttributeValue(namedNodeMap, "location");

            // Information about the rule
            assertThat(name).isNotNull().isEqualTo(rule.getName());
            assertThat(severity).isNotNull().isEqualToIgnoringCase(rule.getSeverity().toString());
            assertThat(category).isNotNull().isEqualToIgnoringCase(rule.getCategory().toString());
            assertThat(summary).isNotNull().isEqualTo(rule.getSummary());
            assertThat(location).isNotNull().isEqualTo(ruleLocation.getAbsolutePath());

            // Information about the error
            assertThat(message).isEqualTo(lintError.getMessage());
            assertThat(explanation).isEqualTo(lintError.getViolatedRule().getDetailedDescription());
        }
    }

    private String getAttributeValue(NamedNodeMap namedNodeMap, String attribute) {
        Node node = namedNodeMap.getNamedItem(attribute);
        if (node != null) {
            return node.getNodeValue();
        }
        return null;
    }
}
