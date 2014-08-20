package com.selesse.jxlint.web

import com.selesse.jxlint.model.rules.LintRule
import com.selesse.jxlint.model.rules.LintRulesImpl

public class HtmlReportExecutor {
    private static final String reportPath = System.getProperty("user.home") + "/.jxlint.html"
    private static File jar
    private String[] args

    static def setJar(File jar) {
        this.jar = jar;
    }

    public HtmlReportExecutor(String[] args) {
        this.args = args;
    }

    static String reportFileContents() {
        new File(reportPath).text
    }

    public static List<LintRule> getAvailableRules() {
        LintRulesImpl.getInstance().allRules
    }

    public boolean directoryExists() {
        def directory = new File(args.last())
        directory.exists() && directory.isDirectory()
    }

    public String generateReport() {
        def command = ['java', '-jar', jar.getAbsolutePath(), '--html', reportPath, *args]
        command.execute().text.trim()

        return ''
    }
}
