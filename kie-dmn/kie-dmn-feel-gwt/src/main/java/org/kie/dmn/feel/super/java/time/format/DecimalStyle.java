/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.time.format;

public class DecimalStyle {

    public char getZeroDigit() {
        return ' ';
    }

    public DecimalStyle withZeroDigit(final char zeroDigit) {
        return null;
    }

    public char getPositiveSign() {
        return ' ';
    }

    public DecimalStyle withPositiveSign(final char positiveSign) {
        return null;
    }

    public char getNegativeSign() {
        return ' ';
    }

    public DecimalStyle withNegativeSign(final char negativeSign) {
        return null;
    }

    public char getDecimalSeparator() {
        return ' ';
    }

    public DecimalStyle withDecimalSeparator(final char decimalSeparator) {
        return null;
    }

    public boolean equals(final DecimalStyle obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }
}
