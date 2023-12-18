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

package org.optaplanner.persistence.jpa.api.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HardMediumSoftBigDecimalScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new HardMediumSoftBigDecimalScoreConverterTestJpaEntity(HardMediumSoftBigDecimalScore.ZERO), null,
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.01000"), new BigDecimal("-4.32100"),
                        new BigDecimal("-2.20000")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.01000"), new BigDecimal("-4.32100"),
                        new BigDecimal("-2.20000")));
    }

    @Entity
    static class HardMediumSoftBigDecimalScoreConverterTestJpaEntity
            extends AbstractTestJpaEntity<HardMediumSoftBigDecimalScore> {

        @Convert(converter = HardMediumSoftBigDecimalScoreConverter.class)
        protected HardMediumSoftBigDecimalScore score;

        HardMediumSoftBigDecimalScoreConverterTestJpaEntity() {
        }

        public HardMediumSoftBigDecimalScoreConverterTestJpaEntity(HardMediumSoftBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardMediumSoftBigDecimalScore score) {
            this.score = score;
        }
    }
}
