package com.selesse.jxlint.utils;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.html.HtmlEscapers;

public class HtmlUtils {
    public static Iterable<String> surroundAndHtmlEscapeAll(Iterable<String> iterable,
                                                            final String before, final String after) {
        return Iterables.transform(iterable, new Function<String, String>() {

            @Override
            public String apply(String input) {
                if (input == null) {
                    input = "";
                }
                return before + htmlEncode(input) + after;
            }
        });
    }

    public static String htmlEncode(String string) {
        return HtmlEscapers.htmlEscaper().escape(string);
    }

}
