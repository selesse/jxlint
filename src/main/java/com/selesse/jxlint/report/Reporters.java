package com.selesse.jxlint.report;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.rules.LintError;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class Reporters {
    private static Map<OutputType, Class<? extends Reporter>> outputTypeReporterMap = Maps.newHashMap();
    static {
        outputTypeReporterMap.put(OutputType.DEFAULT, DefaultReporter.class);
        outputTypeReporterMap.put(OutputType.HTML, HtmlReporter.class);
        outputTypeReporterMap.put(OutputType.XML, XmlReporter.class);
        outputTypeReporterMap.put(OutputType.QUIET, DefaultReporter.class);
    }

    public static void setCustomReporter(OutputType outputType, Class<? extends Reporter> reporter) {
        outputTypeReporterMap.put(outputType, reporter);
    }

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

        try {
            switch (outputType) {
                case XML:
                case HTML:
                    Class<? extends Reporter> reporterType = outputTypeReporterMap.get(outputType);
                    Constructor<?> reporterTypeConstructor = reporterType.getConstructor(PrintStream.class, List.class);
                    return (Reporter) reporterTypeConstructor.newInstance(out, lintErrors);
                case DEFAULT:
                case QUIET:
                    return new DefaultReporter(out, lintErrors);
            }
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            // We failed some part of reflection... It's okay, though, since we initialized the reporter to System.out
        }

        return reporter;
    }
}
