package com.selesse.jxlint.model;

import java.util.HashMap;
import java.util.Map;

public class ProgramOptions {
    private Map<String, String> options;
    private String helpMessage;

    public ProgramOptions() {
        this.options = new HashMap<String, String>();
    }

    /**
     * Used for options that don't have any associated information (i.e. option == true).
     */
    public void addOption(String optionName) {
        options.put(optionName, "true");
    }

    public void addOption(String optionName, String value) {
        options.put(optionName, value);
    }

    public boolean hasOption(String optionName) {
        return options.containsKey(optionName);
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    public String getOption(String show) {
        return options.get(show);
    }
}
