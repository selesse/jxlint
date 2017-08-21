package com.selesse.jxlint.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.selesse.jxlint.model.rules.Categories;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.utils.FileUtils;

import java.io.File;
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
        switch (outputTypeString) {
            case "quiet":
                return OutputType.QUIET;
            case "html":
                return OutputType.HTML;
            case "xml":
                return OutputType.XML;
            default:
                return OutputType.DEFAULT;
        }
    }

    public String getSourcePathPrefix() {
        String result;
        if (options.containsKey(JxlintOption.SRC_PATH_PREFIX)) {
            result = options.get(JxlintOption.SRC_PATH_PREFIX);
        }
        else {
            if (getSourceDirectory() == null) {
                return "";
            }
            if (options.containsKey(JxlintOption.OUTPUT_TYPE_PATH)) {
                File outputFolder = new File(options.get(JxlintOption.OUTPUT_TYPE_PATH)).getParentFile();
                if (outputFolder != null) {
                    result = FileUtils.getRelativePath(outputFolder.getAbsoluteFile(), new File(getSourceDirectory()));
                }
                else {
                    result = new File(getSourceDirectory()).toURI().toString();
                }
            }
            else {
                result = new File(getSourceDirectory()).toURI().toString();
            }
        }
        if (!result.isEmpty() && !result.endsWith("/")) {
            result = result + "/";
        }
        return result;
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
     * will throw a {@link com.selesse.jxlint.model.rules.NonExistentLintRuleException}.
     */
    public static List<String> getRuleListFromOptionString(String optionString)
            throws NonExistentLintRuleException {
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();

        List<String> rulesStringList = splitter.splitToList(optionString);
        return getRuleListFromRuleNameList(rulesStringList);
    }

    public static List<String> getRuleListFromRuleNameList(List<String> ruleNameList)
            throws NonExistentLintRuleException {
        for (String ruleName : ruleNameList) {
            LintRulesImpl.getInstance().getLintRule(ruleName);
        }

        return Lists.newArrayList(ruleNameList);
    }

    public static List<String> getCategoryListFromOptionString(String categoryOptionString)
            throws NonExistentCategoryException {

        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        List<String> rawCategoryStringList = splitter.splitToList(categoryOptionString);

        return getCategoryListFromCategoryNameList(rawCategoryStringList);
    }

    /**
     * Ensure that the category names are valid and return a list containing the valid category names. If the name is
     * not valid, an {@link NonExistentCategoryException} is thrown.
     *
     * @param categoryNameList
     * @return categories as list
     * @throws NonExistentCategoryException
     */
    public static List<String> getCategoryListFromCategoryNameList(List<String> categoryNameList)
            throws NonExistentCategoryException {
        List<String> categoryList = Lists.newArrayList();
        Enum<?>[] categories = Categories.get().getEnumConstants();
        List<String> categoryNames = Lists.newArrayList();
        for (Enum<?> category : categories) {
            categoryNames.add(category.toString());
        }

        for (String categoryString : categoryNameList) {
            if (!categoryNames.contains(categoryString)) {
                throw new NonExistentCategoryException(categoryString, categoryNames);
            }
            categoryList.add(categoryString);
        }

        return categoryList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("options", options)
                .add("directory", getSourceDirectory())
                .toString();
    }
}
