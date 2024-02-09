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
package org.drools.modelcompiler.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

import org.drools.base.time.TimeUtils;
import org.drools.util.CoercionUtil;
import org.drools.model.BitMask;
import org.drools.model.bitmask.AllSetBitMask;
import org.drools.model.bitmask.AllSetButLastBitMask;
import org.drools.model.bitmask.EmptyBitMask;
import org.drools.model.bitmask.EmptyButLastBitMask;
import org.drools.model.bitmask.LongBitMask;
import org.drools.model.bitmask.OpenBitSet;
import org.drools.util.DateUtils;

public class EvaluationUtil {

    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtils.getDateFormatMask(), Locale.ENGLISH);

    public static boolean areNullSafeEquals(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    public static boolean areNumbersNullSafeEquals(Number n1, Number n2) {
        return n1 != null ? n2 != null && n1.doubleValue() == n2.doubleValue() : n2 == null;
    }

    public static boolean equals(Comparable c1, Comparable c2) {
        return c1 != null ? c2 != null && c1.compareTo(c2) == 0 : c2 == null;
    }

    public static boolean notEquals(Comparable c1, Comparable c2) {
        return !equals(c1, c2);
    }

    public static boolean greaterThan(Comparable c1, Comparable c2) {
        return c1 != null && c2 != null && c1.compareTo( c2 ) > 0;
    }

    public static boolean lessThan(Comparable c1, Comparable c2) {
        return c1 != null && c2 != null && c1.compareTo( c2 ) < 0;
    }

    public static boolean greaterOrEqual(Comparable c1, Comparable c2) {
        return c1 != null && c2 != null && c1.compareTo( c2 ) >= 0;
    }

    public static boolean lessOrEqual(Comparable c1, Comparable c2) {
        return c1 != null && c2 != null && c1.compareTo( c2 ) <= 0;
    }

    // We accept Object for compatibility with standard-drl mvel behavior. See DROOLS-5924
    public static boolean greaterThan(Object o1, Comparable c2) {
        if (!(o1 instanceof Comparable)) {
            return false; // compatibility with standard-drl: MathProcessor.doOperationNonNumeric() returns false when the left operand is not Comparable
        }
        Comparable c1 = (Comparable)o1;
        return coerceAndEvaluate(c1, c2, EvaluationUtil::greaterThanNumbers, EvaluationUtil::greaterThan);
    }

    public static boolean lessThan(Object o1, Comparable c2) {
        if (!(o1 instanceof Comparable)) {
            return false; // compatibility with standard-drl: MathProcessor.doOperationNonNumeric() returns false when the left operand is not Comparable
        }
        Comparable c1 = (Comparable)o1;
        return coerceAndEvaluate(c1, c2, EvaluationUtil::lessThanNumbers, EvaluationUtil::lessThan);
    }

    public static boolean greaterOrEqual(Object o1, Comparable c2) {
        if (!(o1 instanceof Comparable)) {
            return false; // compatibility with standard-drl: MathProcessor.doOperationNonNumeric() returns false when the left operand is not Comparable
        }
        Comparable c1 = (Comparable)o1;
        return coerceAndEvaluate(c1, c2, EvaluationUtil::greaterOrEqualNumbers, EvaluationUtil::greaterOrEqual);
    }

    public static boolean lessOrEqual(Object o1, Comparable c2) {
        if (!(o1 instanceof Comparable)) {
            return false; // compatibility with standard-drl: MathProcessor.doOperationNonNumeric() returns false when the left operand is not Comparable
        }
        Comparable c1 = (Comparable)o1;
        return coerceAndEvaluate(c1, c2, EvaluationUtil::lessOrEqualNumbers, EvaluationUtil::lessOrEqual);
    }

    public static boolean greaterThan(Comparable c1, Object o2) {
        if (!(o2 instanceof Comparable)) {
            throw new ClassCastException(o2 + " is not Comparable"); // compatibility with standard-drl: MathProcessor.doOperationNonNumeric() throws ClassCastException when the right operand is not Comparable
        }
        Comparable c2 = (Comparable)o2;
        return coerceAndEvaluate(c1, c2, EvaluationUtil::greaterThanNumbers, EvaluationUtil::greaterThan);
    }

    public static boolean lessThan(Comparable c1, Object o2) {
        if (!(o2 instanceof Comparable)) {
            throw new ClassCastException(o2 + " is not Comparable"); // compatibility with standard-drl: MathProcessor.doOperationNonNumeric() throws ClassCastException when the right operand is not Comparable
        }
        Comparable c2 = (Comparable)o2;
        return coerceAndEvaluate(c1, c2, EvaluationUtil::lessThanNumbers, EvaluationUtil::lessThan);
    }

    public static boolean greaterOrEqual(Comparable c1, Object o2) {
        if (!(o2 instanceof Comparable)) {
            throw new ClassCastException(o2 + " is not Comparable"); // compatibility with standard-drl: MathProcessor.doOperationNonNumeric() throws ClassCastException when the right operand is not Comparable
        }
        Comparable c2 = (Comparable)o2;
        return coerceAndEvaluate(c1, c2, EvaluationUtil::greaterOrEqualNumbers, EvaluationUtil::greaterOrEqual);
    }

    public static boolean lessOrEqual(Comparable c1, Object o2) {
        if (!(o2 instanceof Comparable)) {
            throw new ClassCastException(o2 + " is not Comparable"); // compatibility with standard-drl: MathProcessor.doOperationNonNumeric() throws ClassCastException when the right operand is not Comparable
        }
        Comparable c2 = (Comparable)o2;
        return coerceAndEvaluate(c1, c2, EvaluationUtil::lessOrEqualNumbers, EvaluationUtil::lessOrEqual);
    }

    private static boolean coerceAndEvaluate(Comparable c1, Comparable c2, BiPredicate<Number, Number> numberPredicate, BiPredicate<Comparable, Comparable> plainPredicate) {
        Optional<NumberPair> optNumberPair = getCoercedNumberPair(c1, c2);
        if (optNumberPair.isPresent()) {
            NumberPair numberPair = optNumberPair.get();
            return numberPredicate.test(numberPair.getN1(), numberPair.getN2());
        }
        return plainPredicate.test(c1, c2);
    }

    private static Optional<NumberPair> getCoercedNumberPair(Comparable c1, Comparable c2) {
        if (c1 instanceof Number && c2 instanceof Number) {
            return Optional.of(new NumberPair((Number)c1, (Number)c2));
        }

        if (c1 instanceof Number && c2 instanceof String) {
            try {
                Number n2 = CoercionUtil.coerceToNumber((String) c2, c1.getClass());
                return Optional.of(new NumberPair((Number)c1, n2));
            } catch (RuntimeException e) {
                return Optional.empty();
            }
        }

        if (c1 instanceof String && c2 instanceof Number) {
            try {
                Number n1 = CoercionUtil.coerceToNumber((String) c1, c2.getClass());
                return Optional.of(new NumberPair(n1, (Number)c2));
            } catch (RuntimeException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private static class NumberPair {

        private final Number n1;
        private final Number n2;

        public NumberPair(Number n1, Number n2) {
            this.n1 = n1;
            this.n2 = n2;
        }

        public Number getN1() {
            return n1;
        }

        public Number getN2() {
            return n2;
        }
    }

    public static boolean greaterThanNumbers(Number n1, Number n2) {
        if (n1 == null || n2 == null) {
            return false;
        }
        double d1 = n1.doubleValue();
        double d2 = n2.doubleValue();
        if (Double.isNaN(d1) || Double.isNaN(d2)) {
            return false;
        }
        return Double.compare( d1, d2 ) > 0;
    }

    public static boolean greaterThanNumbers(Number n1, Object n2) {
        if (n2 instanceof Number) {
            return greaterThanNumbers(n1, (Number)n2);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static boolean greaterThanNumbers(Long n1, Long n2) {
        return n1 != null && n2 != null && n1.compareTo(n2) > 0;
    }

    public static boolean lessThanNumbers(Number n1, Number n2) {
        if (n1 == null || n2 == null) {
            return false;
        }
        double d1 = n1.doubleValue();
        double d2 = n2.doubleValue();
        if (Double.isNaN(d1) || Double.isNaN(d2)) {
            return false;
        }
        return Double.compare( d1, d2 ) < 0;
    }

    public static boolean lessThanNumbers(Number n1, Object n2) {
        if (n2 instanceof Number) {
            return lessThanNumbers(n1, (Number)n2);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static boolean lessThanNumbers(Long n1, Long n2) {
        return n1 != null && n2 != null && n1.compareTo(n2) < 0;
    }

    public static boolean greaterOrEqualNumbers(Number n1, Number n2) {
        return n1 != null && n2 != null && Double.compare( n1.doubleValue(), n2.doubleValue() ) >= 0;
    }

    public static boolean greaterOrEqualNumbers(Long n1, Long n2) {
        return n1 != null && n2 != null && n1.compareTo(n2) >= 0;
    }

    public static boolean lessOrEqualNumbers(Number n1, Number n2) {
        return n1 != null && n2 != null && Double.compare( n1.doubleValue(), n2.doubleValue() ) <= 0;
    }

    public static boolean lessOrEqualNumbers(Long n1, Long n2) {
        return n1 != null && n2 != null && n1.compareTo(n2) <= 0;
    }

    public static boolean greaterThanStringsAsNumbers(String s1, String s2) {
        return s1 != null && s2 != null && greaterThan(new BigDecimal(s1), new BigDecimal(s2));
    }

    public static boolean lessThanStringsAsNumbers(String s1, String s2) {
        return s1 != null && s2 != null && lessThan(new BigDecimal(s1), new BigDecimal(s2));
    }

    public static boolean greaterOrEqualStringsAsNumbers(String s1, String s2) {
        return s1 != null && s2 != null && greaterOrEqual(new BigDecimal(s1), new BigDecimal(s2));
    }

    public static boolean lessOrEqualStringsAsNumbers(String s1, String s2) {
        return s1 != null && s2 != null && lessOrEqual(new BigDecimal(s1), new BigDecimal(s2));
    }

    public static BigDecimal toBigDecimal(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        if (obj instanceof BigInteger) {
            return new BigDecimal( ( BigInteger ) obj );
        }
        if (obj instanceof String) {
            return new BigDecimal( ( String ) obj );
        }
        if (obj instanceof Long) {
            return new BigDecimal( ( Long ) obj );
        }
        if (obj instanceof Double) {
            return new BigDecimal( ( Double ) obj );
        }
        if (obj instanceof Integer) {
            return new BigDecimal( ( Integer ) obj );
        }
        if (obj instanceof Float) {
            return new BigDecimal( ( Float ) obj );
        }
        return null;
    }


    public static org.drools.util.bitmask.BitMask adaptBitMask(BitMask mask) {
        if (mask == null) {
            return null;
        }
        if (mask instanceof LongBitMask ) {
            long maskValue = (( LongBitMask ) mask).asLong();
            return maskValue == 0L ? org.drools.util.bitmask.EmptyBitMask.get() : new org.drools.util.bitmask.LongBitMask( maskValue );
        }
        if (mask instanceof EmptyBitMask ) {
            return org.drools.util.bitmask.EmptyBitMask.get();
        }
        if (mask instanceof AllSetBitMask ) {
            return org.drools.util.bitmask.AllSetBitMask.get();
        }
        if (mask instanceof AllSetButLastBitMask ) {
            return org.drools.util.bitmask.AllSetButLastBitMask.get();
        }
        if (mask instanceof EmptyButLastBitMask ) {
            return org.drools.util.bitmask.EmptyButLastBitMask.get();
        }
        if (mask instanceof OpenBitSet ) {
            return new org.drools.util.bitmask.OpenBitSet( ( (OpenBitSet) mask ).getBits(), ( (OpenBitSet) mask ).getNumWords() );
        }
        throw new IllegalArgumentException( "Unknown bitmask: " + mask );
    }

    public static Date convertDate(String s) {
        return GregorianCalendar.from(convertDateLocal(s).atStartOfDay(ZoneId.systemDefault())).getTime();
    }

    public static LocalDate convertDateLocal(String s) {
        return LocalDate.parse(s, DATE_TIME_FORMATTER);
    }

    public static LocalDateTime convertDateTimeLocal( String s) {
        return convertDateLocal(s).atStartOfDay();
    }

    public static int string2Int(String s) {
        try {
            return Integer.parseInt( s );
        } catch (NumberFormatException nfe) {
            return (int) TimeUtils.parseTimeString( s );
        }
    }
}
