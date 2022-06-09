package org.optaplanner.persistence.jpa.api.score.buildin.hardsoft;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class HardSoftScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(HardSoftScore.ZERO), null,
                HardSoftScore.of(-10, -2),
                HardSoftScore.ofUninitialized(-7, -10, -2));
    }

    @Entity
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardSoftScore> {

        @Convert(converter = HardSoftScoreConverter.class)
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
