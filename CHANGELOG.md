## Unreleased - jxlint 1.3.0

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
