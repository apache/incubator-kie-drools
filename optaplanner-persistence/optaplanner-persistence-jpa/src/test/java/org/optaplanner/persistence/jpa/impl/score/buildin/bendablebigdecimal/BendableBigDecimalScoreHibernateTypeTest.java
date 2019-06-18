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

package org.optaplanner.persistence.jpa.impl.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateTypeTest;

public class BendableBigDecimalScoreHibernateTypeTest extends AbstractScoreHibernateTypeTest {

    @Test
    public void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(BendableBigDecimalScore.zero(3, 2)),
                BendableBigDecimalScore.of(
                        new BigDecimal[]{new BigDecimal("10000.00001"), new BigDecimal("2000.00020"), new BigDecimal("300.00300")},
                        new BigDecimal[]{new BigDecimal("40.04000"), new BigDecimal("5.50000")}),
                BendableBigDecimalScore.ofUninitialized(-7,
                        new BigDecimal[]{new BigDecimal("10000.00001"), new BigDecimal("2000.00020"), new BigDecimal("300.00300")},
                        new BigDecimal[]{new BigDecimal("40.04000"), new BigDecimal("5.50000")}));
    }

    @Entity
    @TypeDef(defaultForType = BendableBigDecimalScore.class, typeClass = BendableBigDecimalScoreHibernateType.class,
            parameters = {@Parameter(name = "hardLevelsSize", value = "3"), @Parameter(name = "softLevelsSize", value = "2")})
    public static class TestJpaEntity extends AbstractScoreHibernateTypeTest.AbstractTestJpaEntity<BendableBigDecimalScore> {

        protected BendableBigDecimalScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(BendableBigDecimalScore score) {
            this.score = score;
        }

        @Override
        @Columns(columns = {
                @Column(name = "initScore"),
                @Column(name = "hard0Score", precision = 10, scale = 5),
                @Column(name = "hard1Score", precision = 10, scale = 5),
                @Column(name = "hard2Score", precision = 10, scale = 5),
                @Column(name = "soft0Score", precision = 10, scale = 5),
                @Column(name = "soft1Score", precision = 10, scale = 5)})
        public BendableBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(BendableBigDecimalScore score) {
            this.score = score;
        }

    }

}
