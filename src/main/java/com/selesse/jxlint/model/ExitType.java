package com.selesse.jxlint.model;

public enum ExitType {
    SUCCESS(0, "Success"),
    FAILED(1, "Failed"),
    COMMAND_LINE_ERROR(2, "Command line error");

    private final int errorCode;
    private final String printFriendlyMessage;

    private ExitType(int errorCode, String printFriendlyMessage) {
        this.errorCode = errorCode;
        this.printFriendlyMessage = printFriendlyMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getExplanation() {
        return printFriendlyMessage;
    }
}
