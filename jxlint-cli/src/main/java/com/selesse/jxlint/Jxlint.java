package com.selesse.jxlint;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.cli.CommandLineOptions;
import com.selesse.jxlint.cli.ProgramOptionExtractor;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import com.selesse.jxlint.model.rules.Categories;
import com.selesse.jxlint.model.rules.LintRules;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.settings.Profiler;
import com.selesse.jxlint.settings.ProgramSettings;
import org.apache.commons.cli.*;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Entry-point to the jxlint framework. This should be called only after customizing
 * {@link com.selesse.jxlint.report.Reporter}s (if desired). Pass your program's command line arguments to
 * {@link #parseArgumentsAndDispatch(String[])}}, jxlint will do the rest! Sample implementation is shown below:
 *
 * <pre>
 *     {@code
 *     public class MyApplication {
 *         public static void main (String[] args) {
 *             Jxlint jxlint = new Jxlint(new MyLintRules(), new MyProgramSettings());
 *             jxlint.parseArgumentsAndDispatch(args);
 *         }
 *     }
 *     }
 * </pre>
 */
public class Jxlint {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jxlint.class);

    private final ProgramSettings programSettings;

    /**
     * Initializes jxlint with a set of rules ({@link com.selesse.jxlint.model.rules.LintRules}
     * and program settings {@link com.selesse.jxlint.settings.ProgramSettings}).
     * This will make the program call {@link java.lang.System#exit(int)} under circumstances described in
     * {@link com.selesse.jxlint.model.ExitType}.
     */
    public Jxlint(LintRules lintRules, ProgramSettings programSettings) {
        this(lintRules, programSettings, true);
    }

    /**
     * Same as {@link #Jxlint(com.selesse.jxlint.model.rules.LintRules, com.selesse.jxlint.settings.ProgramSettings)},
     * but explicitly specify if we'll use {@link System#exit(int)} after reporting.
     */
    public Jxlint(LintRules lintRules, ProgramSettings programSettings, boolean exitAfterReporting) {
        this.programSettings = programSettings;

        LintRulesImpl.setInstance(lintRules);
        LintRulesImpl.setExitAfterReporting(exitAfterReporting);
        Profiler.setEnabled(false);
    }

    @SuppressWarnings("unused")
    public void setCustomCategories(Class<? extends Enum<?>> categories) {
        LOGGER.debug("Setting custom categories to {}", categories.getCanonicalName());
        Categories.setCategories(categories);
    }

    /**
     * Parses the command line arguments, and calls the {@link com.selesse.jxlint.Dispatcher} to decide what to do
     * next. Makes sure that there are no {@link java.lang.Exception}s thrown when parsing the command line arguments.
     *
     * @param args Your program's command line arguments.
     */
    public void parseArgumentsAndDispatch(String[] args) {
        Profiler.beginProgramProfiling();

        if (args.length == 0) {
            args = new String[] { "--help" };
        }

        // Turn the color on!
        AnsiConsole.systemInstall();

        CommandLineParser commandLineParser = new GnuParser();
        Options options = CommandLineOptions.generateJxlintOptions();

        try {
            CommandLine commandLine = commandLineParser.parse(options, args);
            ProgramOptions programOptions = ProgramOptionExtractor.extractProgramOptions(commandLine);
            LOGGER.debug("Extracted {} from command line options", programOptions);
            Dispatcher dispatcher = new Dispatcher(programOptions, programSettings);
            dispatcher.dispatch();
        }
        catch (MissingArgumentException e) {
            ProgramExitter.exitProgramWithMessage("Missing argument for option '--" + e.getOption().getLongOpt() + "'" +
                    ".", ExitType.COMMAND_LINE_ERROR);
        }
        catch (AlreadySelectedException e) {
            OptionGroup badOptionGroup = e.getOptionGroup();
            List<String> optionNames = Lists.newArrayList();

            for (Object option : badOptionGroup.getOptions()) {
                optionNames.add("--" + ((Option) option).getLongOpt());
            }

            Collections.sort(optionNames);

            ProgramExitter.exitProgramWithMessage("Only one of " + Joiner.on(", ").join(optionNames) +
                    " must be selected.", ExitType.COMMAND_LINE_ERROR);
        }
        catch (UnrecognizedOptionException e) {
            ProgramExitter.exitProgramWithMessage("Unrecognized option: " + e.getOption(), ExitType.COMMAND_LINE_ERROR);
        }
        catch (ParseException e) {
            LOGGER.error("Error parsing command line tokens", e);
            ProgramExitter.exitProgramWithMessage(CommandLineOptions.getHelpMessage(programSettings),
                    ExitType.COMMAND_LINE_ERROR);
        }

        // This is done both here, and in ProgramExitter#exitProgramWithMessage.
        // Why? Because we have two possible program flows: we exit with System.exit, or we don't.
        if (Profiler.isEnabled()) {
            Profiler.endProgramProfiling();
            Profiler.printProfileReport();
        }
    }
}
