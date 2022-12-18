package org.optaplanner.persistence.jpa.api.score.buildin.hardmediumsoft;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HardMediumSoftScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new HardMediumSoftScoreConverterTestJpaEntity(HardMediumSoftScore.ZERO), null,
                HardMediumSoftScore.of(-100, -20, -3),
                HardMediumSoftScore.ofUninitialized(-7, -100, -20, -3));
    }

    @Entity
    static class HardMediumSoftScoreConverterTestJpaEntity extends AbstractTestJpaEntity<HardMediumSoftScore> {

        @Convert(converter = HardMediumSoftScoreConverter.class)
        protected HardMediumSoftScore score;

        HardMediumSoftScoreConverterTestJpaEntity() {
        }

        public HardMediumSoftScoreConverterTestJpaEntity(HardMediumSoftScore score) {
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
