package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;

public class ProgramExitter {
    private static String outputMessage;


    public static void exitProgram(ExitType exitType) {
        ProgramExitter.outputMessage = "";
        System.exit(exitType.getErrorCode());
    }

    public static void exitProgramWithMessage(String outputMessage, ExitType exitType) {
        ProgramExitter.outputMessage = outputMessage;
        if (outputMessage.trim().length() > 0) {
            System.out.println(outputMessage);
        }
        System.exit(exitType.getErrorCode());
    }

    public static String getOutputMessage() {
        return outputMessage;
    }
}
