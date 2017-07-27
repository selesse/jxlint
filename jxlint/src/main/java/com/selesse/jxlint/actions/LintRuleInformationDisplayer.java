package com.selesse.jxlint.actions;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.ExitException;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.settings.ProgramSettings;

import java.util.List;

/**
 * Class that is knowledgeable about {@link LintRules}. The corresponding static methods print out their knowledge, and
 * exit the program using {@link com.selesse.jxlint.ProgramExitter}.
 */
public class LintRuleInformationDisplayer {
    /**
     * List all of the {@link LintRule}s, in summary form:
     *
     * <pre>
     *     "Lint Rule 1" : This rule looks at source code.
     *     "Lint Rule 2" : This rule looks at source code comments.
     * </pre>
     *
     * @throws ExitException
     */
    public static void listRules() throws ExitException {
        List<String> outputBuffer = Lists.newArrayList();

        LintRules lintRules = LintRulesImpl.getInstance();
        if (lintRules.getAllRules().size() == 0) {
            outputBuffer.add("There are no rules defined.");
        }
        for (LintRule lintRule : lintRules.getAllRules()) {
            outputBuffer.add(lintRule.getSummaryOutput());
        }

        throw new ExitException(Joiner.on("\n").join(outputBuffer), ExitType.SUCCESS);
    }

    /**
     * List the {@link LintRule}s, in detailed form. Uses
     * {@link com.selesse.jxlint.model.rules.LintRule#getDetailedOutput()} to print individual rules.
     *
     * @throws ExitException
     */
    public static void showRules(ProgramOptions programOptions) throws ExitException {
        try {
            StringBuilder outputBuffer = new StringBuilder();
            if (programOptions.hasOption(JxlintOption.SHOW) && programOptions.getOption(JxlintOption.SHOW) == null) {
                for (LintRule rule : LintRulesImpl.getInstance().getAllRules()) {
                    outputBuffer.append(rule.getDetailedOutput()).append("\n\n");
                }
                outputBuffer.append("There are ").append(
                        LintRulesImpl.getInstance().getAllRules().size()).append(" rules.");
            }
            else {
                Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();

                List<String> rules = splitter.splitToList(programOptions.getOption(JxlintOption.SHOW));
                for (String rule : rules) {
                    LintRule lintRule = LintRulesImpl.getInstance().getLintRule(rule.trim());
                    outputBuffer.append(lintRule.getDetailedOutput()).append("\n\n");
                }
            }
            throw new ExitException(outputBuffer.toString(), ExitType.SUCCESS);
        }
        catch (NonExistentLintRuleException e) {
            throw new ExitException(String.format("'%s' is not a valid rule.", e.getRuleName()),
                    ExitType.COMMAND_LINE_ERROR);
        }
    }

    public static void printMarkdownRuleReport(ProgramSettings settings) throws ExitException {
        StringBuilder outputStringBuilder = new StringBuilder();
        String headerString =
                String.format("Rules for %s - %s", settings.getProgramName(), settings.getProgramVersion());
        outputStringBuilder.append(underline(headerString, "=")).append("\n\n");

        List<LintRule> allRules = LintRulesImpl.getInstance().getAllRules();
        for (int i = 0; i < allRules.size(); i++) {
            LintRule lintRule = allRules.get(i);
            outputStringBuilder.append(getMarkdownString(lintRule)).append("\n");
            if (i + 1 < allRules.size()) {
                outputStringBuilder.append("---");
            }
            outputStringBuilder.append("\n\n");
        }

        throw new ExitException(outputStringBuilder.toString(), ExitType.SUCCESS);
    }

    private static String underline(String string, String underlineCharacter) {
        int numberOfChars = string.length();

        StringBuilder underLineChar = new StringBuilder();

        for (int i = 0; i < numberOfChars; i++) {
            underLineChar.append(underlineCharacter);
        }

        return string + "\n" + underLineChar.toString();
    }

    private static String getMarkdownString(LintRule lintRule) {
        StringBuilder outputStringBuilder = new StringBuilder();

        // Name of a rule
        // --------------
        // **Summary** : summary
        // **Category** : CATEGORY
        // **Severity** : Severity
        // **Enabled by default?** : yes
        //
        // **Detailed description** :
        //
        // detailedExplanation
        outputStringBuilder.append(underline(lintRule.getName(), "-")).append("\n");
        outputStringBuilder.append("**Summary** : ").append(lintRule.getSummary()).append("\n\n");
        outputStringBuilder.append("**Category** : ").append(lintRule.getCategory()).append("\n\n");
        outputStringBuilder.append("**Severity** : ").append(lintRule.getSeverity()).append("\n\n");
        outputStringBuilder.append("**Enabled by default?** : ").append(lintRule.isEnabled() ? "yes" : "no")
                .append("\n\n");
        outputStringBuilder.append("\n").append("**Detailed description** :").append("\n\n");

        String explanation = lintRule.getDetailedDescription();
        outputStringBuilder.append(explanation).append("\n");
        return outputStringBuilder.toString();
    }
}
