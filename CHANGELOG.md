## jxlint 2.x.x

IMPROVEMENTS:

- Update to gradle 4.10.3

## jxlint 2.1.0 (July 19, 2019)

IMPROVEMENTS:

- New output format `jenkins-xml` that can be imported in Jenkins CI

## jxlint 2.0.0 (September 14, 2017)

BUG FIXES:

- avoid duplicate rule analysis with --enable

IMPROVEMENTS:

- Separate "core" from "command-line" logic
- Add "jxlint-maven" to create maven plugins
- Add `srcpath` option
- Ensure that rule names are unique
- Ensure that rule names do not contain a comma
- Update jetty version to 9.2.22.v20170606

## jxlint 1.7.2 (August 20, 2016)

BUG FIXES:

  - Fixed NullPointerException when producing an HTML report that had an
    Exception without a message.

## jxlint 1.7.1

IMPROVEMENTS:

  - "Verified rules" is now part of the "summary" tab and is now collapsible

## jxlint 1.7.0 (December 9, 2014)

IMPROVEMENTS:

  - HTML report now includes directory it validated and names of rules it
    validated with.

## jxlint 1.6.2 (November 12, 2014)

BUG FIXES:

  - Fix bug where web validator errors double in size on successive runs.
    The bug was due to lintRule.validate() doubling in size every time
    `validate()` was called

## jxlint 1.6.1 (November 3, 2014)

BUG FIXES:

  - Explicitly set UTF-8 encoding in HTML report
  - Fix iframe grossness: pressing enter now opens report in new page

## jxlint 1.6.0 (October 13, 2014)

BACKWARD INCOMPATIBILITIES:
  - Default port changed from 8080 to 8083
  - For a given rule, every file for the validation is now `try/catch`ed. The
    previous behavior was that one exception would cause Jxlint to "give up"
  - When specifying a report file, if the extension is not specified, it is
    now automatically appended

IMPROVEMENTS:
  - Default reporter now provides exception summary
  - Web mode now provides option for saving report

## jxlint 1.5.1 (September 21, 2014)

IMPROVEMENTS:
  - Major improvements to the generated HTML report
    1. Categories can be selected via tabs
    2. Columns in the summary report can be clicked to sort
    3. Any rule description Markdown is prettified via prettify.js
    4. There are now more than 0 lines of CSS ;)

BUG FIXES:
  - Fix summary report (the violation numbers did not add up)

## jxlint 1.5.0 (September 6, 2014)

IMPROVEMENTS:

  - Allow users to choose the web port for `--web`
  - Include name and version in page
  - Add SLF4J and logging
  - Add error summary to HTML report
  - Improve spacing after text box in page

BUG FIXES:

  - Fix centering in iFrame

## jxlint 1.4.0 (September 2, 2014)

IMPROVEMENTS:
  - Add `andSeverity` to `LintError` build constructor, allowing for the same
  `LintRule` to provide multiple different errors
  - Add experimental option "--web":
    1. When `--web` is selected, Jetty will start up on 8080 and open the
       user's web browser to a webpage
    2. The user is presented with a web page, displaying *all* rules, with a
       check box next to every rule. The user can choose which rules to run,
       and which directory to run on.
    3. When the user selects rules and a directory, then presses "enter", a
       report iframe is generated.

BUG FIXES:

  - Always sort bad option groups before displaying errors

## jxlint 1.3.0 (June 11, 2014)

IMPROVEMENTS:

  - Add profile option (`-p`, `--profile`)
  - Add special handling for unrecognized options
  - Multiple errors within the same file are now sorted first by the file name,
    then by their line numbers
  - Add ability to validate specific `Category`s (`-y`, `--category`)
    i.e. with `<program-name> --category "LINT, PERFORMANCE"`
  - Rules are now run in separate threads for increased performance

BUG FIXES:

  - Fix line numbers not being displayed in `DefaultReporter`

## jxlint 1.2.0 (June 5, 2014)

IMPROVEMENTS:

  - `jxlint` is now Java 1.6-compatible [GH-6]
  - Display line numbers in `DefaultReporter` (i.e. command line)
  - Add ability to create custom `Category`s [GH-7]

## jxlint 1.1.1 (May 27, 2014)

BUG FIXES:

  - Error messages in HTML reporters are now HTML-encoded.

## jxlint 1.1.0 (April 20, 2014)

IMPROVEMENTS:

  - Add support for Markdown rule documentation.
