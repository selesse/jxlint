package com.selesse.jxlint.report;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * An {@link java.lang.Exception} thrown when a report (created by a {@link com.selesse.jxlint.report.Reporter}) cannot
 * be created.
 */
public class UnableToCreateReportException extends FileNotFoundException {
    public UnableToCreateReportException(File file) {
        super("Unable to create the report file for '" + file.getAbsolutePath() + "'.");
    }
}
