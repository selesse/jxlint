package com.selesse.jxlint.report;

import com.selesse.jxlint.TestFileCreator;
import com.selesse.jxlint.linter.Linter;
import com.selesse.jxlint.linter.LinterFactory;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlEncodingRule;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class JenkinsXmlReportTest extends AbstractReportTest {
    @Test
    public void makeSureXmlReportGetsCreated() throws IOException, ParserConfigurationException, SAXException {
        File createdFile = ensureReportGetsCreatedFor2Errors(OutputType.JENKINS_XML);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(createdFile);

        NodeList nodeList = document.getElementsByTagName("issue");

        assertThat(nodeList.getLength()).isEqualTo(2);

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

            Node issueNode = nodeList.item(i);
            NodeList issueChildNodes = issueNode.getChildNodes();
            Map<String, String> issueContent = new HashMap<>();
            for (int j = 0; j < issueChildNodes.getLength(); j++) {
                Node node = issueChildNodes.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (issueContent.containsKey(node.getNodeName())) {
                        fail("Found duplicate nodeName:" + node.getNodeName());
                    }
                    issueContent.put(node.getNodeName(), node.getTextContent());
                }
            }
            assertThat(issueContent).hasSize(7);

            String name = issueContent.get("name");
            String severity = issueContent.get("severity");
            String message = issueContent.get("message");
            String category = issueContent.get("category");
            String type = issueContent.get("type");
            String description = issueContent.get("description");
            String fileName = issueContent.get("fileName");

            // Information about the rule
            assertThat(name).isNotNull().isEqualTo(rule.getName());
            assertThat(severity).isNotNull();
            if (rule.getSeverity() == Severity.WARNING) {
                assertThat(severity).isEqualTo("HIGH");
            }
            else if (rule.getSeverity() == Severity.ERROR) {
                assertThat(severity).isEqualTo("ERROR");
            }
            else if (rule.getSeverity() == Severity.FATAL) {
                assertThat(severity).isEqualTo("ERROR");
            }
            else {
                assertThat(severity).isEqualTo("NORMAL");
            }
            assertThat(category).isNotNull().isEqualToIgnoringCase(rule.getCategory().toString());
            assertThat(type).isNotNull().isEqualTo(rule.getSummary());
            assertThat(fileName).isNotNull().isEqualTo(ruleLocation.getAbsolutePath());

            // Information about the error
            assertThat(message).isEqualTo(lintError.getMessage());
            assertThat(description)
                .isEqualTo(HtmlTemplateHelper.markdownToHtml(lintError.getViolatedRule().getDetailedDescription()));
        }
    }
}
