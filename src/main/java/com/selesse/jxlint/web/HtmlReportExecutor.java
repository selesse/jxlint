package com.selesse.jxlint.web;

import com.google.common.io.Files;
import com.selesse.jxlint.Jxlint;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.settings.ProgramSettings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
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
    private final ProgramSettings programSettings;
    private final String[] args;

    public HtmlReportExecutor(ProgramSettings programSettings, String[] args) {
        this.programSettings = programSettings;
        this.args = Arrays.copyOf(args, args.length);
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
        File directory = new File(args[args.length - 1]);
        return directory.exists() && directory.isDirectory();
    }

    public void generateReport() {
        Jxlint jxlint = new Jxlint(LintRulesImpl.getInstance(), programSettings, false);
        jxlint.parseArgumentsAndDispatch(args);
    }

}

