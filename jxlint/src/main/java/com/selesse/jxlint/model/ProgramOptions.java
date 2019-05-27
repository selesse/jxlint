package com.selesse.jxlint.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.selesse.jxlint.model.rules.Categories;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.utils.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * jxlint program options. This contains information relating to {@link com.selesse.jxlint.model.JxlintOption}s.
 * It is a {@link java.util.Map} of {@link com.selesse.jxlint.model.JxlintOption}s to {@link java.lang.String}s.
 */
public class ProgramOptions {
    private Map<JxlintOption, String> options;
    private String sourceDirectory;
    private Optional<List<Enum<?>>> enabledCategories;
    private Optional<List<LintRule>> checkRules;
    private Optional<List<LintRule>> disabledRules;
    private Optional<List<LintRule>> enabledRules;

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
        switch (option) {
            case CATEGORY:
                enabledCategories = null;
                break;
            case CHECK:
                checkRules = null;
                break;
            case ENABLE:
                enabledRules = null;
                break;
            case DISABLE:
                disabledRules = null;
                break;
            default:
                //nothing to do
                break;
        }
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
            case "jenkins-xml":
                return OutputType.JENKINS_XML;
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
     * Get the list of enabled categories, either set with {@link #setEnabledCategories(List)} or with the the option
     * value corresponding to the {@link JxlintOption#CATEGORY} key. In this second case the list will be computed with
     * the {@link #getCategoryListFromOptionString(String)} method.
     *
     * @return an optional containing the list of categories if the option is set.
     * @throws NonExistentCategoryException
     */
    public Optional<List<Enum<?>>> getEnabledCategories() throws NonExistentCategoryException {
        if (enabledCategories == null) {
            if (hasOption(JxlintOption.CATEGORY)) {
                enabledCategories = Optional.of(Collections
                        .unmodifiableList(getCategoryListFromOptionString(options.get(JxlintOption.CATEGORY))));
            }
            else {
                enabledCategories = Optional.empty();
            }
        }
        return enabledCategories;
    }

    /**
     * Set the list of enabled categories, this will update the option value corresponding to the
     * {@link JxlintOption#CATEGORY} key.
     *
     * @param enabledCategories
     *            list of categories. <code>null</code> value means no option set.
     */
    public void setEnabledCategories(List<Enum<?>> enabledCategories) {
        this.enabledCategories =
                computeSetterValueAndFixOptionsValue(JxlintOption.CATEGORY, enabledCategories, c -> c.toString());
    }

    /**
     * Get the list of rules to check, either set with {@link #setCheckRules(List)} or with the option value
     * corresponding to the {@link JxlintOption#CHECK} key. In this second case the list will be computed with the
     * {@link #getRuleListFromOptionString(String)} method.
     *
     * @return an optional containing the list of rules if the option is set.
     * @throws NonExistentLintRuleException
     */
    public Optional<List<LintRule>> getCheckRules() throws NonExistentLintRuleException {
        if (checkRules == null) {
            checkRules = computeGetterValueWhenNull(JxlintOption.CHECK);
        }
        return checkRules;
    }

    /**
     * Set the list of rules to check. This will update the option value corresponding to the {@link JxlintOption#CHECK}
     * key.
     *
     * @param checkRules
     *            list of rules. <code>null</code> value means no option set.
     */
    public void setCheckRules(List<LintRule> checkRules) {
        this.checkRules =
                computeSetterValueAndFixOptionsValue(JxlintOption.CHECK, checkRules, r -> r.getName());
    }

    /**
     * Get the list of rules to disable, either set with {@link #setDisabledRules(List)} or with the option value
     * corresponding to the {@link JxlintOption#DISABLE} key. In this second case the list will be computed with the
     * {@link #getRuleListFromOptionString(String)} method.
     *
     * @return an optional containing the list of rules if the option is set.
     * @throws NonExistentLintRuleException
     */
    public Optional<List<LintRule>> getDisabledRules() throws NonExistentLintRuleException {
        if (disabledRules == null) {
            disabledRules = computeGetterValueWhenNull(JxlintOption.DISABLE);
        }
        return disabledRules;
    }

    /**
     * Set the list of rules to disable. This will update the option value corresponding to the
     * {@link JxlintOption#DISABLE} key.
     *
     * @param disabledRules
     *            list of rules. <code>null</code> value means no option set.
     */
    public void setDisabledRules(List<LintRule> disabledRules) {
        this.disabledRules =
                computeSetterValueAndFixOptionsValue(JxlintOption.DISABLE, disabledRules, r -> r.getName());
    }

    /**
     * Get the list of rules to check, either set with {@link #setEnabledRules(List)} or with the option value
     * corresponding to the {@link JxlintOption#ENABLE} key. In this second case the list will be computed with the
     * {@link #getRuleListFromOptionString(String)} method.
     *
     * @return an optional containing the list of rules if the option is set.
     * @throws NonExistentLintRuleException
     */
    public Optional<List<LintRule>> getEnabledRules() throws NonExistentLintRuleException {
        if (enabledRules == null) {
            enabledRules = computeGetterValueWhenNull(JxlintOption.ENABLE);
        }
        return enabledRules;
    }

    /**
     * Set the list of rules to enable. This will update the option value corresponding to the
     * {@link JxlintOption#ENABLE} key.
     *
     * @param enabledRules
     *            list of rules. <code>null</code> value means no option set.
     */
    public void setEnabledRules(List<LintRule> enabledRules) {
        this.enabledRules =
                computeSetterValueAndFixOptionsValue(JxlintOption.ENABLE, enabledRules, r -> r.getName());
    }

    private Optional<List<LintRule>> computeGetterValueWhenNull(JxlintOption jxlintOption)
            throws NonExistentLintRuleException {
        if (hasOption(jxlintOption)) {
            return Optional.of(Collections
                    .unmodifiableList(getRuleListFromOptionString(options.get(jxlintOption))));
        }
        else {
            return Optional.empty();
        }
    }

    private <T> Optional<List<T>> computeSetterValueAndFixOptionsValue(JxlintOption jxlintOption, List<T> listValue,
            Function<T, String> mapper) {
        if (listValue != null) {
            String stringValue = listValue.stream().map(mapper).collect(Collectors.joining(","));
            addOption(jxlintOption, stringValue);
            return Optional.of(Collections.unmodifiableList(listValue));
        }
        else {
            //corresponds to a remove
            options.remove(jxlintOption);
            return Optional.empty();
        }
    }

    /**
     * Returns a list of rules from the raw option string. In other words, given a raw option string "Rule1, Rule2"
     * (i.e. parsed from <code>--disable "Rule1, Rule2"</code>), this returns a list with the instances having "Rule1"
     * and "Rule2" as name. If any of the rules have a name corresponding to the passed strings, this will function will
     * throw a {@link com.selesse.jxlint.model.rules.NonExistentLintRuleException}.
     */
    public static List<LintRule> getRuleListFromOptionString(String optionString)
            throws NonExistentLintRuleException {
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();

        List<String> rulesStringList = splitter.splitToList(optionString);
        return getRuleListFromRuleNameList(rulesStringList);
    }

    public static List<LintRule> getRuleListFromRuleNameList(List<String> ruleNameList)
            throws NonExistentLintRuleException {
        List<LintRule> rules = Lists.newArrayList();
        for (String ruleName : ruleNameList) {
            rules.add(LintRulesImpl.getInstance().getLintRule(ruleName));
        }
        return rules;
    }

    public static List<Enum<?>> getCategoryListFromOptionString(String categoryOptionString)
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
    public static List<Enum<?>> getCategoryListFromCategoryNameList(List<String> categoryNameList)
            throws NonExistentCategoryException {
        List<Enum<?>> categoryList = Lists.newArrayList();
        Enum<?>[] categories = Categories.get().getEnumConstants();
        Map<String, Enum<?>> categoryMap = Maps.newHashMap();
        for (Enum<?> category : categories) {
            categoryMap.put(category.toString(), category);
        }

        for (String categoryName : categoryNameList) {
            if (!categoryMap.containsKey(categoryName)) {
                throw new NonExistentCategoryException(categoryName, categoryMap.keySet());
            }
            categoryList.add(categoryMap.get(categoryName));
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
