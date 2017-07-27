package com.selesse.jxlint.model;

public class ExitException extends Exception {
    private static final long serialVersionUID = 1L;

    private ExitType exitType;

    public ExitException(String message, ExitType exitType) {
        super(message);
        this.exitType = exitType;
    }

    public ExitType getExitType() {
        return exitType;
    }
}
