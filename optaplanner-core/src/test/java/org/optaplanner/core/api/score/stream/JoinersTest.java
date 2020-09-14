/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.penta.AbstractPentaJoiner;
import org.optaplanner.core.impl.score.stream.quad.AbstractQuadJoiner;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

public class JoinersTest {

    @Test
    public void equalBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractBiJoiner<BigInteger, BigDecimal> joiner = (AbstractBiJoiner<BigInteger, BigDecimal>) Joiners.equal(leftMapping,
                rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ONE, BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    public void equalTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.equal(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ONE, BigInteger.ZERO, BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    public void equalQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners.equal(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    public void equalPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .equal(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    public void lessThanBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractBiJoiner<BigInteger, BigDecimal> joiner = (AbstractBiJoiner<BigInteger, BigDecimal>) Joiners
                .lessThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void lessThanTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.lessThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void lessThanQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .lessThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void lessThanPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
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
    public void lessThanOrEqualBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractBiJoiner<BigInteger, BigDecimal> joiner = (AbstractBiJoiner<BigInteger, BigDecimal>) Joiners
                .lessThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void lessThanOrEqualTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.lessThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void lessThanOrEqualQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .lessThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void lessThanOrEqualPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
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
    public void greaterThanBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractBiJoiner<BigInteger, BigDecimal> joiner = (AbstractBiJoiner<BigInteger, BigDecimal>) Joiners
                .greaterThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    public void greaterThanTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.greaterThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    public void greaterThanQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .greaterThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    public void greaterThanPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
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
    public void greaterThanOrEqualBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractBiJoiner<BigInteger, BigDecimal> joiner = (AbstractBiJoiner<BigInteger, BigDecimal>) Joiners
                .greaterThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    public void greaterThanOrEqualTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.greaterThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    public void greaterThanOrEqualQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
                        .greaterThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    public void greaterThanOrEqualPenta() {
        QuadFunction<BigInteger, BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c, d) -> a.add(b).add(c).add(d)
                .longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractPentaJoiner<BigInteger, BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners
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
    public void overlapsBiDifferentTypes() {
        Function<Interval<Long, BigInteger>, Long> leftStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigInteger>, Long> leftEndMapping = interval -> interval.getEnd().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightEndMapping = interval -> interval.getEnd().longValue();

        AbstractBiJoiner<Interval<Long, BigInteger>, Interval<Long, BigDecimal>> joiner =
                (AbstractBiJoiner<Interval<Long, BigInteger>, Interval<Long, BigDecimal>>) Joiners.overlapping(leftStartMapping,
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
    public void overlapsBiSameTypes() {
        Function<Interval<Long, BigInteger>, Long> leftStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigInteger>, Long> leftEndMapping = interval -> interval.getEnd().longValue();

        AbstractBiJoiner<Interval<Long, BigInteger>, Interval<Long, BigInteger>> joiner =
                (AbstractBiJoiner<Interval<Long, BigInteger>, Interval<Long, BigInteger>>) Joiners.overlapping(leftStartMapping,
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
    public void overlapsTri() {
        BiFunction<Interval<Long, BigInteger>, String, Long> leftStartMapping =
                (interval, ignored) -> interval.getStart().longValue();
        BiFunction<Interval<Long, BigInteger>, String, Long> leftEndMapping =
                (interval, ignored) -> interval.getEnd().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightEndMapping = interval -> interval.getEnd().longValue();

        AbstractTriJoiner<Interval<Long, BigInteger>, String, Interval<Long, BigDecimal>> joiner =
                (AbstractTriJoiner<Interval<Long, BigInteger>, String, Interval<Long, BigDecimal>>) Joiners.overlapping(
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
    public void overlapsQuad() {
        TriFunction<Interval<Long, BigInteger>, String, String, Long> leftStartMapping =
                (interval, ignored1, ignored2) -> interval.getStart().longValue();
        TriFunction<Interval<Long, BigInteger>, String, String, Long> leftEndMapping =
                (interval, ignored1, ignored2) -> interval.getEnd().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightEndMapping = interval -> interval.getEnd().longValue();

        AbstractQuadJoiner<Interval<Long, BigInteger>, String, String, Interval<Long, BigDecimal>> joiner =
                (AbstractQuadJoiner<Interval<Long, BigInteger>, String, String, Interval<Long, BigDecimal>>) Joiners
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
    public void overlapsPenta() {
        QuadFunction<Interval<Long, BigInteger>, String, String, String, Long> leftStartMapping =
                (interval, ignored1, ignored2, ignored3) -> interval.getStart().longValue();
        QuadFunction<Interval<Long, BigInteger>, String, String, String, Long> leftEndMapping =
                (interval, ignored1, ignored2, ignored3) -> interval.getEnd().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightStartMapping = interval -> interval.getStart().longValue();
        Function<Interval<Long, BigDecimal>, Long> rightEndMapping = interval -> interval.getEnd().longValue();

        AbstractPentaJoiner<Interval<Long, BigInteger>, String, String, String, Interval<Long, BigDecimal>> joiner =
                (AbstractPentaJoiner<Interval<Long, BigInteger>, String, String, String, Interval<Long, BigDecimal>>) Joiners
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
