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

package org.optaplanner.core.api.domain.valuerange;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.optaplanner.core.impl.domain.valuerange.buildin.bigdecimal.BigDecimalValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.biginteger.BigIntegerValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.primdouble.DoubleValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.primint.IntValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.primlong.LongValueRange;

/**
 * Factory for {@link ValueRange}.
 */
public class ValueRangeFactory {

    /**
     * Build a {@link CountableValueRange} of all {@code int} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @return never null
     */
    public static CountableValueRange<Integer> createIntValueRange(int from, int to) {
        return new IntValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@code int} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @param incrementUnit {@code > 0}
     * @return never null
     */
    public static CountableValueRange<Integer> createIntValueRange(int from, int to, int incrementUnit) {
        return new IntValueRange(from, to, incrementUnit);
    }

    /**
     * Build a {@link CountableValueRange} of all {@code long} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @return never null
     */
    public static CountableValueRange<Long> createLongValueRange(long from, long to) {
        return new LongValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@code long} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @param incrementUnit {@code > 0}
     * @return never null
     */
    public static CountableValueRange<Long> createLongValueRange(long from, long to, long incrementUnit) {
        return new LongValueRange(from, to, incrementUnit);
    }

    /**
     * Build an uncountable {@link ValueRange} of all {@code double} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @return never null
     */
    public static ValueRange<Double> createDoubleValueRange(double from, double to) {
        return new DoubleValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of all {@link BigInteger} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @return never null
     */
    public static CountableValueRange<BigInteger> createBigIntegerValueRange(BigInteger from, BigInteger to) {
        return new BigIntegerValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@link BigInteger} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @param incrementUnit {@code > 0}
     * @return never null
     */
    public static CountableValueRange<BigInteger> createBigIntegerValueRange(BigInteger from, BigInteger to, BigInteger incrementUnit) {
        return new BigIntegerValueRange(from, to, incrementUnit);
    }

    /**
     * Build a {@link CountableValueRange} of all {@link BigDecimal} values (of a specific scale) between 2 bounds.
     * All parameters must have the same {@link BigDecimal#scale()}.
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @return never null
     */
    public static CountableValueRange<BigDecimal> createBigDecimalValueRange(BigDecimal from, BigDecimal to) {
        return new BigDecimalValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@link BigDecimal} values (of a specific scale) between 2 bounds.
     * All parameters must have the same {@link BigDecimal#scale()}.
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @param incrementUnit {@code > 0}
     * @return never null
     */
    public static CountableValueRange<BigDecimal> createBigDecimalValueRange(BigDecimal from, BigDecimal to, BigDecimal incrementUnit) {
        return new BigDecimalValueRange(from, to, incrementUnit);
    }

}
