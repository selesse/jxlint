jxlint
======

jxlint is a Java framework for performing static analysis. Its name is derived
from [lint](http://en.wikipedia.org/wiki/Lint_(software\)) tools and is
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

Quick Start
-----------

1. Clone this repository.
2. Make customizations:

Create all your rules. It's recommended to put all the rules in one directory,
as can be seen in the [sample implementations](src/test/java/com/selesse/jxlint/samplerules).
Each rule should also have a corresponding test.

    public class MustHaveAuthorTag extends LintRule {
        public MustHaveAuthorTag() {
            super("Author tag required", "Every file must have an @author tag.",
                    "Every file in this project requires an \"@author\" tag.",
                    Severity.WARNING, Category.DEFAULT);
        }

        @Override
        public List<File> getFilesToValidate() {
            return FileUtils.allFilesIn(getSourceDirectory());
        }

        @Override
        public boolean applyRule(File file) {
            try {
                List<String> fileContents = Files.readAllLines(file.toPath(), Charset.defaultCharset());
                for (String line : fileContents) {
                    if (line.contains("<?xml")) {
                        if (line.contains("version=\"")) {
                            return true;
                        }
                    }
                }
                failedRules.add(new LintError("No version specified"));
            } catch (IOException e) {
                failedRules.add(new LintError("Error reading file"));
            }

            return false;
      }

Set up the container by adding all of the rules to it.

    public class MyLintRuleImplementation extends AbstractLintRules {
        @Override
        public void initializeLintRules() {
            lintRules.add(new ValidXmlRule());
            lintRules.add(new UniqueAttributeRule());
            lintRules.add(new XmlVersionRule());
        }
    }

In `com.selesse.jxlint.Main`, set the `LintRules` singleton:

    public class Main {
        ...

        public static void main(String[] args) {
            LintRulesImpl.setInstance(new MyLintRuleImplementation());
            new Main().run(args);
        }

    }

3. Type `gradle`.

Requirements
------------

[Gradle](http://gradle.org) is required to build the code. jxlint uses the
following libraries:

  * [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)
  * [Guava](https://code.google.com/p/guava-libraries/)

For running tests, jxlint uses the following libraries:

  * [junit](http://junit.org/)
  * [System Rules](http://www.stefan-birkner.de/system-rules/)

Building the Code
-----------------

To build the code, run `gradle`. This will create a jxlint jar. (TODO: create
a portable, general bash script that runs the jar).

Examples
--------

Sample implementations can be found [here](src/test/java/com/selesse/jxlint/samplerules).

License
-------

This software is licensed under the [MIT License](http://en.wikipedia.org/wiki/MIT_License).
