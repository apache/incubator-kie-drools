/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jpa.impl.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateTypeTest;

public class HardMediumSoftBigDecimalScoreHibernateTypeTest extends AbstractScoreHibernateTypeTest {

    @Test
    public void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(HardMediumSoftBigDecimalScore.ZERO),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.01000"), new BigDecimal("-4.32100"), new BigDecimal("-2.20000")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.01000"), new BigDecimal("-4.32100"), new BigDecimal("-2.20000")));
    }

    @Entity
    @TypeDef(defaultForType = HardMediumSoftBigDecimalScore.class, typeClass = HardMediumSoftBigDecimalScoreHibernateType.class)
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardMediumSoftBigDecimalScore> {

        protected HardMediumSoftBigDecimalScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(HardMediumSoftBigDecimalScore score) {
            this.score = score;
        }

        @Override
        @Columns(columns = {
                @Column(name = "initScore"),
                @Column(name = "hardScore", precision = 10, scale = 5),
                @Column(name = "mediumScore", precision = 10, scale = 5),
                @Column(name = "softScore", precision = 10, scale = 5)})
        public HardMediumSoftBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardMediumSoftBigDecimalScore score) {
            this.score = score;
        }

    }

}
