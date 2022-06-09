package org.optaplanner.persistence.jackson.api.score.buildin.bendablelong;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonRoundTripTest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

class BendableLongScoreJacksonRoundTripTest extends AbstractScoreJacksonRoundTripTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestBendableLongScoreWrapper(null));
        BendableLongScore score = BendableLongScore.of(new long[] { 1000L, 200L }, new long[] { 34L });
        assertSerializeAndDeserialize(score, new TestBendableLongScoreWrapper(score));
        score = BendableLongScore.ofUninitialized(-7, new long[] { 1000L, 200L }, new long[] { 34L });
        assertSerializeAndDeserialize(score, new TestBendableLongScoreWrapper(score));
    }

    public static class TestBendableLongScoreWrapper extends TestScoreWrapper<BendableLongScore> {

        @JsonSerialize(using = BendableLongScoreJacksonSerializer.class)
        @JsonDeserialize(using = BendableLongScoreJacksonDeserializer.class)
        private BendableLongScore score;

        @SuppressWarnings("unused")
        private TestBendableLongScoreWrapper() {
        }

        public TestBendableLongScoreWrapper(BendableLongScore score) {
            this.score = score;
        }

        @Override
        public BendableLongScore getScore() {
            return score;
        }

    }

}
