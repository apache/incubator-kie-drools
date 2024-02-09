/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.operators;

import org.drools.model.functions.Operator;

public enum SoundsLikeOperator implements Operator.SingleValue<String, String> {

    INSTANCE;

    public static final char[] MAP = new char[]{'0', '1', '2', '3', '0', '1', '2', '0', '0', '2', '2', '4', '5', '5', '0', '1', '2', '6', '2', '3', '0', '1', '0', '2', '0', '2'};

    @Override
    public boolean eval( String s1, String s2 ) {
        final String soundex1;
        final String soundex2;

        if (s1 == null || s2 == null) {
            return false;
        }

        soundex1 = soundex(s1);
        soundex2 = soundex(s2);

        if (soundex1 == null) {
            return false;
        }

        return soundex1.equals(soundex2);
    }

    @Override
    public String getOperatorName() {
        return "soundslike";
    }

    public static String soundex(String s) {
        char[] ca = s.toUpperCase().toCharArray();
        StringBuilder res = new StringBuilder();
        char prev = '?';

        char c;
        int i;
        for(i = 0; i < ca.length && res.length() < 4 && (c = ca[i]) != ','; ++i) {
            if (c >= 'A' && c <= 'Z' && c != prev) {
                prev = c;
                char m = MAP[c - 65];
                if (m != '0') {
                    res.append(m);
                }
            }
        }

        if (res.length() == 0) {
            return null;
        } else {
            for(i = res.length(); i < 4; ++i) {
                res.append('0');
            }

            return res.toString();
        }
    }
}
