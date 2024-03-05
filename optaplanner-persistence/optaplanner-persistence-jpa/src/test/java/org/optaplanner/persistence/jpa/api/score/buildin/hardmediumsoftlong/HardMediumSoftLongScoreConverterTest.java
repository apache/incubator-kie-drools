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

package org.optaplanner.persistence.jpa.api.score.buildin.hardmediumsoftlong;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class HardMediumSoftLongScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new HardMediumSoftLongScoreConverterTestJpaEntity(HardMediumSoftLongScore.ZERO), null,
                HardMediumSoftLongScore.of(-100L, -20L, -3L),
                HardMediumSoftLongScore.ofUninitialized(-7, -100L, -20L, -3L));
    }

    @Entity
    static class HardMediumSoftLongScoreConverterTestJpaEntity
            extends AbstractTestJpaEntity<HardMediumSoftLongScore> {

        @Convert(converter = HardMediumSoftLongScoreConverter.class)
        protected HardMediumSoftLongScore score;

        HardMediumSoftLongScoreConverterTestJpaEntity() {
        }

        public HardMediumSoftLongScoreConverterTestJpaEntity(HardMediumSoftLongScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftLongScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardMediumSoftLongScore score) {
            this.score = score;
        }
    }
}
