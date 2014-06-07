package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.settings.Profiler;

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
        exitProgramWithMessage("", exitType);
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
        if (Profiler.isEnabled()) {
            Profiler.setStopTime(System.currentTimeMillis());
            System.out.println();
            System.out.println(Profiler.getGeneratedProfileReport());
        }
        System.exit(exitType.getErrorCode());
    }

    public static String getOutputMessage() {
        return outputMessage;
    }
}
