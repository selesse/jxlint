package com.selesse.jxlint.report;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.html.HtmlEscapers;
import com.google.common.io.Resources;
import com.selesse.jxlint.model.LintRuleComparator;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.settings.ProgramSettings;
import com.selesse.jxlint.utils.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTemplatedReporter extends Reporter {
    private static final Pattern alphanumeric = Pattern.compile("[a-zA-Z0-9_]");
    private Set<Enum<?>> violatedCategories;
    private Set<LintRule> lintRuleSet;
    private Map<LintRule, Integer> summaryMap;

    public HtmlTemplatedReporter(PrintStream out, ProgramSettings settings, List<LintError> lintErrorList) {
        super(out, settings, lintErrorList);
        violatedCategories = Sets.newTreeSet(new Comparator<Enum<?>>() {
            @Override
            public int compare(Enum<?> o1, Enum<?> o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        summaryMap = Maps.newTreeMap(new Comparator<LintRule>() {
            @Override
            public int compare(LintRule o1, LintRule o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        lintRuleSet = Sets.newLinkedHashSet();

        for (LintError lintError : lintErrorList) {
            LintRule violatedRule = lintError.getViolatedRule();
            lintRuleSet.add(violatedRule);

            violatedCategories.add(violatedRule.getCategory());
            if (!summaryMap.containsKey(violatedRule)) {
                summaryMap.put(violatedRule, 1);
            }
            else {
                int occurrences = summaryMap.get(violatedRule);
                summaryMap.put(violatedRule, occurrences + 1);
            }
        }

        Collections.sort(lintErrorList, new Comparator<LintError>() {
            @Override
            public int compare(LintError o1, LintError o2) {
                return LintRuleComparator.compareLintErrorByCategoryNameThenLineNumber(o1, o2);
            }
        });
    }

    @Override
    public void writeReport() {
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityProperties.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        VelocityEngine velocityEngine = new VelocityEngine(velocityProperties);
        velocityEngine.init();

        Template template = velocityEngine.getTemplate("velocity/report.vm");

        VelocityContext context = new VelocityContext();
        context.put("templateHelper", HtmlTemplatedReporter.class);
        context.put("Joiner", Joiner.class);
        context.put("allCss", getAllCss());
        context.put("allJs", getAllJs());
        context.put("nameAndVersion", settings.getProgramName() + " " + settings.getProgramVersion());
        context.put("date", new Date());
        context.put("lintErrorList", lintErrorList);
        context.put("categoryList", Lists.newArrayList(violatedCategories));
        context.put("navDataTargets", generateNavListString(Lists.newArrayList(violatedCategories)));
        context.put("summaryMap", summaryMap);
        context.put("errorSummaryString", getErrorReportString());
        context.put("lintRuleSet", lintRuleSet);

        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);

        out.println(stringWriter.toString());
        out.close();
    }

    private String generateNavListString(List<Enum<?>> categoryList) {
        StringBuilder dataTarget = new StringBuilder("#summary, ");

        for (int i = 0; i < categoryList.size(); i++) {
            Enum<?> enumType = categoryList.get(i);
            dataTarget.append("#").append(getHrefSafeName(enumType.toString()));

            if (i + 1 < categoryList.size()) {
                dataTarget.append(", ");
            }
        }

        return dataTarget.toString();
    }

    private String getAllCss() {
        List<String> cssFiles = Lists.newArrayList("bootstrap.min.css", "prettify.min.css");
        return concatenateVendorResources(cssFiles);
    }

    private String getAllJs() {
        List<String> jsFiles = Lists.newArrayList("prettify.min.js", "jquery.min.js", "jquery.tablesorter.min.js",
                "tab.min.js");
        return concatenateVendorResources(jsFiles);
    }

    private String concatenateVendorResources(List<String> resources) {
        StringBuilder concatenatedResource = new StringBuilder();

        for (String resource : resources) {
            URL resourceUrl = Resources.getResource("vendor/" + resource);
            String tag = resource.endsWith("css") ? "style" : "script";

            try {
                concatenatedResource.append("<").append(tag).append(">");
                String resourceToString = Resources.toString(resourceUrl, Charsets.UTF_8);
                concatenatedResource.append(resourceToString);
                concatenatedResource.append("</").append(tag).append(">");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            concatenatedResource.append("\n");
        }

        return concatenatedResource.toString();
    }


    public static String getHrefSafeName(String string) {
        String hrefSafeName = "" + string.hashCode();

        Matcher matcher = alphanumeric.matcher(string);
        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find()) {
            stringBuilder.append(matcher.group());
        }

        if (stringBuilder.length() > 0) {
            hrefSafeName = stringBuilder.toString();
        }

        return hrefSafeName.toLowerCase();
    }

    public static String relativize(File file) {
        return FileUtils.getRelativePath(LintRulesImpl.getInstance().getSourceDirectory(), file);
    }

    public static String htmlEscape(String string) {
        return HtmlEscapers.htmlEscaper().escape(string);
    }

    public static String getLabel(Severity severity) {
        switch (severity) {
            case ERROR:
            case FATAL:
                return "danger";
            case WARNING:
                return "warning";
        }
        return "warning";
    }

    public static String sanitizeStackTrace(Exception exception) {
        StringBuilder sanitizedStackTrace = new StringBuilder(exception.getMessage()).append("\n");
        List<StackTraceElement> stackTrace = Lists.newArrayList(exception.getStackTrace());
        for (StackTraceElement stackTraceElement : stackTrace) {
            sanitizedStackTrace.append(HtmlEscapers.htmlEscaper().escape(stackTraceElement.toString())).append("\n");
        }
        return sanitizedStackTrace.toString();
    }

    public static String markdownToHtml(String description) {
        PegDownProcessor pegDownProcessor = new PegDownProcessor();
        return pegDownProcessor.markdownToHtml(description);
    }

    @Override
    protected void printHeader() {}

    @Override
    protected void printCategoryHeader(Enum<?> category) {}

    @Override
    protected void printError(LintError error) {}

    @Override
    protected void printFooter() {}
}
