package org.optaplanner.persistence.jpa.impl.score.buildin.hardmediumsoft;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class HardMediumSoftScoreHibernateTypeTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(HardMediumSoftScore.ZERO),
                HardMediumSoftScore.of(-100, -20, -3),
                HardMediumSoftScore.ofUninitialized(-7, -100, -20, -3));
    }

    @Entity
    @TypeDef(defaultForType = HardMediumSoftScore.class, typeClass = HardMediumSoftScoreHibernateType.class)
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardMediumSoftScore> {

        @Columns(columns = { @Column(name = "initScore"),
                @Column(name = "hardScore"), @Column(name = "mediumScore"), @Column(name = "softScore") })
        protected HardMediumSoftScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(HardMediumSoftScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardMediumSoftScore score) {
            this.score = score;
        }

    }

}
