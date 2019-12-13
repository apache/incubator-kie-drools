/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.model.BitMask;
import org.drools.model.bitmask.AllSetBitMask;
import org.drools.model.bitmask.AllSetButLastBitMask;
import org.drools.model.bitmask.EmptyBitMask;
import org.drools.model.bitmask.EmptyButLastBitMask;
import org.drools.model.bitmask.LongBitMask;
import org.drools.model.bitmask.OpenBitSet;

public class EvaluationUtil {

    public static boolean areNullSafeEquals(Object obj1, Object obj2) {
        return obj1 != null ? obj1.equals( obj2 ) : obj2 == null;
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

    public static boolean greaterThanNumbers(Number n1, Number n2) {
        return n1 != null && n2 != null && Double.compare( n1.doubleValue(), n2.doubleValue() ) > 0;
    }

    public static boolean greaterThanNumbers(Number n1, Object n2) {
        if(n2 instanceof Number){
            return greaterThanNumbers(n1, (Number)n2);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static boolean greaterThanNumbers(Long n1, Long n2) {
        return n1 != null && n2 != null && n1.compareTo(n2) > 0;
    }

    public static boolean lessThanNumbers(Number n1, Number n2) {
        return n1 != null && n2 != null && Double.compare( n1.doubleValue(), n2.doubleValue() ) < 0;
    }

    public static boolean lessThanNumbers(Number n1, Object n2) {
        if(n2 instanceof Number){
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


    public static org.drools.core.util.bitmask.BitMask adaptBitMask(BitMask mask) {
        if (mask == null) {
            return null;
        }
        if (mask instanceof LongBitMask ) {
            return new org.drools.core.util.bitmask.LongBitMask( ( (LongBitMask) mask ).asLong() );
        }
        if (mask instanceof EmptyBitMask ) {
            return org.drools.core.util.bitmask.EmptyBitMask.get();
        }
        if (mask instanceof AllSetBitMask ) {
            return org.drools.core.util.bitmask.AllSetBitMask.get();
        }
        if (mask instanceof AllSetButLastBitMask ) {
            return org.drools.core.util.bitmask.AllSetButLastBitMask.get();
        }
        if (mask instanceof EmptyButLastBitMask ) {
            return org.drools.core.util.bitmask.EmptyButLastBitMask.get();
        }
        if (mask instanceof OpenBitSet ) {
            return new org.drools.core.util.bitmask.OpenBitSet( ( (OpenBitSet) mask ).getBits(), ( (OpenBitSet) mask ).getNumWords() );
        }
        throw new IllegalArgumentException( "Unknown bitmask: " + mask );
    }
}
