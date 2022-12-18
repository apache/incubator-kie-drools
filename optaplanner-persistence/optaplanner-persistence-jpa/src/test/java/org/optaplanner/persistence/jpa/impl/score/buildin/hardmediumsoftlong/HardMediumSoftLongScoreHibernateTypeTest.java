package org.optaplanner.persistence.jpa.impl.score.buildin.hardmediumsoftlong;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.TypeDef;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HardMediumSoftLongScoreHibernateTypeTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new HardMediumSoftLongScoreHibernateTypeTestJpaEntity(HardMediumSoftLongScore.ZERO),
                HardMediumSoftLongScore.of(-100L, -20L, -3L),
                HardMediumSoftLongScore.ofUninitialized(-7, -100L, -20L, -3L));
    }

    @Entity
    @TypeDef(defaultForType = HardMediumSoftLongScore.class, typeClass = HardMediumSoftLongScoreHibernateType.class)
    static class HardMediumSoftLongScoreHibernateTypeTestJpaEntity
            extends AbstractTestJpaEntity<HardMediumSoftLongScore> {

        @Columns(columns = { @Column(name = "initScore"),
                @Column(name = "hardScore"), @Column(name = "mediumScore"), @Column(name = "softScore") })
        protected HardMediumSoftLongScore score;

        HardMediumSoftLongScoreHibernateTypeTestJpaEntity() {
        }

        public HardMediumSoftLongScoreHibernateTypeTestJpaEntity(HardMediumSoftLongScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftLongScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardMediumSoftLongScore score) {
            this.score = score;
        }

    }

}
