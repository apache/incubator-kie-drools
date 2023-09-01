package org.drools.compiler.integrationtests.concurrency;

public class StaticUtils {

    public static String TOSTRING(String s) {
        return s == null ? "null" : s;
    }

    public static String TOSTRING(Object o) {
        return o == null ? "null" : o.toString();
    }
}
