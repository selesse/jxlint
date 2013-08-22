package com.selesse.jxlint;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.ExitType;
import org.junit.Test;

import java.util.List;

public class DispatcherTest extends AbstractTestCase {
    @Test
    public void testHelpProperlyExtracted() {
        final List<String> expectedOutput = Lists.newArrayList(
                "usage: jxlint [flags]"
               ," -h,--help                Usage information, help message."
               ," -v,--version             Output version information."
               ," -l,--list                Lists lint rules with a short, summary"
               ,"                          explanation."
               ," -s,--show <RULE[s]>      Lists a verbose rule explanation."
               ," -d,--disable <RULE[s]>   Disable the list of rules."
               ," -e,--enable <RULE[s]>    Enable the list of rules."
               ," -c,--check <RULE[s]>     Only check for these rules."
               ," -w,--nowarn              Only check for errors; ignore warnings."
               ," -Wall,--Wall             Check all warnings, including those all by"
               ,"                          default."
               ," -Werror,--Werror         Treat all warnings as errors."
               ," -q,--quiet               Don't output any progress or reports."
               ," -t,--html <arg>          Create an HTML report."
               ," -x,--xml <arg>           Create an XML (!!) report."
               ,""
               ,"<RULE[s]> should be comma separated, without spaces."
               ,"Exit Status:"
               ,"0                     Success"
               ,"1                     Failed"
               ,"2                     Command line error\n"
        );

        runExitTest(new String[]{"--help"}, null, Joiner.on("\n").join(expectedOutput), ExitType.SUCCESS);
    }

    @Test
    public void testVersionProperlyExtracted() {
        final String expectedOutput = String.format("%s: version %s", Main.getProgramName(), Main.getProgramVersion());

        runExitTest(new String[]{"--version"}, null, expectedOutput, ExitType.SUCCESS);
    }

    @Test
    public void testShowInvalidOptionSaysSo() {
        final String expectedOutput = "'foobarrule' is not a valid rule.";

        runExitTest(new String[] { "--show=foobarrule" }, null, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }

    @Test
    public void testShowInvalidOptionSaysSoVariation() {
        final String expectedOutput = "'foobarrule' is not a valid rule.";

        runExitTest(new String[] { "--show", "foobarrule" }, null, expectedOutput, ExitType.COMMAND_LINE_ERROR);
    }
}
