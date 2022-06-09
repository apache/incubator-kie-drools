package org.optaplanner.persistence.jpa.api.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class HardSoftBigDecimalScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(HardSoftBigDecimalScore.ZERO), null,
                HardSoftBigDecimalScore.of(new BigDecimal("-10.01000"), new BigDecimal("-2.20000")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.01000"), new BigDecimal("-2.20000")));
    }

    @Entity
    public static class TestJpaEntity extends AbstractTestJpaEntity<HardSoftBigDecimalScore> {

        @Convert(converter = HardSoftBigDecimalScoreConverter.class)
        protected HardSoftBigDecimalScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(HardSoftBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public HardSoftBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardSoftBigDecimalScore score) {
            this.score = score;
        }
    }
}
