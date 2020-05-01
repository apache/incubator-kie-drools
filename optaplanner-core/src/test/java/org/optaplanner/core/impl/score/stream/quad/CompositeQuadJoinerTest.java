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

package org.optaplanner.core.impl.score.stream.quad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.score.stream.quad.AbstractQuadJoiner.merge;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;

public class CompositeQuadJoinerTest {

    @Test
    public void compositeMappings() {
        QuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner1 = Joiners
                .equal((a, b, c) -> a.add(b).add(c).longValue(), BigDecimal::longValue);
        QuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> joiner2 = Joiners
                .lessThan((a, b, c) -> a.add(b).add(c).longValue(), BigDecimal::longValue);
        AbstractQuadJoiner<BigInteger, BigInteger, BigInteger, BigDecimal> composite = merge(joiner1, joiner2);
        TriFunction<BigInteger, BigInteger, BigInteger, Object[]> leftMapping = composite.getLeftCombinedMapping();
        Object[] left = leftMapping.apply(BigInteger.ONE, BigInteger.ONE, BigInteger.TEN);
        assertThat(left).containsExactly(12L, 12L);
        Function<BigDecimal, Object[]> rightMapping = composite.getRightCombinedMapping();
        Object[] right = rightMapping.apply(BigDecimal.TEN);
        assertThat(right).containsExactly(10L, 10L);
    }

}
