package org.optaplanner.persistence.jpa.api.score.buildin.bendablelong;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class BendableLongScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new BendaleLongScoreConverterTestJpaEntity(BendableLongScore.zero(3, 2)), null,
                BendableLongScore.of(new long[] { 10000L, 2000L, 300L }, new long[] { 40L, 5L }),
                BendableLongScore.ofUninitialized(-7, new long[] { 10000L, 2000L, 300L }, new long[] { 40L, 5L }));
    }

    @Entity
    static class BendaleLongScoreConverterTestJpaEntity extends AbstractTestJpaEntity<BendableLongScore> {

        @Convert(converter = BendableLongScoreConverter.class)
        protected BendableLongScore score;

        BendaleLongScoreConverterTestJpaEntity() {
        }

        public BendaleLongScoreConverterTestJpaEntity(BendableLongScore score) {
            this.score = score;
        }

        @Override
        public BendableLongScore getScore() {
            return score;
        }

        @Override
        public void setScore(BendableLongScore score) {
            this.score = score;
        }
    }
}
