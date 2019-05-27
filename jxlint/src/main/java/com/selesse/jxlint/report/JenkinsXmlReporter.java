package com.selesse.jxlint.report;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.xml.XmlEscapers;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.settings.ProgramSettings;

import java.io.PrintStream;
import java.util.List;

/**
 * The XmlReporter provides a basic XML report in the "Warnings Plugin Native Format" of the
 * "Jenkins Warnings Next Generation Plugin".
 */
class JenkinsXmlReporter extends Reporter {

    public JenkinsXmlReporter(PrintStream out, ProgramSettings programSettings, ProgramOptions options,
                       List<LintError> lintErrorList) {
        super(out, programSettings, options, lintErrorList);
    }

    @Override
    public void printHeader() {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<report>");
    }

    @Override
    public void printCategoryHeader(Enum<?> category) {
    }

    @Override
    public void printError(LintError error) {
        LintRule violatedRule = error.getViolatedRule();
        out.println("  <issue>");
        List<String> outputBuffer = Lists.newArrayList(
                "    <name>" + xmlEncode(violatedRule.getName()) + "</name>",
                "    <severity>" + xmlEncode(toSeverity(violatedRule.getSeverity())) + "</severity>",
                "    <message>" + xmlEncode(error.getMessage()) + "</message>",
                "    <category>" + xmlEncode(violatedRule.getCategory().toString()) + "</category>",
                "    <type>" + xmlEncode(violatedRule.getSummary()) + "</type>",
                "    <description>" + xmlEncode(HtmlTemplateHelper.markdownToHtml(
                        violatedRule.getDetailedDescription())) + "</description>",
                "    <fileName>" + xmlEncode(error.getFile().getAbsolutePath()) + "</fileName>"
        );
        if (error.getLineNumber() > 0) {
            outputBuffer.add("    <lineStart>" + error.getLineNumber() + "</lineStart>");
        }
        outputBuffer.add("  </issue>");

        out.println(Joiner.on("\n").join(outputBuffer));
    }

    private String xmlEncode(String string) {
        return XmlEscapers.xmlAttributeEscaper().escape(string);
    }

    private String toSeverity(Severity severity) {
        if (severity == null) {
            return "NORMAL";
        }
        switch (severity) {
            case ERROR:
            case FATAL:
                return "ERROR";
            case WARNING:
                return "HIGH";
            default:
                return "NORMAL";
        }
    }

    @Override
    public void printFooter() {
        out.println("</report>");
    }
}
