package com.selesse.jxlint.report;

import java.io.File;
import java.io.FileNotFoundException;

public class UnableToCreateReportException extends FileNotFoundException {
    public UnableToCreateReportException(File file) {
        super("Unable to create the report file for '" + file.getAbsolutePath() + "'.");
    }
}
