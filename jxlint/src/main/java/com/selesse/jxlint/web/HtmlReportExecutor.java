package com.selesse.jxlint.web;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.Jxlint;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.settings.ProgramSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class HtmlReportExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReportExecutor.class);
    private static String reportPath;
    static {
        try {
            reportPath = File.createTempFile("jxlint", ".html").getAbsolutePath();
        }
        catch (IOException e) {
            LOGGER.error("Error creating temporary file", e);
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
        catch (IOException e) {
            return e.getMessage();
        }
    }

    public static List<LintRule> getAvailableRules() {
        return LintRulesImpl.getInstance().getAllRules();
    }

    public boolean directoryExists() {
        File directory = new File(argList.get(argList.size() - 1));
        return directory.isDirectory();
    }

    public void generateReport() {
        programSettings.initializeForWeb(new File(folderToValidate));
        String[] newArgs = new String[argList.size() + 2];
        argList.add(0, "--html");
        argList.add(1, reportPath);

        argList.toArray(newArgs);

        LOGGER.info("Running Jxlint with args: {}", argList);
        Jxlint jxlint = new Jxlint(LintRulesImpl.getInstance(), programSettings, false);
        jxlint.parseArgumentsAndDispatch(newArgs);
    }

}

