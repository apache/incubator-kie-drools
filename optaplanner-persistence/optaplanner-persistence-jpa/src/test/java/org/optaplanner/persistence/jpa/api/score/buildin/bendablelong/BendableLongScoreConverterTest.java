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

package org.optaplanner.persistence.jpa.api.score.buildin.bendablelong;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.persistence.jpa.AbstractScoreJpaTest;

public class BendableLongScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    public void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(BendableLongScore.zero(3, 2)), null,
                BendableLongScore.of(new long[] { 10000L, 2000L, 300L }, new long[] { 40L, 5L }),
                BendableLongScore.ofUninitialized(-7, new long[] { 10000L, 2000L, 300L }, new long[] { 40L, 5L }));
    }

    @Entity
    public static class TestJpaEntity extends AbstractTestJpaEntity<BendableLongScore> {

        @Convert(converter = BendableLongScoreConverter.class)
        protected BendableLongScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(BendableLongScore score) {
            this.score = score;
        }

        @Override
        public BendableLongScore getScore() {
            return score;
        }

        @Override
        public void setScore(BendableLongScore score) {
            this.score = score;
        }
    }
}
