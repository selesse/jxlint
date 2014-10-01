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




