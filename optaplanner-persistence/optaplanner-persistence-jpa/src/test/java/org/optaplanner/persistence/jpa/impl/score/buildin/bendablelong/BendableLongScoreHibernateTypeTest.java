/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jpa.impl.score.buildin.bendablelong;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateTypeTest;

public class BendableLongScoreHibernateTypeTest extends AbstractScoreHibernateTypeTest {

    @Test
    public void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(BendableLongScore.zero(3, 2)),
                BendableLongScore.of(new long[]{10000L, 2000L, 300L}, new long[]{40L, 5L}),
                BendableLongScore.ofUninitialized(-7, new long[]{10000L, 2000L, 300L}, new long[]{40L, 5L}));
    }

    @Entity
    @TypeDef(defaultForType = BendableLongScore.class, typeClass = BendableLongScoreHibernateType.class,
            parameters = {@Parameter(name = "hardLevelsSize", value = "3"), @Parameter(name = "softLevelsSize", value = "2")})
    public static class TestJpaEntity extends AbstractTestJpaEntity<BendableLongScore> {

        protected BendableLongScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(BendableLongScore score) {
            this.score = score;
        }

        @Override
        @Columns(columns = {@Column(name = "initScore"),
                @Column(name = "hard0Score"), @Column(name = "hard1Score"), @Column(name = "hard2Score"),
                @Column(name = "soft0Score"), @Column(name = "soft1Score")})
        public BendableLongScore getScore() {
            return score;
        }

        @Override
        public void setScore(BendableLongScore score) {
            this.score = score;
        }

    }

}
