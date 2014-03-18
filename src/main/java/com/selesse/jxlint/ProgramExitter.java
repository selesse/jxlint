package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;

/**
 * Exits the program with provided {@link ExitType}s.
 */
public class ProgramExitter {
    private static String outputMessage;

    /**
     * Exits the program without displaying anything. Calls System.exit on {@link com.selesse.jxlint.model.ExitType}'s
     * error code.
     */
    public static void exitProgram(ExitType exitType) {
        ProgramExitter.outputMessage = "";
        System.exit(exitType.getErrorCode());
    }

    /**
     * Exits the program, displaying the message. Calls System.exit on {@link com.selesse.jxlint.model.ExitType}'s
     * error code.
     */
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
