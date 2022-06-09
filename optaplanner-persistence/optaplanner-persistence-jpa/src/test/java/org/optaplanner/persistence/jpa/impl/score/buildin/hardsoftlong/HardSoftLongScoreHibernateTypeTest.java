package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoftlong;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class HardSoftLongScoreHibernateTypeTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(HardSoftLongScore.ZERO),
                HardSoftLongScore.of(-10L, -2L),
                HardSoftLongScore.ofUninitialized(-7, -10L, -2L));
    }

    @Entity
    @TypeDef(defaultForType = HardSoftLongScore.class, typeClass = HardSoftLongScoreHibernateType.class)
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardSoftLongScore> {

        @Columns(columns = { @Column(name = "initScore"), @Column(name = "hardScore"), @Column(name = "softScore") })
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
