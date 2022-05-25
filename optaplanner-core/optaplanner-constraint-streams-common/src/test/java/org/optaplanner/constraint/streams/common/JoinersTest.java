/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.common;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.optaplanner.constraint.streams.common.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.common.penta.DefaultPentaJoiner;
import org.optaplanner.constraint.streams.common.quad.DefaultQuadJoiner;
import org.optaplanner.constraint.streams.common.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.Joiners;

class JoinersTest {

    @Test
    void equalBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultBiJoiner<BigInteger, BigDecimal> joiner = (DefaultBiJoiner<BigInteger, BigDecimal>) Joiners.equal(leftMapping,
                rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ONE, BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    void equalTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.equal(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ONE, BigInteger.ZERO, BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    void equalQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners.equal(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    void equalPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .equal(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    void lessThanBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultBiJoiner<BigInteger, BigDecimal> joiner = (DefaultBiJoiner<BigInteger, BigDecimal>) Joiners
                .lessThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    void lessThanTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.lessThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    void lessThanQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .lessThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    void lessThanPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .lessThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    void lessThanOrEqualBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultBiJoiner<BigInteger, BigDecimal> joiner = (DefaultBiJoiner<BigInteger, BigDecimal>) Joiners
                .lessThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    void lessThanOrEqualTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.lessThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    void lessThanOrEqualQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .lessThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    void lessThanOrEqualPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .lessThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    void greaterThanBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultBiJoiner<BigInteger, BigDecimal> joiner = (DefaultBiJoiner<BigInteger, BigDecimal>) Joiners
                .greaterThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    void greaterThanTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.greaterThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    void greaterThanQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .greaterThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    void greaterThanPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .greaterThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    void greaterThanOrEqualBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultBiJoiner<BigInteger, BigDecimal> joiner = (DefaultBiJoiner<BigInteger, BigDecimal>) Joiners
                .greaterThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    void greaterThanOrEqualTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.greaterThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    void greaterThanOrEqualQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .greaterThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    void greaterThanOrEqualPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (DefaultPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .greaterThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ONE)).isFalse();
        });
    }

    private static final class Interval<A, B> {
        final A start;
        final A end;
        final Function<A, B> map;

        public Interval(A start, A end, Function<A, B> map) {
            this.start = start;
            this.end = end;
            this.map = map;
        }

        public B getStart() {
            return map.apply(start);
        }

        public B getEnd() {
            return map.apply(end);
        }

        @Override
        public String toString() {
            return "(" + start.toString() + ", " + end.toString() + ")";
        }

        public static Interval<Long, BigDecimal> ofBigDecimal(Long start, Long end) {
            return new Interval<>(start, end, BigDecimal::valueOf);
        }

        public static Interval<Long, BigInteger> ofBigInt(Long start, Long end) {
            return new Interval<>(start, end, BigInteger::valueOf);
        }
    }

    @Test
    void overlapsBiDifferentTypes() {
        Function<Interval<Long, BigInteger>, Long> leftStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigInteger>, Long> leftEndMapping = interval -> interval.getEnd().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightEndMapping = interval -> interval.getEnd().longValue();

        DefaultBiJoiner<Interval<Long, BigInteger>, Interval<Long, BigDecimal>> joiner =
                (DefaultBiJoiner<Interval<Long, BigInteger>, Interval<Long, BigDecimal>>) Joiners.overlapping(leftStartMapping,
                        leftEndMapping, rightStartMapping, rightEndMapping);

        assertSoftly(softly -> {
            // True cases (equals, overlaps, contains, starts, ends)
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    Interval.ofBigDecimal(1L, 5L)))
                    .as("Case A = B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    Interval.ofBigDecimal(1L, 5L)))
                    .as("B starts before A, A ends after B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A starts before B, B ends after A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 5L),
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B contains A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    Interval.ofBigDecimal(3L, 5L)))
                    .as("A contains B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("A started by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B started by A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A ended by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B ended by A").isTrue();

            // False Cases (before, after, meets)

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    Interval.ofBigDecimal(5L, 7L)))
                    .as("A before B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(5L, 7L),
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("B before A").isFalse();

            // This is false since typically, when overlaps is used,
            // end is exclusive, and start is inclusive,
            // so 0-5, 5-10 do not overlap
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A meets B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("B meets A").isFalse();
        });
    }

    @Test
    void overlapsBiSameTypes() {
        Function<Interval<Long, BigInteger>, Long> leftStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigInteger>, Long> leftEndMapping = interval -> interval.getEnd().longValue();

        DefaultBiJoiner<Interval<Long, BigInteger>, Interval<Long, BigInteger>> joiner =
                (DefaultBiJoiner<Interval<Long, BigInteger>, Interval<Long, BigInteger>>) Joiners.overlapping(leftStartMapping,
                        leftEndMapping);

        assertSoftly(softly -> {
            // True cases (equals, overlaps, contains, starts, ends)
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    Interval.ofBigInt(1L, 5L)))
                    .as("Case A = B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    Interval.ofBigInt(1L, 5L)))
                    .as("B starts before A, A ends after B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    Interval.ofBigInt(3L, 7L)))
                    .as("A starts before B, B ends after A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 5L),
                    Interval.ofBigInt(1L, 7L)))
                    .as("B contains A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    Interval.ofBigInt(3L, 5L)))
                    .as("A contains B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    Interval.ofBigInt(1L, 3L)))
                    .as("A started by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    Interval.ofBigInt(1L, 7L)))
                    .as("B started by A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    Interval.ofBigInt(3L, 7L)))
                    .as("A ended by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    Interval.ofBigInt(1L, 7L)))
                    .as("B ended by A").isTrue();

            // False Cases (before, after, meets)

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    Interval.ofBigInt(5L, 7L)))
                    .as("A before B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(5L, 7L),
                    Interval.ofBigInt(1L, 3L)))
                    .as("B before A").isFalse();

            // This is false since typically, when overlaps is used,
            // end is exclusive, and start is inclusive,
            // so 0-5, 5-10 do not overlap
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    Interval.ofBigInt(3L, 7L)))
                    .as("A meets B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    Interval.ofBigInt(1L, 3L)))
                    .as("B meets A").isFalse();
        });
    }

    @Test
    void overlapsTri() {
        BiFunction<Interval<Long, BigInteger>, String, Long> leftStartMapping =
                (interval, ignored) -> interval.getStart().longValue();
        BiFunction<Interval<Long, BigInteger>, String, Long> leftEndMapping =
                (interval, ignored) -> interval.getEnd().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightEndMapping = interval -> interval.getEnd().longValue();

        DefaultTriJoiner<Interval<Long, BigInteger>, String, Interval<Long, BigDecimal>> joiner =
                (DefaultTriJoiner<Interval<Long, BigInteger>, String, Interval<Long, BigDecimal>>) Joiners.overlapping(
                        leftStartMapping,
                        leftEndMapping, rightStartMapping, rightEndMapping);

        assertSoftly(softly -> {
            // True cases (equals, overlaps, contains, starts, ends)
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 5L)))
                    .as("Case A = B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 5L)))
                    .as("B starts before A, A ends after B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A starts before B, B ends after A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 5L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B contains A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 5L)))
                    .as("A contains B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("A started by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B started by A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A ended by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B ended by A").isTrue();

            // False Cases (before, after, meets)

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(5L, 7L)))
                    .as("A before B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(5L, 7L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("B before A").isFalse();

            // This is false since typically, when overlaps is used,
            // end is exclusive, and start is inclusive,
            // so 0-5, 5-10 do not overlap
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A meets B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("B meets A").isFalse();
        });
    }

    @Test
    void overlapsQuad() {
        TriFunction<Interval<Long, BigInteger>, String, String, Long> leftStartMapping =
                (interval, ignored1, ignored2) -> interval.getStart().longValue();
        TriFunction<Interval<Long, BigInteger>, String, String, Long> leftEndMapping =
                (interval, ignored1, ignored2) -> interval.getEnd().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightEndMapping = interval -> interval.getEnd().longValue();

        DefaultQuadJoiner<Interval<Long, BigInteger>, String, String, Interval<Long, BigDecimal>> joiner =
                (DefaultQuadJoiner<Interval<Long, BigInteger>, String, String, Interval<Long, BigDecimal>>) Joiners
                        .overlapping(
                                leftStartMapping,
                                leftEndMapping, rightStartMapping, rightEndMapping);

        assertSoftly(softly -> {
            // True cases (equals, overlaps, contains, starts, ends)
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 5L)))
                    .as("Case A = B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 5L)))
                    .as("B starts before A, A ends after B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A starts before B, B ends after A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 5L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B contains A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 5L)))
                    .as("A contains B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("A started by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B started by A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A ended by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B ended by A").isTrue();

            // False Cases (before, after, meets)

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(5L, 7L)))
                    .as("A before B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(5L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("B before A").isFalse();

            // This is false since typically, when overlaps is used,
            // end is exclusive, and start is inclusive,
            // so 0-5, 5-10 do not overlap
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A meets B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("B meets A").isFalse();
        });
    }

    @Test
    void overlapsPenta() {
        QuadFunction<Interval<Long, BigInteger>, String, String, String, Long> leftStartMapping =
                (interval, ignored1, ignored2, ignored3) -> interval.getStart().longValue();
        QuadFunction<Interval<Long, BigInteger>, String, String, String, Long> leftEndMapping =
                (interval, ignored1, ignored2, ignored3) -> interval.getEnd().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightEndMapping = interval -> interval.getEnd().longValue();

        DefaultPentaJoiner<Interval<Long, BigInteger>, String, String, String, Interval<Long, BigDecimal>> joiner =
                (DefaultPentaJoiner<Interval<Long, BigInteger>, String, String, String, Interval<Long, BigDecimal>>) Joiners
                        .overlapping(leftStartMapping,
                                leftEndMapping, rightStartMapping, rightEndMapping);

        assertSoftly(softly -> {
            // True cases (equals, overlaps, contains, starts, ends)
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 5L)))
                    .as("Case A = B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 5L)))
                    .as("B starts before A, A ends after B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 5L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A starts before B, B ends after A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 5L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B contains A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 5L)))
                    .as("A contains B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("A started by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B started by A").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A ended by B").isTrue();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 7L)))
                    .as("B ended by A").isTrue();

            // False Cases (before, after, meets)

            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(5L, 7L)))
                    .as("A before B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(5L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("B before A").isFalse();

            // This is false since typically, when overlaps is used,
            // end is exclusive, and start is inclusive,
            // so 0-5, 5-10 do not overlap
            softly.assertThat(joiner.matches(Interval.ofBigInt(1L, 3L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(3L, 7L)))
                    .as("A meets B").isFalse();

            softly.assertThat(joiner.matches(Interval.ofBigInt(3L, 7L),
                    "Ignored Arg",
                    "Ignored Arg",
                    "Ignored Arg",
                    Interval.ofBigDecimal(1L, 3L)))
                    .as("B meets A").isFalse();
        });
    }

}
