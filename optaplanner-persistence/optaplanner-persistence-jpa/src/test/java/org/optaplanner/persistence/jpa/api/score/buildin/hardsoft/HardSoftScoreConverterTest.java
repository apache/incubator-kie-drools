package org.optaplanner.persistence.jpa.api.score.buildin.hardsoft;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HardSoftScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new HardSoftScoreConverterTestJpaEntity(HardSoftScore.ZERO), null,
                HardSoftScore.of(-10, -2),
                HardSoftScore.ofUninitialized(-7, -10, -2));
    }

    @Entity
    static class HardSoftScoreConverterTestJpaEntity extends AbstractTestJpaEntity<HardSoftScore> {

        @Convert(converter = HardSoftScoreConverter.class)
        protected HardSoftScore score;

        HardSoftScoreConverterTestJpaEntity() {
        }

        public HardSoftScoreConverterTestJpaEntity(HardSoftScore score) {
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
