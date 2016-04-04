/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.business;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Sorts data like this: "data-1", "data-2", "data-3", "data-10", "data-20", "data-100", ...
 */
public class AlphaNumericStringComparator implements Comparator<String>, Serializable {

    @Override
    public int compare(String a, String b) {
        char[] aChars = a.toCharArray();
        char[] bChars = b.toCharArray();
        int aIndex = 0;
        int bIndex = 0;
        while (true) {
            if (aIndex >= aChars.length) {
                if (bIndex >= bChars.length) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (bIndex >= bChars.length) {
                return 1;
            }
            char aChar = aChars[aIndex];
            char bChar = bChars[bIndex];
            if (isDigit(aChar) && isDigit(bChar)) {
                int aIndexTo = aIndex + 1;
                while (aIndexTo < aChars.length && isDigit(aChars[aIndexTo])) {
                    aIndexTo++;
                }
                int bIndexTo = bIndex + 1;
                while (bIndexTo < bChars.length && isDigit(bChars[bIndexTo])) {
                    bIndexTo++;
                }
                int aNumber = Integer.parseInt(new String(aChars, aIndex, aIndexTo - aIndex));
                int bNumber = Integer.parseInt(new String(bChars, bIndex, bIndexTo - bIndex));
                if (aNumber < bNumber) {
                    return -1;
                } else if (aNumber > bNumber) {
                    return 1;
                }
                aIndex = aIndexTo;
                bIndex = bIndexTo;
            } else {
                if (aChar < bChar) {
                    return -1;
                } else if (aChar > bChar) {
                    return 1;
                }
                aIndex++;
                bIndex++;
            }
        }
    }

    private boolean isDigit(char aChar) {
        return aChar >= '0' && aChar <= '9';
    }

}
