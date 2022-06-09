package org.optaplanner.core.api.domain.valuerange;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

import org.optaplanner.core.impl.domain.valuerange.buildin.bigdecimal.BigDecimalValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.biginteger.BigIntegerValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.primboolean.BooleanValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.primdouble.DoubleValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.primint.IntValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.primlong.LongValueRange;
import org.optaplanner.core.impl.domain.valuerange.buildin.temporal.TemporalValueRange;

/**
 * Factory for {@link ValueRange}.
 */
public final class ValueRangeFactory {

    /**
     * Build a {@link CountableValueRange} of both {@code boolean} values.
     *
     * @return never null
     */
    public static CountableValueRange<Boolean> createBooleanValueRange() {
        return new BooleanValueRange();
    }

    /**
     * Build a {@link CountableValueRange} of all {@code int} values between 2 bounds.
     *
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @return never null
     */
    public static CountableValueRange<Integer> createIntValueRange(int from, int to) {
        return new IntValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@code int} values between 2 bounds.
     *
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
     *
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @return never null
     */
    public static CountableValueRange<Long> createLongValueRange(long from, long to) {
        return new LongValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@code long} values between 2 bounds.
     *
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
     *
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @return never null
     */
    public static ValueRange<Double> createDoubleValueRange(double from, double to) {
        return new DoubleValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of all {@link BigInteger} values between 2 bounds.
     *
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @return never null
     */
    public static CountableValueRange<BigInteger> createBigIntegerValueRange(BigInteger from, BigInteger to) {
        return new BigIntegerValueRange(from, to);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@link BigInteger} values between 2 bounds.
     *
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @param incrementUnit {@code > 0}
     * @return never null
     */
    public static CountableValueRange<BigInteger> createBigIntegerValueRange(BigInteger from, BigInteger to,
            BigInteger incrementUnit) {
        return new BigIntegerValueRange(from, to, incrementUnit);
    }

    /**
     * Build a {@link CountableValueRange} of all {@link BigDecimal} values (of a specific scale) between 2 bounds.
     * All parameters must have the same {@link BigDecimal#scale()}.
     *
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
     *
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @param incrementUnit {@code > 0}
     * @return never null
     */
    public static CountableValueRange<BigDecimal> createBigDecimalValueRange(BigDecimal from, BigDecimal to,
            BigDecimal incrementUnit) {
        return new BigDecimalValueRange(from, to, incrementUnit);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@link LocalDate} values between 2 bounds.
     * <p>
     * Facade for {@link #createTemporalValueRange(Temporal, Temporal, long, TemporalUnit)}.
     *
     * @param from never null, inclusive minimum
     * @param to never null, exclusive maximum, {@code >= from}
     * @param incrementUnitAmount {@code > 0}
     * @param incrementUnitType never null, must be {@link LocalDate#isSupported(TemporalUnit) supported}
     */
    public static CountableValueRange<LocalDate> createLocalDateValueRange(
            LocalDate from, LocalDate to, long incrementUnitAmount, TemporalUnit incrementUnitType) {
        return createTemporalValueRange(from, to, incrementUnitAmount, incrementUnitType);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@link LocalTime} values between 2 bounds.
     * <p>
     * Facade for {@link #createTemporalValueRange(Temporal, Temporal, long, TemporalUnit)}.
     *
     * @param from never null, inclusive minimum
     * @param to never null, exclusive maximum, {@code >= from}
     * @param incrementUnitAmount {@code > 0}
     * @param incrementUnitType never null, must be {@link LocalTime#isSupported(TemporalUnit) supported}
     */
    public static CountableValueRange<LocalTime> createLocalTimeValueRange(
            LocalTime from, LocalTime to, long incrementUnitAmount, TemporalUnit incrementUnitType) {
        return createTemporalValueRange(from, to, incrementUnitAmount, incrementUnitType);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@link LocalDateTime} values between 2 bounds.
     * <p>
     * Facade for {@link #createTemporalValueRange(Temporal, Temporal, long, TemporalUnit)}.
     *
     * @param from never null, inclusive minimum
     * @param to never null, exclusive maximum, {@code >= from}
     * @param incrementUnitAmount {@code > 0}
     * @param incrementUnitType never null, must be {@link LocalDateTime#isSupported(TemporalUnit) supported}
     */
    public static CountableValueRange<LocalDateTime> createLocalDateTimeValueRange(
            LocalDateTime from, LocalDateTime to, long incrementUnitAmount, TemporalUnit incrementUnitType) {
        return createTemporalValueRange(from, to, incrementUnitAmount, incrementUnitType);
    }

    /**
     * Build a {@link CountableValueRange} of a subset of {@link Temporal} values (such as {@link LocalDate} or
     * {@link LocalDateTime}) between 2 bounds.
     * All parameters must have the same {@link TemporalUnit}.
     *
     * @param from never null, inclusive minimum
     * @param to never null, exclusive maximum, {@code >= from}
     * @param incrementUnitAmount {@code > 0}
     * @param incrementUnitType never null, must be {@link Temporal#isSupported(TemporalUnit) supported} by {@code from} and
     *        {@code to}
     */
    public static <Temporal_ extends Temporal & Comparable<? super Temporal_>> CountableValueRange<Temporal_>
            createTemporalValueRange(Temporal_ from, Temporal_ to, long incrementUnitAmount, TemporalUnit incrementUnitType) {
        return new TemporalValueRange<>(from, to, incrementUnitAmount, incrementUnitType);
    }

}
