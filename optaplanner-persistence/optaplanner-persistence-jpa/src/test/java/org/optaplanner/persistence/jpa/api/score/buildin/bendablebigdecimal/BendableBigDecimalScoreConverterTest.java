/*
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

package org.optaplanner.persistence.jpa.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class BendableBigDecimalScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new BendableBigDecimalScoreConverterTestJpaEntity(BendableBigDecimalScore.zero(3, 2)), null,
                BendableBigDecimalScore.of(
                        new BigDecimal[] { new BigDecimal("10000.00001"), new BigDecimal("2000.00020"),
                                new BigDecimal("300.00300") },
                        new BigDecimal[] { new BigDecimal("40.04000"), new BigDecimal("5.50000") }),
                BendableBigDecimalScore.ofUninitialized(-7,
                        new BigDecimal[] { new BigDecimal("10000.00001"), new BigDecimal("2000.00020"),
                                new BigDecimal("300.00300") },
                        new BigDecimal[] { new BigDecimal("40.04000"), new BigDecimal("5.50000") }));
    }

    @Entity
    static class BendableBigDecimalScoreConverterTestJpaEntity extends AbstractTestJpaEntity<BendableBigDecimalScore> {

        @Convert(converter = BendableBigDecimalScoreConverter.class)
        protected BendableBigDecimalScore score;

        BendableBigDecimalScoreConverterTestJpaEntity() {
        }

        public BendableBigDecimalScoreConverterTestJpaEntity(BendableBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public BendableBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(BendableBigDecimalScore score) {
            this.score = score;
        }
    }
}
