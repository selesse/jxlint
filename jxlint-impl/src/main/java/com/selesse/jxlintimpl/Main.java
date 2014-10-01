package com.selesse.jxlintimpl;

import com.selesse.jxlint.Jxlint;
import com.selesse.jxlintimpl.rules.JxlintImplRules;
import com.selesse.jxlintimpl.settings.JxlintImplProgramSettings;

public class Main {
    public static void main(String[] args) {
        Jxlint jxlint = new Jxlint(new JxlintImplRules(), new JxlintImplProgramSettings());
        jxlint.parseArgumentsAndDispatch(args);
    }
}