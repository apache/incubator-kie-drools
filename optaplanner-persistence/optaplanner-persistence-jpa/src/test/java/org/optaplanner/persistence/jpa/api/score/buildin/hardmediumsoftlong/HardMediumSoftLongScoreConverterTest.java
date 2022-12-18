package org.optaplanner.persistence.jpa.api.score.buildin.hardmediumsoftlong;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HardMediumSoftLongScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new HardMediumSoftLongScoreConverterTestJpaEntity(HardMediumSoftLongScore.ZERO), null,
                HardMediumSoftLongScore.of(-100L, -20L, -3L),
                HardMediumSoftLongScore.ofUninitialized(-7, -100L, -20L, -3L));
    }

    @Entity
    static class HardMediumSoftLongScoreConverterTestJpaEntity
            extends AbstractTestJpaEntity<HardMediumSoftLongScore> {

        @Convert(converter = HardMediumSoftLongScoreConverter.class)
        protected HardMediumSoftLongScore score;

        HardMediumSoftLongScoreConverterTestJpaEntity() {
        }

        public HardMediumSoftLongScoreConverterTestJpaEntity(HardMediumSoftLongScore score) {
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
