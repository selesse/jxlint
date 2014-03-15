package com.selesse.jxlint.cli;

import com.selesse.jxlint.Main;
import org.apache.commons.cli.Option;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator that uses {@link com.selesse.jxlint.Main#getOptionsOrder()} to specify an order for the {@link Option}s.
 * This allows for a logical ordering between related flags.
 */
public class CliOptionComparator implements Comparator<Option>, Serializable {
    @Override
    public int compare(Option o1, Option o2) {
        return Main.getOptionsOrder().indexOf(o1.getOpt()) - Main.getOptionsOrder().indexOf(o2.getOpt());
    }
}
