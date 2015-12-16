/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This class contains a set of utility functions that were created with the aim to be used in the context
 * of a process's action scripts and conditions scripts. To make the life of the script programmer easier.
 * This class will be automatically imported by the platform when process script related classes are
 * generated. So the user don't have to take care of import this class.
 *
 * An example of use of this class in a process condition script can be something like this.
 *
 * return KieFunctions.isTrue(approved) &&
 *        !KieFunctions.equals(invoiceType, "external") &&
 *        KieFunctions.greaterThan(amount, "15000");
 *
 */
public class KieFunctions {

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean equalsTo(Number number, String value) {
        if (number == null) return value == null;

        if (value == null) throw new RuntimeException("Number: " + number + " can not be compared with a null value.");

        return compareTo(number, value) == 0;
    }

    public static boolean equalsTo(String string, String value) {
        if (string == null) return value == null;

        return string.equals(value);
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean contains(String string, String value) {
        if (string == null) return value == null;

        return value != null && string.contains(value);
    }

    public static boolean startsWith(String string, String value) {
        if (string == null) return value == null;

        return value != null && string.startsWith(value);
    }

    public static boolean endsWith(String string, String value) {
        if (string == null) return value == null;

        return value != null && string.endsWith(value);
    }

    public static boolean greaterThan(Number number, String value) {
        if (number == null) return false;

        if (value == null) throw new RuntimeException("Number: " + number + " can not be compared with a null value.");

        return compareTo(number, value) > 0;
    }

    public static boolean greaterOrEqualThan(Number number, String value) {
        if (number == null) return false;

        if (value == null) throw new RuntimeException("Number: " + number + " can not be compared with a null value.");

        return compareTo(number, value) >= 0;
    }

    public static boolean lessThan(Number number, String value) {
        if (number == null) return false;

        if (value == null) throw new RuntimeException("Number: " + number + " can not be compared with a null value.");

        return compareTo(number, value) < 0;
    }

    public static boolean lessOrEqualThan(Number number, String value) {
        if (number == null) return false;

        if (value == null) throw new RuntimeException("Number: " + number + " can not be compared with a null value.");

        return compareTo(number, value) <= 0;
    }

    public static boolean between(Number number, String minValue, String maxValue) {
        if (number == null) return false;

        if (minValue == null) throw new RuntimeException("Number: " + number + " can not be compared with minValue null.");

        if (maxValue == null) throw new RuntimeException("Number: " + number + " can not be compared with maxValue null.");

        return compareTo(number, minValue) >= 0 && compareTo(number, maxValue) <= 0;
    }

    public static boolean isTrue(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    public static boolean isFalse(Boolean value) {
        return Boolean.FALSE.equals(value);
    }

    private static int compareTo(Number number, String value) {

        if (number == null) throw new RuntimeException("Number parameter can not be null.");
        if (value == null) throw new RuntimeException("Number: " + number + " can not be compared with a null value.");

        if (number instanceof Short) {
            return new Short(number.shortValue()).compareTo(Short.valueOf(value));
        } else if (number instanceof Integer) {
            return new Integer(number.intValue()).compareTo(Integer.valueOf(value));
        } else if (number instanceof Long) {
            return new Long(number.longValue()).compareTo(Long.valueOf(value));
        } else if (number instanceof Float) {
            return new Float(number.floatValue()).compareTo(Float.valueOf(value));
        } else if (number instanceof Double) {
            return new Double(number.doubleValue()).compareTo(Double.valueOf(value));
        } else if (number instanceof BigDecimal) {
            return ((BigDecimal) number).compareTo((new BigDecimal(value)));
        } else if (number instanceof BigInteger) {
            return ((BigInteger) number).compareTo((new BigInteger(value)));
        }

        throw new RuntimeException("Unsupported type: " + number.getClass() + " was provided for parameter number: " + number);
    }
}