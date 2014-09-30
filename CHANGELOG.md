## jxlint x.x.x (unreleased)

IMPROVEMENTS:
  - Default reporter now provides exception summary

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
