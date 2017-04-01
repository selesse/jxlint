package com.selesse.jxlint.report;

import com.google.common.base.Objects;
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

@SuppressWarnings({"unused", "WeakerAccess"}) // These functions are used by `report.vm`.
public class HtmlTemplateHelper {
    private static final Pattern alphanumeric = Pattern.compile("[a-zA-Z0-9_]");

    public static String getHrefSafeName(String string) {
        String hrefSafeName = "" + string.hashCode();

        Matcher alphanumericMatcher = alphanumeric.matcher(string);
        StringBuilder stringBuilder = new StringBuilder();
        while (alphanumericMatcher.find()) {
            stringBuilder.append(alphanumericMatcher.group());
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
        String exceptionMessage = Objects.firstNonNull(exception.getMessage(), exception.getClass().getSimpleName());
        StringBuilder sanitizedStackTrace = new StringBuilder(exceptionMessage).append("\n");
        List<StackTraceElement> stackTrace = Lists.newArrayList(exception.getStackTrace());
        for (StackTraceElement stackTraceElement : stackTrace) {
            sanitizedStackTrace.append(HtmlTemplateHelper.htmlEscape(stackTraceElement.toString())).append("\n");
        }
        return sanitizedStackTrace.toString();
    }

    public static String markdownToHtml(String description) {
        PegDownProcessor pegDownProcessor = new PegDownProcessor();
        return pegDownProcessor.markdownToHtml(description);
    }

}
