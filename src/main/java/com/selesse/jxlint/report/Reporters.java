package com.selesse.jxlint.report;

import com.google.common.base.Charsets;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.rules.LintError;

import java.io.*;
import java.util.List;

public class Reporters {
    public static Reporter createReporter(List<LintError> lintErrors, OutputType outputType,
                                          String outputPath) throws UnableToCreateReportException {
        Reporter reporter = new DefaultReporter(System.out, lintErrors);

        if (outputType == null) {
            return reporter;
        }

        PrintStream out = System.out;

        if (outputType == OutputType.QUIET) {
            try {
                out = new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        // my own little /dev/null
                    }
                }, false, Charsets.UTF_8.displayName());
            } catch (UnsupportedEncodingException e) {
                // nothing to do here, why wouldn't UTF-8 be available?
            }
        }
        else {
            if (outputPath != null) {
                try {
                    out = new PrintStream(new FileOutputStream(outputPath), true, Charsets.UTF_8.displayName());
                } catch (FileNotFoundException e) {
                    throw new UnableToCreateReportException(new File(outputPath));
                } catch (UnsupportedEncodingException e) {
                    // nothing to do here, why wouldn't UTF-8 be available?
                }
            }
        }

        switch (outputType) {
            case QUIET:
                return new DefaultReporter(out, lintErrors);
            case HTML:
                return new HtmlReporter(out, lintErrors);
            case XML:
                return new XmlReporter(out, lintErrors);
        }

        return reporter;
    }
}
