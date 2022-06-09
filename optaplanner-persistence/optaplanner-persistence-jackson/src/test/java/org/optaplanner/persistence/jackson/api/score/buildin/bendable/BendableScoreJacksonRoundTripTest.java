package org.optaplanner.persistence.jackson.api.score.buildin.bendable;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonRoundTripTest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

class BendableScoreJacksonRoundTripTest extends AbstractScoreJacksonRoundTripTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestBendableScoreWrapper(null));
        BendableScore score = BendableScore.of(new int[] { 1000, 200 }, new int[] { 34 });
        assertSerializeAndDeserialize(score, new TestBendableScoreWrapper(score));
        score = BendableScore.ofUninitialized(-7, new int[] { 1000, 200 }, new int[] { 34 });
        assertSerializeAndDeserialize(score, new TestBendableScoreWrapper(score));
    }

    public static class TestBendableScoreWrapper extends TestScoreWrapper<BendableScore> {

        @JsonSerialize(using = BendableScoreJacksonSerializer.class)
        @JsonDeserialize(using = BendableScoreJacksonDeserializer.class)
        private BendableScore score;

        @SuppressWarnings("unused")
        private TestBendableScoreWrapper() {
        }

        public TestBendableScoreWrapper(BendableScore score) {
            this.score = score;
        }

        @Override
        public BendableScore getScore() {
            return score;
        }

    }

}
