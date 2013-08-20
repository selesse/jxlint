package com.selesse.jxlint;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.cli.CLIHelpMessage;
import com.selesse.jxlint.cli.ProgramOptionExtractor;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.model.ProgramOptions;
import org.apache.commons.cli.*;

import java.util.List;

public class Main {
    private static final String optionsOrder = "hvlsdecwWallWerrorqtx";
    private static String outputMessage;
    private static final String programVersion = "1.0.0-SNAPSHOT"; // sync with build.gradle
    public static final String programName = "jxlint";

    public static void main(String[] args) {
        new Main().run(args);
    }

    public void run(String[] args) {
        CommandLineParser commandLineParser = new GnuParser();
        Options options = new Options();

        // the sequence in which these options are added represents the order in which we want them to be displayed.
        // if this order is modified, make sure to modify "Main:optionsOrder"
        options.addOption("h", "help", false, "Usage information, help message.");
        options.addOption("v", "version", false, "Output version information.");
        options.addOption("l", "list", false, "Lists lint rules with a short, summary explanation.");
        options.addOption("s", "show", true, "Lists a verbose rule explanation.");

        options.addOption("d", "disable", true, "Disable the list of rules.");
        options.addOption("e", "enable", true, "Enable the list of rules.");
        options.addOption("c", "check", true, "Only check for these rules.");
        options.addOption("w", "nowarn", false, "Only check for errors; ignore warnings.");
        options.addOption("Wall", "Wall", false, "Check all warnings, including those all by default.");
        options.addOption("Werror", "Werror", false, "Treat all warnings as errors.");

        OptionGroup outputOptionGroup = new OptionGroup();
        outputOptionGroup.addOption(new Option("q", "quiet", false, "Don't output any progress or reports."));
        outputOptionGroup.addOption(new Option("t", "html", true, "Create an HTML report."));
        outputOptionGroup.addOption(new Option("x", "xml", true, "Create an XML (!!) report."));

        options.addOptionGroup(outputOptionGroup);

        try {
            CommandLine commandLine = commandLineParser.parse(options, args);
            ProgramOptions programOptions = ProgramOptionExtractor.extractProgramOptions(commandLine, options);
            Dispatcher.dispatch(programOptions);
        }
        catch (AlreadySelectedException e) {
            OptionGroup badOptionGroup = e.getOptionGroup();
            List<String> optionNames = Lists.newArrayList();

            for (Object option : badOptionGroup.getOptions()) {
                optionNames.add("--" + ((Option) option).getLongOpt());
            }
            exitProgram("Only one of " + Joiner.on(", ").join(optionNames) + " must be selected.", ExitType.COMMAND_LINE_ERROR);
        }
        catch (ParseException e) {
            exitProgram(CLIHelpMessage.getMessage(options), ExitType.COMMAND_LINE_ERROR);
        }
    }

    public static void exitProgram(String outputMessage, ExitType exitType) {
        Main.outputMessage = outputMessage;
        System.out.println(outputMessage);
        System.exit(exitType.getErrorCode());
    }

    public static String getOptionsOrder() {
        return optionsOrder;
    }

    public static String getOutputMessage() {
        return outputMessage;
    }

    public static String getProgramVersion() {
        return programVersion;
    }
}
