package com.selesse.jxlint.cli;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.settings.ProgramSettings;
import org.apache.commons.cli.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@SuppressWarnings("AccessStaticViaInstance")
/**
 * Generator of {@link org.apache.commons.cli.Options} and its Comparator. These options
 * are jxlint's defaults, but can be extended.
 */
public class CommandLineOptions {
    /**
     * Populate the {@link Options} for our command line parser. This sets up all the option short names,
     * long names, and help messages. It can also specify whether the argument takes arguments
     * (i.e. "--html index.html"), and whether options are mutually exclusive.
     */
    public static Options generateJxlintOptions() {
        Options options = new Options();

        // The sequence in which these options are added represents the order in which we want them to be displayed.
        // If this order is modified, make sure to modify "CommandLineOptions.getOptionsOrder".
        options.addOption("h", "help", false, "Usage information, help message.");
        options.addOption("v", "version", false, "Output version information.");
        options.addOption("p", "profile", false, "Measure time every rule takes to complete.");
        options.addOption("l", "list", false, "Lists lint rules with a short, summary explanation.");
        options.addOption("b", "web", false, "Run in the background, as a website.");
        options.addOption(OptionBuilder.withLongOpt("web").
                withDescription("Run in the background, as a website. " +
                        "(default port: " + ProgramOptionExtractor.DEFAULT_PORT + ")").
                hasOptionalArg().
                withArgName("port").create('b')
        );
        options.addOption("r", "rules", false, "Prints a Markdown dump of the program's rules.");
        options.addOption(OptionBuilder.withLongOpt("show").
                withDescription("Lists a verbose rule explanation.").
                hasOptionalArg().
                withArgName("RULE[s]").create('s')
        );
        options.addOption(OptionBuilder.withLongOpt("check").
                withDescription("Only check for these rules.").
                hasArg().
                withArgName("RULE[s]").create('c')
        );
        options.addOption(OptionBuilder.withLongOpt("disable").
                withDescription("Disable the list of rules.").
                hasArg().
                withArgName("RULE[s]").create('d')
        );
        options.addOption(OptionBuilder.withLongOpt("enable").
                withDescription("Enable the list of rules.").
                hasArg().
                withArgName("RULE[s]").create('e')
        );
        options.addOption(OptionBuilder.withLongOpt("category").
                        withDescription("Run all rules of a certain category.").
                        hasArg().
                        withArgName("CATEGORY[s]").create('y')
        );
        options.addOption("w", "nowarn", false, "Only check for errors; ignore warnings.");
        options.addOption("Wall", "Wall", false, "Check all warnings, including those off by default.");
        options.addOption("Werror", "Werror", false, "Treat all warnings as errors.");

        OptionGroup outputOptionGroup = new OptionGroup();
        outputOptionGroup.addOption(OptionBuilder.withLongOpt("quiet").
                withDescription("Don't output any progress or reports.").
                create('q')
        );
        outputOptionGroup.addOption(OptionBuilder.withLongOpt("html").
                withDescription("Create an HTML report.").
                hasOptionalArg().
                withArgName("filename").
                create('t')
        );
        outputOptionGroup.addOption(OptionBuilder.withLongOpt("xml").
                withDescription("Create an XML (!!) report.").
                hasOptionalArg().
                withArgName("filename").
                create('x')
        );
        options.addOption(OptionBuilder.withLongOpt("srcpath").
                withDescription("Local or remote path to the source directory, "
                    + "if not set a relative path to the local file will be computed.").
                hasArg().
                withArgName("PATH-PREFIX").create("sp")
        );

        options.addOptionGroup(outputOptionGroup);

        return options;
    }

    /**
     * Return the command line's help message based on the {@link org.apache.commons.cli.Options} created in {@link
     * #generateJxlintOptions()}.
     */
    public static String getHelpMessage(ProgramSettings programSettings) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(optionsOrdering);

        StringBuilder exitStatusFooter = new StringBuilder("<RULE[s]> should be comma separated, without spaces.\n");
        exitStatusFooter.append("<PATH-PREFIX>:\n");
        exitStatusFooter.append("Links to the source code files will use this value as value as prefix.");
        exitStatusFooter.append(" Possible values:\n");
        exitStatusFooter.append(" - relative path: ../../my-project/\n");
        exitStatusFooter.append(" - absolute path: file:///C:/work/my-project/\n");
        exitStatusFooter.append(" - online path: https://github.com/selesse/jxlint/blob/master/jxlint-impl/\n");
        exitStatusFooter.append("\n");
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

            helpFormatter.printHelp(programSettings.getProgramName() + " [flags] <directory>", "",
                    generateJxlintOptions(), "\n" + exitStatusFooter.toString().trim());

            outputString = output.toString(Charsets.UTF_8.displayName());
        }
        catch (UnsupportedEncodingException e) {
            // nothing to do here, why wouldn't UTF-8 be available?
        }
        finally {
            System.setOut(previousOut);
        }

        return outputString;
    }

    /**
     * Our desired options order. This gives us control on how our help gets printed, via
     * {@link #optionsOrdering}.
     */
    private static Map<String, Integer> getOptionsOrder() {
        return ImmutableMap.<String, Integer>builder()
                .put("h", 1)
                .put("v", 2)
                .put("p", 3)
                .put("l", 4)
                .put("b", 5)
                .put("r", 6)
                .put("s", 7)
                .put("c", 8)
                .put("d", 9)
                .put("e", 10)
                .put("y", 11)
                .put("w", 12)
                .put("Wall", 13)
                .put("Werror", 14)
                .put("q", 15)
                .put("t", 16)
                .put("x", 17)
                .put("sp", 18)
                .build();
    }

    /**
     * Ordering that will return options in the order they appear in {@link #getOptionsOrder()} first.
     */
    private static final Ordering<Option> optionsOrdering = new Ordering<Option>() {
        @Override
        public int compare(Option o1, Option o2) {
            return getOptionsOrder().get(o1.getOpt()) - getOptionsOrder().get(o2.getOpt());
        }
    };
}
