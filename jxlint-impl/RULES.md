Rules for jxlint-impl - 0.1.0
=============================

Functions starting with 'test' are tests
----------------------------------------
**Summary** : Functions in tests starting with 'test' are annotated with @Test

**Category** : Probably An Accident

**Severity** : ERROR

**Enabled by default?** : yes


**Detailed description** :

This rule is only applicable to JUnit 4+ applications.

In a class ending with `Test.java`, functions that start with the pattern
'public void test' should probably be annotated with @Test.

For example:

    @Test
    public void testThatOneIsEqualToTwo() {
        assertThat(1).isEqualTo(2);
    }

    public void testThatThisRuleIsAmazing() {
        someString = computeSomeString();

        assertThat(someString).isEqualTo("someOtherString");
    }

In this case, `testThatThisRuleIsAmazing` won't get executed because it is not
annotated with `@Test`.

---

SLF4J loggers should not use String.format
------------------------------------------
**Summary** : SLF4J loggers should use parametrized logging, not String.format

**Category** : Probably An Accident

**Severity** : WARNING

**Enabled by default?** : yes


**Detailed description** :

Java files that make use of SLF4J should generally favor parametrized logging
over `String.format` logging

For example:

    logger.info(String.format("Found %s", someVariable));

Could more easily be written as

    logger.info("Found {}", someVariable);

**Note**: the implementation does not fully parse symbols, so it approximates by
looking for SLF4J imports and usages of `LoggerFactory.getLogger`.




