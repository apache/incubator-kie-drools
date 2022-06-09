package org.optaplanner.persistence.jpa.api.score.buildin.hardsoftlong;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class HardSoftLongScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(HardSoftLongScore.ZERO), null,
                HardSoftLongScore.of(-10L, -2L),
                HardSoftLongScore.ofUninitialized(-7, -10L, -2L));
    }

    @Entity
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardSoftLongScore> {

        @Convert(converter = HardSoftLongScoreConverter.class)
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
