package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoft;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class HardSoftScoreHibernateTypeTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(HardSoftScore.ZERO),
                HardSoftScore.of(-10, -2),
                HardSoftScore.ofUninitialized(-7, -10, -2));
    }

    @Entity
    @TypeDef(defaultForType = HardSoftScore.class, typeClass = HardSoftScoreHibernateType.class)
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardSoftScore> {

        @Columns(columns = { @Column(name = "initScore"), @Column(name = "hardScore"), @Column(name = "softScore") })
        protected HardSoftScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(HardSoftScore score) {
            this.score = score;
        }

        @Override
        public HardSoftScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardSoftScore score) {
            this.score = score;
        }

    }

}
