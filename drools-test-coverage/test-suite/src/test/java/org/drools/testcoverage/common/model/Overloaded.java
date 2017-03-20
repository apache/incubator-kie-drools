/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.common.model;

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
