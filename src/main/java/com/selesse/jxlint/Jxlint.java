package com.selesse.jxlint;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.cli.CommandLineOptions;
import com.selesse.jxlint.cli.ProgramOptionExtractor;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import org.apache.commons.cli.*;
import org.fusesource.jansi.AnsiConsole;

import java.util.List;

/**
 * Entry-point to the application.
 */
public class Jxlint {
    public static void doLintAnalysis(String[] args) {
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
            Dispatcher.dispatch(programOptions);
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
            ProgramExitter.exitProgramWithMessage("Only one of " + Joiner.on(", ").join(optionNames) +
                    " must be selected.", ExitType.COMMAND_LINE_ERROR);
        }
        catch (ParseException e) {
            System.out.println(e);
            ProgramExitter.exitProgramWithMessage(CommandLineOptions.getHelpMessage(), ExitType.COMMAND_LINE_ERROR);
        }
    }
}
