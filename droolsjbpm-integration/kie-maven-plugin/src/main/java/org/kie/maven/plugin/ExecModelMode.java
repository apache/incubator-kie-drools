package org.kie.maven.plugin;

import java.util.Collections;

import static java.util.Arrays.asList;

public enum ExecModelMode {

    YES,
    NO,
    WITHDRL;

    public static boolean shouldGenerateModel(String s) {
        return asList(YES, WITHDRL).contains(valueOf(s.toUpperCase()));
    }

    public static boolean shouldDeleteFile(String s) {
        return Collections.singletonList(YES).contains(valueOf(s.toUpperCase()));
    }
}

