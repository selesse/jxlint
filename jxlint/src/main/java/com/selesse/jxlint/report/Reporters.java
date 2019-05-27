package com.selesse.jxlint.report;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.selesse.jxlint.model.JxlintOption;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.settings.ProgramSettings;
import com.selesse.jxlint.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Reporters.class);

    private static Map<OutputType, Class<? extends Reporter>> outputTypeReporterMap = Maps.newHashMap();
    static {
        outputTypeReporterMap.put(OutputType.DEFAULT, DefaultReporter.class);
        outputTypeReporterMap.put(OutputType.HTML, HtmlTemplatedReporter.class);
        outputTypeReporterMap.put(OutputType.XML, XmlReporter.class);
        outputTypeReporterMap.put(OutputType.JENKINS_XML, JenkinsXmlReporter.class);
        outputTypeReporterMap.put(OutputType.QUIET, DefaultReporter.class);
    }

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"})
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
    public static Reporter createReporter(List<LintError> lintErrors, ProgramSettings settings,
                                          ProgramOptions options) throws UnableToCreateReportException {
        Reporter reporter = new DefaultReporter(System.out, settings, options, lintErrors);
        OutputType outputType = options.getOutputType();
        String outputPath = options.getOption(JxlintOption.OUTPUT_TYPE_PATH);

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
            }
            catch (UnsupportedEncodingException e) {
                // nothing to do here, why wouldn't UTF-8 be available?
            }
        }
        else {
            if (outputPath != null) {
                try {
                    outputPath = FileUtils.normalizeFile(new File(outputPath)).getAbsolutePath();
                    createParentDirectoryIfNecessary(outputPath);
                    out = new PrintStream(new FileOutputStream(outputPath), true, Charsets.UTF_8.displayName());
                }
                catch (FileNotFoundException e) {
                    LOGGER.error("Could not create file output stream", e);
                    throw new UnableToCreateReportException(new File(outputPath));
                }
                catch (UnsupportedEncodingException e) {
                    // nothing to do here, why wouldn't UTF-8 be available?
                }
            }
        }

        try {
            switch (outputType) {
                case XML:
                case JENKINS_XML:
                case HTML:
                    Class<? extends Reporter> reporterType = outputTypeReporterMap.get(outputType);
                    Constructor<?> reporterTypeConstructor = reporterType.getConstructor(PrintStream.class,
                            ProgramSettings.class, ProgramOptions.class, List.class);
                    return (Reporter) reporterTypeConstructor.newInstance(out, settings, options, lintErrors);
                case DEFAULT:
                case QUIET:
                    return new DefaultReporter(out, settings, options, lintErrors);
            }
        }
        // If there any any exceptions, we default to System.out
        catch (Exception e) {
            LOGGER.error("Error dynamically creating reporter", e);
        }

        return reporter;
    }

    private static void createParentDirectoryIfNecessary(String outputPath) throws UnableToCreateReportException {
        File outputDirectory = new File(outputPath).getParentFile();
        if (outputDirectory == null) {
            throw new UnableToCreateReportException(new File(outputPath));
        }
        if (!outputDirectory.isDirectory()) {
            boolean directoriesCreated = outputDirectory.mkdirs();
            if (!directoriesCreated) {
                LOGGER.error("Unable to create parent directories for {}", outputPath);
                throw new UnableToCreateReportException(new File(outputPath));
            }
        }
    }

    @VisibleForTesting
    static Class<? extends Reporter> getReporter(OutputType outputType) {
        return outputTypeReporterMap.get(outputType);
    }
}
