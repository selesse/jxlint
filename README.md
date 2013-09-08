jxlint
======

jxlint is a Java framework for performing static analysis. Its name is derived
from [lint](http://en.wikipedia.org/wiki/Lint_(software\)) tools and is
partially inspired by [Android's lint
tool](http://developer.android.com/tools/help/lint.html). It was originally
meant to stand for "Java XML lint", but it's more accurate to say it's Java
Lint.

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

Installation
------------

1. Clone this repository.
2. Make customizations (TODO: specify what this will be).
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

License
-------

This software is licensed under the [MIT License](http://en.wikipedia.org/wiki/MIT_License).
