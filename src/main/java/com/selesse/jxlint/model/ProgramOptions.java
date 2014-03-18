package com.selesse.jxlint.model;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.selesse.jxlint.ProgramExitter;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;

import java.util.List;
import java.util.Map;

/**
 * jxlint program options. This contains information relating to {@link com.selesse.jxlint.model.JxlintOption}s.
 * It is a {@link java.util.Map} of {@link com.selesse.jxlint.model.JxlintOption}s to {@link java.lang.String}s.
 */
public class ProgramOptions {
    private Map<JxlintOption, String> options;
    private String sourceDirectory;

    public ProgramOptions() {
        this.options = Maps.newHashMap();
    }

    /**
     * Used for options that don't have any associated information (i.e. option == true).
     */
    public void addOption(JxlintOption option) {
        options.put(option, "true");
    }

    /**
     * Adds a {@link com.selesse.jxlint.model.JxlintOption} with a particular value.
     */
    public void addOption(JxlintOption option, String value) {
        options.put(option, value);
    }

    public boolean hasOption(JxlintOption option) {
        return options.containsKey(option);
    }

    public String getOption(JxlintOption option) {
        return options.get(option);
    }

    /**
     * Get the program's {@link com.selesse.jxlint.model.OutputType}.
     */
    public OutputType getOutputType() {
        String outputType = options.get(JxlintOption.OUTPUT_TYPE);

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

    /**
     * Get the source/root directory. This is the directory that was passed to the program,
     * i.e. "java -jar myjar.jar sourceDirectory".
     */
    public String getSourceDirectory() {
        return sourceDirectory;
    }

    /**
     * Set the source/root directory. This is the directory that was passed to the program,
     * i.e. "java -jar myjar.jar sourceDirectory".
     */
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
