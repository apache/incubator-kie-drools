package org.optaplanner.persistence.jpa.api.score.buildin.bendable;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class BendableScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new BendableScoreConverterTestJpaEntity(BendableScore.zero(3, 2)), null,
                BendableScore.of(new int[] { 10000, 2000, 300 }, new int[] { 40, 5 }),
                BendableScore.ofUninitialized(-7, new int[] { 10000, 2000, 300 }, new int[] { 40, 5 }));
    }

    @Entity
    static class BendableScoreConverterTestJpaEntity extends AbstractTestJpaEntity<BendableScore> {

        @Convert(converter = BendableScoreConverter.class)
        protected BendableScore score;

        BendableScoreConverterTestJpaEntity() {
        }

        public BendableScoreConverterTestJpaEntity(BendableScore score) {
            this.score = score;
        }

        @Override
        public BendableScore getScore() {
            return score;
        }

        @Override
        public void setScore(BendableScore score) {
            this.score = score;
        }
    }
}
