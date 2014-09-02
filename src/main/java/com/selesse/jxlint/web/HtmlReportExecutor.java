package com.selesse.jxlint.web;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.Jxlint;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.settings.ProgramSettings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class HtmlReportExecutor {
    private static String reportPath;
    static {
        try {
            reportPath = File.createTempFile("jxlint", "html").getAbsolutePath();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final String folderToValidate;
    private final ProgramSettings programSettings;
    private final List<String> argList;

    public HtmlReportExecutor(String folderToValidate, ProgramSettings programSettings, String[] args) {
        this.folderToValidate = folderToValidate;
        this.programSettings = programSettings;
        this.argList = Lists.newArrayList(args);
    }

    public static String reportFileContents() {
        try {
            return new String(Files.toByteArray(new File(reportPath)), Charset.defaultCharset());
        }
        catch (IOException ignored) {
            // You only live once
        }
        return "";
    }

    public static List<LintRule> getAvailableRules() {
        return LintRulesImpl.getInstance().getAllRules();
    }

    public boolean directoryExists() {
        File directory = new File(argList.get(argList.size() - 1));
        return directory.exists() && directory.isDirectory();
    }

    public void generateReport() {
        programSettings.initializeForWeb(new File(folderToValidate));
        String[] newArgs = new String[argList.size() + 2];
        argList.add(0, "--html");
        argList.add(1, reportPath);

        argList.toArray(newArgs);

        Jxlint jxlint = new Jxlint(LintRulesImpl.getInstance(), programSettings, false);
        jxlint.parseArgumentsAndDispatch(newArgs);
    }

}

