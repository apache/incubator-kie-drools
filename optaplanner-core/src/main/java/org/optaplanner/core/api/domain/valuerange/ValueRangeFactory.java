package org.optaplanner.core.api.domain.valuerange;

import java.math.BigDecimal;

import org.optaplanner.core.impl.domain.valuerange.buildin.bigdecimal.BigDecimalValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.primdouble.DoubleValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.primint.IntValueRange;

/**
 * Factory for {@link ValueRange}.
 */
public class ValueRangeFactory {

    /**
     * Build a {@link CountableValueRange} of all {@code int} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, >= {@code from}
     * @return never null
     */
    public static CountableValueRange<Integer> createIntValueRange(int from, int to) {
        return new IntValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@code int} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, >= {@code from}
     * @param incrementUnit > 0
     * @return never null
     */
    public static CountableValueRange<Integer> createIntValueRange(int from, int to, int incrementUnit) {
        return new IntValueRange(from, to, incrementUnit);
    }

    /**
     * Build a {@link CountableValueRange} of all {@link BigDecimal} values (of a specific scale) between 2 bounds.
     * All parameters must have the same {@link BigDecimal#scale()}.
     * @param from inclusive minimum
     * @param to exclusive maximum, >= {@code from}
     */
    public static CountableValueRange<BigDecimal> createBigDecimalValueRange(BigDecimal from, BigDecimal to) {
        return new BigDecimalValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@code BigDecimal} values (of a specific scale) between 2 bounds.
     * All parameters must have the same {@link BigDecimal#scale()}.
     * @param from inclusive minimum
     * @param to exclusive maximum, >= {@code from}
     * @param incrementUnit > 0
     */
    public static CountableValueRange<BigDecimal> createBigDecimalValueRange(BigDecimal from, BigDecimal to, BigDecimal incrementUnit) {
        return new BigDecimalValueRange(from, to, incrementUnit);
    }

    /**
     * Build an uncountable {@link ValueRange} of all {@code double} values between 2 bounds.
     * @param from inclusive minimum
     * @param to exclusive maximum, >= {@code from}
     */
    public static ValueRange<Double> createDoubleValueRange(double from, double to) {
        return new DoubleValueRange(from, to);
    }

}
