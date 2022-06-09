package org.optaplanner.persistence.jpa.api.score.buildin.simplelong;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

class SimpleLongScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new TestJpaEntity(SimpleLongScore.ZERO), null,
                SimpleLongScore.of(-10L),
                SimpleLongScore.ofUninitialized(-7, -10L));
    }

    @Entity
    public static class TestJpaEntity extends AbstractTestJpaEntity<SimpleLongScore> {

        @Convert(converter = SimpleLongScoreConverter.class)
        protected SimpleLongScore score;

        private TestJpaEntity() {
        }

        public TestJpaEntity(SimpleLongScore score) {
            this.score = score;
        }

        @Override
        public SimpleLongScore getScore() {
            return score;
        }

        @Override
        public void setScore(SimpleLongScore score) {
            this.score = score;
        }
    }
}
