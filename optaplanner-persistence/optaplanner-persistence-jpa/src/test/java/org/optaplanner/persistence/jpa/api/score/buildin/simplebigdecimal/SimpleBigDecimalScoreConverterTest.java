package org.optaplanner.persistence.jpa.api.score.buildin.simplebigdecimal;

import java.math.BigDecimal;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class SimpleBigDecimalScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(SimpleBigDecimalScore.ZERO), null,
                SimpleBigDecimalScore.of(new BigDecimal("-10.01000")),
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.01000")));
    }

    @Entity
    public static class TestJpaEntity extends AbstractTestJpaEntity<SimpleBigDecimalScore> {

        @Convert(converter = SimpleBigDecimalScoreConverter.class)
        protected SimpleBigDecimalScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(SimpleBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public SimpleBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(SimpleBigDecimalScore score) {
            this.score = score;
        }
    }
}
