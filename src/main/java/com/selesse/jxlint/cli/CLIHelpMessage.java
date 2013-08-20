package com.selesse.jxlint.cli;

import com.selesse.jxlint.Main;
import com.selesse.jxlint.model.ExitType;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CLIHelpMessage {
    public static String getMessage(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(new CLIOptionComparator());

        // redirect stdout to a temporary stream to capture HelpFormatter.printHelp()

        PrintStream previousOut = System.out;
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output));

            String exitStatusFooter = "Exit Status:\n";
            for (ExitType exitType : ExitType.values()) {
                exitStatusFooter += String.format("%-21d %-30s\n", exitType.getErrorCode(), exitType.getExplanation());
            }

            helpFormatter.printHelp(Main.programName + " [flags]", "", options, "\n" + exitStatusFooter.trim());

            return output.toString();
        } finally {
            System.setOut(previousOut);
        }
    }
}
