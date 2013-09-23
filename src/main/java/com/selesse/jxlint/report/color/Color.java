package com.selesse.jxlint.report.color;

@SuppressWarnings("unused")
public enum Color {
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m");

    private String color;

    private Color(String s) {
        this.color = s;
    }

    private String toAnsi() {
        return this.color;
    }

    /**
     * Return the ANSI color representation for a given {@link Color}.
     * This is intended to be clever, meaning that it will only print color when stdout is a terminal that supports
     * ANSI sequences (cygwin, xterm-256color are target audiences). This also means that if the output is being
     * redirected, don't print colors.
     */
    public static String getTermColor(Color color) {
        // if the terminal is null (i.e cmd.exe in Windows, or being run from a GUI), don't print color
        // if the System.console() is null, that means we're getting our output redirected so don't print colors
        if (System.getenv("TERM") == null || System.console() == null) {
            return "";
        }
        return color.toAnsi();
    }
    public static String wrapColor(String s, Color color) {
        return getTermColor(color) + s + getTermColor(Color.RESET);
    }
}
