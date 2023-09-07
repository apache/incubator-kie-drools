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

package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoftlong;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HardSoftLongScoreHibernateTypeTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new HardSoftLongScoreHibernateTypeTestJpaEntity(HardSoftLongScore.ZERO),
                HardSoftLongScore.of(-10L, -2L),
                HardSoftLongScore.ofUninitialized(-7, -10L, -2L));
    }

    @Entity
    @TypeDef(defaultForType = HardSoftLongScore.class, typeClass = HardSoftLongScoreHibernateType.class)
    static class HardSoftLongScoreHibernateTypeTestJpaEntity extends AbstractTestJpaEntity<HardSoftLongScore> {

        @Columns(columns = { @Column(name = "initScore"), @Column(name = "hardScore"), @Column(name = "softScore") })
        protected HardSoftLongScore score;

        HardSoftLongScoreHibernateTypeTestJpaEntity() {
        }

        public HardSoftLongScoreHibernateTypeTestJpaEntity(HardSoftLongScore score) {
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
