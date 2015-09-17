/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoft;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Persistence;
import javax.persistence.Transient;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.BeforeClass;
import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import static org.junit.Assert.*;

public class HardSoftScoreHibernateTypeTest extends org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateTypeTest {

    @Test
    public void persistAndMerge() {
        Long id = persistAndAssert(new TestJpaEntity(null));
        findAssertAndChangeScore(TestJpaEntity.class, id, null, HardSoftScore.valueOf(-10, -2));
        findAndAssert(TestJpaEntity.class, id, HardSoftScore.valueOf(-10, -2));
    }

    @Entity
    @TypeDef(defaultForType = HardSoftScore.class, typeClass = HardSoftScoreHibernateType.class)
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardSoftScore> {

        protected HardSoftScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(HardSoftScore score) {
            this.score = score;
        }

        @Columns(columns = {@Column(name = "hardScore"), @Column(name="softScore")})
        public HardSoftScore getScore() {
            return score;
        }

        public void setScore(HardSoftScore score) {
            this.score = score;
        }

    }

}
