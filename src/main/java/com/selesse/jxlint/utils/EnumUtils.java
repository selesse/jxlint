package com.selesse.jxlint.utils;

import com.google.common.base.CaseFormat;

public class EnumUtils {
    /**
     * Returns a happy version of toString. The default toString of an {@link Enum} yells at you, i.e. "RED", "BLUE",
     * "DARK_GREEN". toHappyString is much happier: it soothingly returns "Red", "Blue", "DarkGreen".
     *
     * <p> (In other words, this converts an upper underscore string to an upper camel-case string.) </p>
     */
    public static String toHappyString(Enum enumeration) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, enumeration.toString());
    }
}
