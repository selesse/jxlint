package com.selesse.jxlint.report;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.settings.ProgramSettings;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Manager of {@link com.selesse.jxlint.report.Reporter}s.
 *
 * <p>
 *     Keeps a mapping of {@link com.selesse.jxlint.model.OutputType} and {@link com.selesse.jxlint.report.Reporter}
 *     classes. Can be configured to overwrite reporters, or queried to create a reporter.
 * </p>
 *
 * <p>
 *     To overwrite a reporter, extend a Reporter, implement the appropriate methods, and call this function:
 *
 *     <pre>{@code
 *     Reporters.setCustomReporter(OutputType.HTML, MyPrettyHtmlReporter.class);
 *     }</pre>
 *
 *     This should be done before calling {@link com.selesse.jxlint.Jxlint#parseArgumentsAndDispatch(String[])}.
 * </p>
 *
 *
 */
public class Reporters {
    private static Map<OutputType, Class<? extends Reporter>> outputTypeReporterMap = Maps.newHashMap();
    static {
        outputTypeReporterMap.put(OutputType.DEFAULT, DefaultReporter.class);
        outputTypeReporterMap.put(OutputType.HTML, SequentialHtmlReporter.class);
        outputTypeReporterMap.put(OutputType.XML, XmlReporter.class);
        outputTypeReporterMap.put(OutputType.QUIET, DefaultReporter.class);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Add a custom {@link com.selesse.jxlint.report.Reporter} for a particular
     * {@link com.selesse.jxlint.model.OutputType}. It will be instantiated using reflection.
     */
    public static void setCustomReporter(OutputType outputType, Class<? extends Reporter> reporter) {
        outputTypeReporterMap.put(outputType, reporter);
    }

    /**
     * Creates the appropriate {@link com.selesse.jxlint.report.Reporter} given the OutputType.
     */
    public static Reporter createReporter(List<LintError> lintErrors, ProgramSettings settings, OutputType outputType,
                                          String outputPath) throws UnableToCreateReportException {
        Reporter reporter = new DefaultReporter(System.out, settings, lintErrors);

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
                    Constructor<?> reporterTypeConstructor = reporterType.getConstructor(PrintStream.class,
                            ProgramSettings.class, List.class);
                    return (Reporter) reporterTypeConstructor.newInstance(out, settings, lintErrors);
                case DEFAULT:
                case QUIET:
                    return new DefaultReporter(out, settings, lintErrors);
            }
        }
        // If there any any exceptions, we default to System.out
          catch (InvocationTargetException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (InstantiationException ignored) {
        } catch (IllegalAccessException ignored) {
        }

        return reporter;
    }
}
