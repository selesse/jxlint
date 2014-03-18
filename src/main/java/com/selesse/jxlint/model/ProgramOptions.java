package com.selesse.jxlint.model;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.selesse.jxlint.ProgramExitter;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;

import java.util.List;
import java.util.Map;

public class ProgramOptions {
    private Map<String, String> options;
    private String sourceDirectory;

    public ProgramOptions() {
        this.options = Maps.newHashMap();
    }

    /**
     * Used for options that don't have any associated information (i.e. option == true).
     */
    public void addOption(String optionName) {
        options.put(optionName, "true");
    }

    public void addOption(String optionName, String value) {
        options.put(optionName, value);
    }

    public boolean hasOption(String optionName) {
        return options.containsKey(optionName);
    }

    public String getOption(String show) {
        return options.get(show);
    }

    public OutputType getOutputType() {
        String outputType = options.get("outputType");

        if (outputType == null) {
            return OutputType.DEFAULT;
        }

        String outputTypeString = outputType.toLowerCase();
        if (outputTypeString.equals("quiet")) {
            return OutputType.QUIET;
        } else if (outputTypeString.equals("html")) {
            return OutputType.HTML;
        } else if (outputTypeString.equals("xml")) {
            return OutputType.XML;
        } else {
            return OutputType.DEFAULT;
        }
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * Returns a list of strings from the raw option string. In other words,
     * given a raw option string "Rule1, Rule2" (i.e. parsed from <code>--disable "Rule1, Rule2"</code>), this returns
     * a list with { "Rule1", "Rule2" }. If any of the rules passed as strings do not exist, this will function
     * will exit the program.
     */
    public static List<String> getListFromRawOptionStringOrDie(String disabledRules) {
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();

        List<String> disabledRulesStringList = Lists.newArrayList(splitter.split(disabledRules));
        for (String disabledRuleString : disabledRulesStringList) {
            try {
                LintRulesImpl.getInstance().getLintRule(disabledRuleString);
            } catch (NonExistentLintRuleException e) {
                ProgramExitter.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
            }
        }

        return Lists.newArrayList(disabledRulesStringList);
    }
}
