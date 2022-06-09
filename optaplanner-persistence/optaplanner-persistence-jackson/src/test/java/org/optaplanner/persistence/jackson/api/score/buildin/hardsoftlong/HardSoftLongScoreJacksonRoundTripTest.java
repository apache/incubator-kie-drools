package org.optaplanner.persistence.jackson.api.score.buildin.hardsoftlong;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonRoundTripTest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

class HardSoftLongScoreJacksonRoundTripTest extends AbstractScoreJacksonRoundTripTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardSoftLongScoreWrapper(null));
        HardSoftLongScore score = HardSoftLongScore.of(1200L, 34L);
        assertSerializeAndDeserialize(score, new TestHardSoftLongScoreWrapper(score));
        score = HardSoftLongScore.ofUninitialized(-7, 1200L, 34L);
        assertSerializeAndDeserialize(score, new TestHardSoftLongScoreWrapper(score));
    }

    public static class TestHardSoftLongScoreWrapper extends TestScoreWrapper<HardSoftLongScore> {

        @JsonSerialize(using = HardSoftLongScoreJacksonSerializer.class)
        @JsonDeserialize(using = HardSoftLongScoreJacksonDeserializer.class)
        private HardSoftLongScore score;

        @SuppressWarnings("unused")
        private TestHardSoftLongScoreWrapper() {
        }

        public TestHardSoftLongScoreWrapper(HardSoftLongScore score) {
            this.score = score;
        }

        @Override
        public HardSoftLongScore getScore() {
            return score;
        }

    }

}
