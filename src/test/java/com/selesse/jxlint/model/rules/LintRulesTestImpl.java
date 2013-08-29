package com.selesse.jxlint.model.rules;

/**
 * Implementation of a {@link LintRules} used for testing.
 */
public class LintRulesTestImpl extends AbstractLintRules {
    @Override
    void initializeLintTasks() {
        // Example rule saying that XML must be valid
        lintRules.add(new LintRule("Valid XML", "XML must be well-formed, valid.", "The XML needs to be \"valid\" " +
                "XML. This test definition means that the XML can be parsed by any parser. Any tag must be closed.",
                Severity.FATAL, Category.DEFAULT) {

            @Override
            public boolean validate() {
                return true;
            }
        });

        // Example rule saying that duplicate attribute tags within XML are bad
        lintRules.add(new LintRule("Unique attribute", "Attributes within a tag must be unique.",
                "Attributes within an XML tag must be unique. That is, <tag a=\"x\" a=\"y\"> is invalid.",
                Severity.WARNING, Category.DEFAULT) {
            @Override
            public boolean validate() {
                return true;
            }
        });

        // Example (disabled-by-default) rule
        lintRules.add(new LintRule("XML version specified", "Version of XML must be specified.",
                "The xml version should be specified. For example, <?xml version=\"1.0\">.",
                Severity.WARNING, Category.DEFAULT, false) {
            @Override
            public boolean validate() {
                return true;
            }
        });
    }
}
