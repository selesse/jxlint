package com.selesse.jxlint.model;

public class ProgramOptions {
    private boolean helpEnabled;
    private boolean versionEnabled;
    private String helpMessage;

    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    public void setHelpEnabled(boolean helpEnabled) {
        this.helpEnabled = helpEnabled;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    public boolean isVersionEnabled() {
        return versionEnabled;
    }

    public void setVersionEnabled(boolean versionEnabled) {
        this.versionEnabled = versionEnabled;
    }
}
