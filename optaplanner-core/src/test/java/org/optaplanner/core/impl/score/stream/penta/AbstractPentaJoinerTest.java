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

package org.optaplanner.core.impl.score.stream.penta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.optaplanner.core.impl.score.stream.penta.AbstractPentaJoiner.merge;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;

public class AbstractPentaJoinerTest {

    @Test
    public void merge0Joiners() {
        assertThat(merge()).isInstanceOf(NonePentaJoiner.class);
    }

    @Test
    public void merge1JoinersNone() {
        assertThat(merge(new NonePentaJoiner<>())).isInstanceOf(NonePentaJoiner.class);
    }

    @Test
    public void merge1JoinersSingle() {
        assertThat(merge(Joiners.equal((a, b, c, d) -> 0, e -> 0))).isInstanceOf(SinglePentaJoiner.class);
    }

    @Test
    public void merge2Joiners() {
        PentaJoiner<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigInteger> joiner1 = Joiners
                .equal((a, b, c, d) -> a.add(b).add(c).add(d).longValue(), BigInteger::longValue);
        PentaJoiner<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigInteger> joiner2 = Joiners
                .lessThan((a, b, c, d) -> a.add(b).add(c).add(d).longValue(), BigInteger::longValue);
        AbstractPentaJoiner<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigInteger> mergedJoiner = merge(joiner1, joiner2);
        assertSoftly(softly -> {
            softly.assertThat(mergedJoiner).isInstanceOf(CompositePentaJoiner.class);
            softly.assertThat(mergedJoiner.getJoinerTypes()).containsExactly(JoinerType.EQUAL, JoinerType.LESS_THAN);
        });
    }

    @Test
    public void merge2Joiners1Composite() {
        PentaJoiner<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigInteger> joiner1 = Joiners
                .equal((a, b, c, d) -> a.add(b).add(c).add(d).longValue(), BigInteger::longValue);
        PentaJoiner<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigInteger> joiner2 = Joiners
                .lessThan((a, b, c, d) -> a.add(b).add(c).add(d).longValue(), BigInteger::longValue);
        AbstractPentaJoiner<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigInteger> mergedJoiner = merge(joiner1, joiner2);
        PentaJoiner<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigInteger> joiner3 = Joiners
                .greaterThan((a, b, c, d) -> a.add(b).add(c).add(d).longValue(), BigInteger::longValue);
        AbstractPentaJoiner<BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigInteger> reMergedJoiner = merge(mergedJoiner,
                joiner3);
        assertSoftly(softly -> {
            softly.assertThat(reMergedJoiner).isInstanceOf(CompositePentaJoiner.class);
            softly.assertThat(reMergedJoiner.getJoinerTypes())
                    .containsExactly(JoinerType.EQUAL, JoinerType.LESS_THAN, JoinerType.GREATER_THAN);
        });
    }

}
