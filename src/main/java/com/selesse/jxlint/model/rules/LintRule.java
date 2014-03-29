package com.selesse.jxlint.model.rules;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.utils.EnumUtils;

import java.io.File;
import java.util.List;

/**
 * A lint rule model object. Should be extended to create rules. A full implementation is completed by extending this
 * class, and implementing the methods the compiler complains about. A sample implementation is demonstrated below.
 *
 * <pre><code>
 * public class MyRule extends LintRule {
 *     {@literal @}Override
 *     public MyRule() {
 *         super("My name", "My summary", "My detailed description", Severity.ERROR, Category.LINT);
 *     }
 *
 *     {@literal @}Override
 *     public List&lt;File&gt; getFilesToValidate() {
 *         return FileUtils.allFiles(getSourceDirectory());
 *     }
 *
 *     {@literal @}Override
 *     public List&lt;LintError&gt; getLintErrors(File file) {
 *         List&lt;LintError&gt; lintErrorList = Lists.newArrayList();
 *
 *         List&lt;String&gt; fileContents = Files.readLines(file, Charset.defaultCharset());
 *
 *         for (String line : fileContents) {
 *             if (line.contains("Hello, world!")) {
 *                 // There is no error, we passed the rule!
 *                 return lintErrorList;
 *             }
 *         }
 *
 *         lintErrorList.add(LintError.with(this, file).andErrorMessage("Must say hello world!").create());
 *         return lintErrorList;
 *     }
 * }
 * </code>
 * </pre>
 */
public abstract class LintRule {
    private String name;
    private String summary;
    private String detailedDescription;
    private Severity severity;
    private Category category;
    private boolean enabled = true;
    protected List<LintError> lintErrors;

    public LintRule(String name, String summary, String detailedDescription, Severity severity, Category category) {
        this.name = name;
        this.summary = summary;
        this.detailedDescription = detailedDescription;
        this.severity = severity;
        this.category = category;
        this.lintErrors = Lists.newArrayList();
    }

    public LintRule(String name, String summary, String detailedDescription, Severity severity, Category category,
                    boolean isEnabledByDefault) {
        this(name, summary, detailedDescription, severity, category);
        this.enabled = isEnabledByDefault;
    }

    /**
     * Return a list of {@link File}s to perform this rule's validation on. Several utility methods have been created
     * in {@link com.selesse.jxlint.utils.FileUtils} to make this easy and are sampled below.
     *
     * <pre>{@code
     *      FileUtils.allFiles(getSourceDirectory()); // All the files in directory we're validating (recursive)
     *      FileUtils.allFilesWithExtension(getSourceDirectory, "txt"); // All .txt files in directory we're validating
     * }</pre>
     */
    public abstract List<File> getFilesToValidate();

    /**
     * Goes through every file and calls {@link #getLintErrors(java.io.File)} on it. If there is an error,
     * it is added to {@link #lintErrors}.
     */
    public void validate() {
        try {
            for (File file : getFilesToValidate()) {
                List<LintError> fileLintErrors = getLintErrors(file);
                lintErrors.addAll(fileLintErrors);
            }
        } catch (Exception e) {
            throw new RuntimeException("The \"" + this.getClass().getSimpleName() + "\" rule threw an exception " +
                    "when trying to validate:\n" + Joiner.on("\n").join(e.getStackTrace()));
        }
    }

    /**
     * Get a {@link java.util.List} of {@link com.selesse.jxlint.model.rules.LintError}s from a file.
     * The list should be empty if it passed the validation.
     * This is the function that actually performs the validation for a given file.
     */
    public abstract List<LintError> getLintErrors(File file);

    /**
     * Checks to see if a particular file passes this rule.
     */
    public boolean passesValidation(File file) {
        List<LintError> fileLintErrors = getLintErrors(file);
        return fileLintErrors.size() == 0;
    }

    /**
     * Gets the name of the rule. This name is used with command line switches and in displaying summary information.
     * Can contain spaces, but should be relatively short.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets a one or two line summary of the rule. This is used when displaying summary information via
     * {@link #getSummaryOutput()}.
     */
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Gets a detailed description of the rule in question. Should be as specific as possible and contain examples.
     * Used in {@link #getDetailedOutput()}.
     */
    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Return a short summary output:
     * <pre>
     *     "Rule Name" : Summary information
     *     "Rule that is not enabled by default"* : This rule is not enabled by default.
     * </pre>
     */
    public String getSummaryOutput() {
        return String.format("\"%s\"%s : %s", getName(), isEnabled() ? "" : "*", getSummary());
    }

    /**
     * Return a detailed output of this rule. Prints all of its relevant information.
     */
    public String getDetailedOutput() {
        List<String> detailedOutput = Lists.newArrayList(
                getName(),
                // Underline the name with "-"s. Hacky-ish, but works well.
                new String(new char[getName().length()]).replace("\0", "-"),
                "Summary: " + getSummary(),
                isEnabled() ? "" : "\n** Disabled by default **\n",
                "Severity: " + EnumUtils.toHappyString(getSeverity()),
                "Category: " + EnumUtils.toHappyString(getCategory()),
                "",
                getDetailedDescription()
        );

        return Joiner.on("\n").join(detailedOutput);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (detailedDescription != null ? detailedDescription.hashCode() : 0);
        result = 31 * result + (severity != null ? severity.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (lintErrors != null ? lintErrors.hashCode() : 0);
        return result;
    }

    /**
     * Ad-hoc <code>equals</code>. If the object is a LintRule, we compare getName() with the object's
     * getName, in a case-insensitive way.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LintRule) {
            return ((LintRule) obj).getName().equalsIgnoreCase(getName());
        }
        return super.equals(obj);
    }

    /**
     * Returns true if this rule's name is a member of this list of strings.
     * It's like {@link List#contains(Object)}, but on the {@link com.selesse.jxlint.model.rules.LintRule}
     * rather than the list. Does a case-insensitive string comparison.
     */
    public boolean hasNameInList(List<String> ruleStrings) {
        for (String string : ruleStrings) {
            if (string.equalsIgnoreCase(getName())) {
                return true;
            }
        }
        return false;
    }

    public List<LintError> getLintErrors() {
        return lintErrors;
    }

    /**
     * Get the source/root directory. This is the directory that was passed to the program,
     * i.e. "java -jar myjar.jar sourceDirectory".
     */
    public File getSourceDirectory() {
        return LintRulesImpl.getInstance().getSourceDirectory();
    }
}
