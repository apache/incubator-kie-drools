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

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static java.math.BigInteger.ZERO;
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
            softly.assertThat(joiner.matches(TEN, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(ONE, BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    public void equalTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.equal(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(ONE, ZERO, BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    public void equalQuad() {
        TriFunction<BigInteger, BigInteger, BigInteger, Long> leftMapping = (a, b, c) -> a.add(b).add(c).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal>) Joiners.equal(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(ONE, ZERO, ZERO, BigDecimal.ZERO)).isFalse();
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
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(ONE, ZERO, ZERO, ZERO, BigDecimal.ZERO)).isFalse();
        });
    }

    @Test
    public void lessThanBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractBiJoiner<BigInteger, BigDecimal> joiner = (AbstractBiJoiner<BigInteger, BigDecimal>) Joiners
                .lessThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(TEN, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void lessThanTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.lessThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(TEN, ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(ZERO, ZERO, BigDecimal.ONE)).isTrue();
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
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(ZERO, ZERO, ZERO, BigDecimal.ONE)).isTrue();
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
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(ZERO, ZERO, ZERO, ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void lessThanOrEqualBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractBiJoiner<BigInteger, BigDecimal> joiner = (AbstractBiJoiner<BigInteger, BigDecimal>) Joiners
                .lessThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(TEN, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void lessThanOrEqualTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.lessThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(TEN, ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(ZERO, ZERO, BigDecimal.ONE)).isTrue();
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
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(ZERO, ZERO, ZERO, BigDecimal.ONE)).isTrue();
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
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, ZERO, BigDecimal.ONE)).isFalse();
            softly.assertThat(joiner.matches(ZERO, ZERO, ZERO, ZERO, BigDecimal.ONE)).isTrue();
        });
    }

    @Test
    public void greaterThanBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractBiJoiner<BigInteger, BigDecimal> joiner = (AbstractBiJoiner<BigInteger, BigDecimal>) Joiners
                .greaterThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(TEN, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    public void greaterThanTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.greaterThan(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(TEN, ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(ZERO, ZERO, BigDecimal.ONE)).isFalse();
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
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(ZERO, ZERO, ZERO, BigDecimal.ONE)).isFalse();
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
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, ZERO, BigDecimal.TEN)).isFalse();
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(ZERO, ZERO, ZERO, ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    public void greaterThanOrEqualBi() {
        Function<BigInteger, Long> leftMapping = BigInteger::longValue;
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractBiJoiner<BigInteger, BigDecimal> joiner = (AbstractBiJoiner<BigInteger, BigDecimal>) Joiners
                .greaterThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(TEN, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(ZERO, BigDecimal.ONE)).isFalse();
        });
    }

    @Test
    public void greaterThanOrEqualTri() {
        BiFunction<BigInteger, BigInteger, Long> leftMapping = (a, b) -> a.add(b).longValue();
        Function<BigDecimal, Long> rightMapping = BigDecimal::longValue;
        AbstractTriJoiner<BigInteger, BigInteger, BigDecimal> joiner =
                (AbstractTriJoiner<BigInteger, BigInteger, BigDecimal>) Joiners.greaterThanOrEqual(leftMapping, rightMapping);
        assertSoftly(softly -> {
            softly.assertThat(joiner.matches(TEN, ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(TEN, ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(ZERO, ZERO, BigDecimal.ONE)).isFalse();
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
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(ZERO, ZERO, ZERO, BigDecimal.ONE)).isFalse();
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
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, ZERO, BigDecimal.TEN)).isTrue();
            softly.assertThat(joiner.matches(TEN, ZERO, ZERO, ZERO, BigDecimal.ONE)).isTrue();
            softly.assertThat(joiner.matches(ZERO, ZERO, ZERO, ZERO, BigDecimal.ONE)).isFalse();
        });
    }
}
