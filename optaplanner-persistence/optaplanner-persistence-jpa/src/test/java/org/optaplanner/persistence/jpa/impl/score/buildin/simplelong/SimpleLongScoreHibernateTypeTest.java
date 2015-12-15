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

package org.optaplanner.persistence.jpa.impl.score.buildin.simplelong;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateTypeTest;

import static org.junit.Assert.*;

public class SimpleLongScoreHibernateTypeTest extends AbstractScoreHibernateTypeTest {

    @Test
    public void persistAndMerge() {
        Long id = persistAndAssert(new TestJpaEntity(null));
        findAssertAndChangeScore(TestJpaEntity.class, id, null, SimpleLongScore.valueOf(-10L));
        findAndAssert(TestJpaEntity.class, id, SimpleLongScore.valueOf(-10L));
    }

    @Entity
    @TypeDef(defaultForType = SimpleLongScore.class, typeClass = SimpleLongScoreHibernateType.class)
    public static class TestJpaEntity extends AbstractTestJpaEntity<SimpleLongScore> {

        protected SimpleLongScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(SimpleLongScore score) {
            this.score = score;
        }

        @Columns(columns = {@Column(name = "score")})
        public SimpleLongScore getScore() {
            return score;
        }

        public void setScore(SimpleLongScore score) {
            this.score = score;
        }

    }

}
