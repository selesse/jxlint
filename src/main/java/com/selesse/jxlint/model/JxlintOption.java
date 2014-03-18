package com.selesse.jxlint.model;

public enum JxlintOption {
    OUTPUT_TYPE("outputType"),
    SHOW("show"),
    HELP("help"),
    VERSION("version"),
    LIST("list"),
    WARNINGS_ARE_ERRORS("Werror"),
    CHECK("check"),
    ALL_WARNINGS("Wall"),
    NO_WARNINGS("nowarn"),
    DISABLE("disable"),
    ENABLE("enable"),
    OUTPUT_TYPE_PATH("outputTypePath"),
    REPORT_RULES("rules");

    private String optionString;

    JxlintOption(String optionString) {
        this.optionString = optionString;
    }

    public String getOptionString() {
        return optionString;
    }
}
