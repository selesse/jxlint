package com.selesse.jxlint.report;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.xml.XmlEscapers;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.settings.ProgramSettings;
import com.selesse.jxlint.utils.EnumUtils;

import java.io.PrintStream;
import java.util.List;

/**
 * The XmlReporter provides a basic XML report. The schema is:
 *
 * <pre>{@code
 * <issues>
 *     <issue name severity message category summary explanation location />
 * </issues>
 * }</pre>
 *
 */
class XmlReporter extends Reporter {

    public XmlReporter(PrintStream out, ProgramSettings programSettings, ProgramOptions options,
                       List<LintError> lintErrorList) {
        super(out, programSettings, options, lintErrorList);
    }

    @Override
    public void printHeader() {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<issues>");
    }

    @Override
    public void printCategoryHeader(Enum<?> category) {
    }

    @Override
    public void printError(LintError error) {
        LintRule violatedRule = error.getViolatedRule();
        out.println("    <issue");
        List<String> outputBuffer = Lists.newArrayList(
                "        name=\"" + xmlEncode(violatedRule.getName()) + "\"",
                "        severity=\"" + xmlEncode(EnumUtils.toHappyString(violatedRule.getSeverity())) + "\"",
                "        message=\"" + xmlEncode(error.getMessage()) + "\"",
                "        category=\"" + xmlEncode(violatedRule.getCategory().toString()) + "\"",
                "        summary=\"" + xmlEncode(violatedRule.getSummary()) + "\"",
                "        explanation=\"" + xmlEncode(violatedRule.getDetailedDescription()) + "\"",
                "        location=\"" + xmlEncode(error.getFile().getAbsolutePath()) + "\"",
                "    />"

        );
        out.println(Joiner.on("\n").join(outputBuffer));
    }

    private String xmlEncode(String string) {
        return XmlEscapers.xmlAttributeEscaper().escape(string);
    }

    @Override
    public void printFooter() {
        out.println("</issues>");
    }
}
