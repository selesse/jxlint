package com.selesse.jxlint.report;

import com.google.common.collect.Lists;
import com.google.common.html.HtmlEscapers;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.Severity;
import com.selesse.jxlint.utils.FileUtils;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTemplateHelper {
    private static final Pattern alphanumeric = Pattern.compile("[a-zA-Z0-9_]");

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

}
