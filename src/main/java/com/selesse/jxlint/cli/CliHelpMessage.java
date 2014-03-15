package com.selesse.jxlint.cli;

import com.google.common.base.Charsets;
import com.selesse.jxlint.Main;
import com.selesse.jxlint.model.ExitType;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class CliHelpMessage {
    public static String getMessage(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(new CliOptionComparator());

        StringBuilder exitStatusFooter = new StringBuilder("<RULE[s]> should be comma separated, without spaces.\n");
        exitStatusFooter.append("Exit Status:\n");
        for (ExitType exitType : ExitType.values()) {
            exitStatusFooter.append(String.format("%-21d %-30s%n", exitType.getErrorCode(), exitType.getExplanation()));
        }

        String outputString = "";

        // redirect stdout to a temporary stream to capture HelpFormatter.printHelp()

        PrintStream previousOut = System.out;
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output, true, Charsets.UTF_8.displayName()));

            helpFormatter.printHelp(Main.getProgramName() + " [flags] <directory>", "", options, "\n" +
                    exitStatusFooter.toString().trim());

            outputString = output.toString(Charsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            // nothing to do here, why wouldn't UTF-8 be available?
        } finally {
            System.setOut(previousOut);
        }

        return outputString;
    }
}
