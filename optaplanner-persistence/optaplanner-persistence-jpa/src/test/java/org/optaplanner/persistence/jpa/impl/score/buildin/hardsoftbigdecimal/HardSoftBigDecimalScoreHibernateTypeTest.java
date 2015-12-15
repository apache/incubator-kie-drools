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

package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateTypeTest;

import static org.junit.Assert.*;

public class HardSoftBigDecimalScoreHibernateTypeTest extends AbstractScoreHibernateTypeTest {

    @Test
    public void persistAndMerge() {
        Long id = persistAndAssert(new TestJpaEntity(null));
        findAssertAndChangeScore(TestJpaEntity.class, id, null,
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-10.01000"), new BigDecimal("-2.20000")));
        findAndAssert(TestJpaEntity.class, id,
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-10.01000"), new BigDecimal("-2.20000")));
    }

    @Entity
    @TypeDef(defaultForType = HardSoftBigDecimalScore.class, typeClass = HardSoftBigDecimalScoreHibernateType.class)
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardSoftBigDecimalScore> {

        protected HardSoftBigDecimalScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(HardSoftBigDecimalScore score) {
            this.score = score;
        }

        @Columns(columns = {
                @Column(name = "hardScore", precision = 10, scale = 5),
                @Column(name = "softScore", precision = 10, scale = 5)})
        public HardSoftBigDecimalScore getScore() {
            return score;
        }

        public void setScore(HardSoftBigDecimalScore score) {
            this.score = score;
        }

    }

}
