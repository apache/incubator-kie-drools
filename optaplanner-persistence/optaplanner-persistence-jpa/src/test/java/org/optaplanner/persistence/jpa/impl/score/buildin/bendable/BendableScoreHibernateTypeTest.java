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

package org.optaplanner.persistence.jpa.impl.score.buildin.bendable;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateTypeTest;

public class BendableScoreHibernateTypeTest extends AbstractScoreHibernateTypeTest {

    @Test
    public void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(BendableScore.zero(3, 2)),
                BendableScore.of(new int[]{10000, 2000, 300}, new int[]{40, 5}),
                BendableScore.ofUninitialized(-7, new int[]{10000, 2000, 300}, new int[]{40, 5}));
    }

    @Entity
    @TypeDef(defaultForType = BendableScore.class, typeClass = BendableScoreHibernateType.class,
            parameters = {@Parameter(name = "hardLevelsSize", value = "3"), @Parameter(name = "softLevelsSize", value = "2")})
    public static class TestJpaEntity extends AbstractTestJpaEntity<BendableScore> {

        protected BendableScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(BendableScore score) {
            this.score = score;
        }

        @Override
        @Columns(columns = {@Column(name = "initScore"),
                @Column(name = "hard0Score"), @Column(name = "hard1Score"), @Column(name = "hard2Score"),
                @Column(name = "soft0Score"), @Column(name = "soft1Score")})
        public BendableScore getScore() {
            return score;
        }

        @Override
        public void setScore(BendableScore score) {
            this.score = score;
        }
    }
}
