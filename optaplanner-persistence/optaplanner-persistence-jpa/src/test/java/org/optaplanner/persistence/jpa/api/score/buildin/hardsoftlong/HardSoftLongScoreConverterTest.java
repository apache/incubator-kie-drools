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

package org.optaplanner.persistence.jpa.api.score.buildin.hardsoftlong;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.jpa.AbstractScoreJpaTest;

public class HardSoftLongScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    public void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(HardSoftLongScore.ZERO), null,
                HardSoftLongScore.of(-10L, -2L),
                HardSoftLongScore.ofUninitialized(-7, -10L, -2L));
    }

    @Entity
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardSoftLongScore> {

        @Convert(converter = HardSoftLongScoreConverter.class)
        protected HardSoftLongScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(HardSoftLongScore score) {
            this.score = score;
        }

        @Override
        public HardSoftLongScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardSoftLongScore score) {
            this.score = score;
        }
    }
}
