jxlint
======

[![Build status](https://travis-ci.org/selesse/jxlint.png)](https://travis-ci.org/selesse/jxlint)

- [Changelog](CHANGELOG.md)

jxlint is a Java framework for performing static analysis. Its name is derived
from [lint](http://en.wikipedia.org/wiki/Lint_%28software%29) tools and is
partially inspired by [Android's lint tool](http://developer.android.com/tools/help/lint.html).
Originally, it stood for "Java XML lint" but was later generalized to be
useful for any kind of static analysis.

Motivation
----------

The goal of this framework is to provide an easy, obvious way to perform file
validation. Anybody interested in verifying, adding, or removing existing
"rules" should be able to do so quickly (i.e. everything is collocated in an
obvious place), and efficiently (i.e. adding new rules is intuitive and
simple). Additionally, adding corresponding tests to rules should also be as
intuitive.

Most of the needs should be addressed by the framework; additional,
specialized tasks should not be difficult to customize.

If you are working with a framework that relies on XML for various parts of
the system and you'd like to verify that certain configurations are correct
before running the system, you might want to use this framework.

Requirements
------------

[Gradle](http://gradle.org) is required to build the code. If Gradle is not
already installed, you can use the wrapper to install it for you.

The required libraries/dependencies can be found in
[the Gradle build file](build.gradle).

Quick Start
-----------

1. Clone this repository. Run `./gradlew release publish`.
2. Specify `com.selesse:jxlint:1.2.0` as a local Maven dependency.
3. Make customizations:

  Create all your rules. It's recommended to put all the rules in one directory,
  as can be seen in the [sample implementations](src/test/java/com/selesse/jxlint/samplerules).

  ```java
  public class XmlEncodingRule extends LintRule {
      public XmlEncodingRule () {
          super("XML encoding specified", "Encoding of the XML should be specified.",
                  "The XML encoding should be specified. For example, <?xml version=\"1.0\" encoding=\"UTF-8\"?>.",
                  Severity.WARNING, Category.LINT, false);
      }

      @Override
      public List<File> getFilesToValidate() {
          return FileUtils.allFilesWithExtension(getSourceDirectory(), "xml");
      }

      @Override
      public List<LintError> getLintErrors(File file) {
          List<LintError> lintErrorList = Lists.newArrayList();
          try {
              DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
              DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
              documentBuilder.setErrorHandler(null); // silence the DOM error handler
              Document document = documentBuilder.parse(file);

              document.getDocumentElement().normalize();

              if (Strings.isNullOrEmpty(document.getXmlEncoding())) {
                  lintErrorList.add(LintError.with(this, file).
                      addMessage("Encoding wasn't specified").create());
              }
          }
          catch (Exception e) {
              lintErrorList.add(LintError.with(this, file).
                  addMessage("Error checking rule, could not parse XML").
                  addException(e).create());
          }

          return lintErrorList;
      }
  }
  ```

  Add (at least) 1 positive and 1 negative test case. A (recommended) version
  of such a tester can be found [here](src/test/java/com/selesse/jxlint/AbstractPassFailFileTest.java).
  See the Javadoc for instructions on how to set this up. Extending this class leads to
  [the following positive + negative test case](src/test/java/com/selesse/jxlint/samplerulestest/xml/XmlEncodingTest.java):

  ```java
  public class XmlEncodingTest extends AbstractPassFailFileXmlFileTest {
      public XmlEncodingTest() {
          super(new XmlEncodingRule());
      }
  }
  ```

  Set up the container by adding all your custom-defined rules.

  ```java
  public class MyXmlLintRulesImpl extends AbstractLintRules {
      @Override
      public void initializeLintRules() {
          // Example rule saying that XML must be valid
          lintRules.add(new ValidXmlRule());

          // Example rule saying that duplicate attribute tags within XML are bad
          lintRules.add(new UniqueAttributeRule());

          // Example (disabled-by-default) rules
          lintRules.add(new XmlVersionRule());
          lintRules.add(new XmlEncodingRule());
      }
  }
  ```

  In your application's Main class:

  ```java
  public class Main {
      public static void main(String[] args) {
          Jxlint jxlint = new Jxlint(new MyXmlLintRulesImpl(), new MyProgramSettings());
          jxlint.parseArgumentsAndDispatch(args);
      }
  }
  ```

4. Build your application.

Building the Code
-----------------

To build the code, run `gradle`. This will create a jxlint jar. If you do
not have gradle installed, type `gradlew`.

Examples
--------

Sample implementations can be found [here](src/test/java/com/selesse/jxlint/samplerules).

The following is the "--help" command line switch, which should give you an
idea of what you get "for free" if you use jxlint:

    usage: jxlint [flags] <directory>
     -h,--help                     Usage information, help message.
     -v,--version                  Output version information.
     -p,--profile                  Measure time every rule takes to complete.
     -l,--list                     Lists lint rules with a short, summary
                                   explanation.
     -r,--rules                    Prints a Markdown dump of the program's
                                   rules.
     -s,--show <RULE[s]>           Lists a verbose rule explanation.
     -c,--check <RULE[s]>          Only check for these rules.
     -d,--disable <RULE[s]>        Disable the list of rules.
     -e,--enable <RULE[s]>         Enable the list of rules.
     -y,--category <CATEGORY[s]>   Run all rules of a certain category.
     -w,--nowarn                   Only check for errors; ignore warnings.
     -Wall,--Wall                  Check all warnings, including those off by
                                   default.
     -Werror,--Werror              Treat all warnings as errors.
     -q,--quiet                    Don't output any progress or reports.
     -t,--html <filename>          Create an HTML report.
     -x,--xml <filename>           Create an XML (!!) report.

    <RULE[s]> should be comma separated, without spaces.
    Exit Status:
    0                     Success
    1                     Failed
    2                     Command line error

License
-------

This software is licensed under the [MIT License](http://en.wikipedia.org/wiki/MIT_License).
