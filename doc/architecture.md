# Architecture Overview

jxlint aims to be pretty simple, both in terms of usability and
architecture.

The only required integration for your application is the `Jxlint` class. By
creating an instance of it, and calling `parseArgumentsAndDispatch`, the logic
for executing rules is handled.

What is that logic? First, we parse the command line arguments. If there are
any fatal errors, we abort and display an informative error message.
Otherwise, we build a `ProgramOptions` instance based on the command line
parameters. This instance is passed to the `Dispatcher`, which is responsible
for setting up and choosing the appropriate action based on the program
options.

Assuming jxlint was asked to validate rules, it will then perform all the
validations, each rule validating in its own thread. A list of errors is
collected and passed to a `Reporter` class, which is responsible for reporting
rule violations. The kind of reporter (i.e. HTML, XML, or CLI) created is
based on the program options that were passed, with a default value of CLI.
Every `Reporter` can be overridden by calling `Reporters.setCustomReporter`
before calling `Jxlint#parseArgumentsAndDispatch`.

The packages are summarized below:

* `com.selesse.jxlint.actions` contains classes that perform actions that the
  `Dispatcher` calls on. An example might be printing out all the rules, or
  printing out the Markdown rule dump.

* `com.selesse.jxlint.cli` contains logic for parsing / handling the command
  line parameters passed to jxlint.

* `com.selesse.jxlint.linter` contains the logic for calling the functions to
   perform the validations.

* `com.selesse.jxlint.model` contains model information. Everything in here is
  mostly plain old Java objects. The important classes here are `LintRule` and
  `LintError`, the core of jxlint.

* `com.selesse.jxlint.report` contains classes related to reporting.

* `com.selesse.jxlint.settings` contains "settings" information, like the
  program name and the program version.
