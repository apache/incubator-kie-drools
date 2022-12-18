package org.optaplanner.persistence.jpa.api.score.buildin.simple;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.persistence.jpa.impl.AbstractScoreJpaTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SimpleScoreConverterTest extends AbstractScoreJpaTest {

    @Test
    void persistAndMerge() {
        persistAndMerge(new SimpleScoreConverterTestJpaEntity(SimpleScore.ZERO), null,
                SimpleScore.of(-10),
                SimpleScore.ofUninitialized(-7, -10));
    }

    @Entity
    static class SimpleScoreConverterTestJpaEntity extends AbstractTestJpaEntity<SimpleScore> {

        @Convert(converter = SimpleScoreConverter.class)
        protected SimpleScore score;

        SimpleScoreConverterTestJpaEntity() {
        }

        public SimpleScoreConverterTestJpaEntity(SimpleScore score) {
            this.score = score;
        }

        @Override
        public SimpleScore getScore() {
            return score;
        }

        @Override
        public void setScore(SimpleScore score) {
            this.score = score;
        }
    }
}
