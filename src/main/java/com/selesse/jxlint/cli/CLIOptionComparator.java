package com.selesse.jxlint.cli;

import com.selesse.jxlint.Main;
import org.apache.commons.cli.Option;

import java.util.Comparator;

/**
 * Comparator that takes {@link Main}'s {@code getOptionsOrder()} to specify an order for the {@link Option}s.
 * This allows for a logical ordering between related flags.
 */
public class CLIOptionComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        Option option1 = (Option) o1;
        Option option2 = (Option) o2;

        return Main.getOptionsOrder().indexOf(option1.getOpt()) - Main.getOptionsOrder().indexOf(option2.getOpt());
    }
}
