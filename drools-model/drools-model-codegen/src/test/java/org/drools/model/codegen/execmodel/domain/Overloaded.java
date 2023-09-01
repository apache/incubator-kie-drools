package org.drools.model.codegen.execmodel.domain;

import java.io.Serializable;

public class Overloaded implements Serializable {

    private static final long serialVersionUID = 1L;

    public int method(int i, int j, String s) {
        return i + j + s.length();
    }

    public int method(int i, String s, int j) {
        return i + s.length() - j;
    }

    public int method(String s, int i, int j) {
        return s.length() - i - j;
    }

    public String method2(String str1, String str2, long l, double d) {
        String str = str1 + str2 + l + d;
        return str;
    }

    public String method2(long l, String str1, double d, String str2) {
        String str = String.valueOf(l) + str1 + d + str2;
        return str;
    }
}
