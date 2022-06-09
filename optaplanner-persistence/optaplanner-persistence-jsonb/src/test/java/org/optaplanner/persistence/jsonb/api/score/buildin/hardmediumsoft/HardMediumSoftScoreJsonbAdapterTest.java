package org.optaplanner.persistence.jsonb.api.score.buildin.hardmediumsoft;

import javax.json.bind.annotation.JsonbTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapterTest;

class HardMediumSoftScoreJsonbAdapterTest extends AbstractScoreJsonbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardMediumSoftScoreWrapper(null));
        HardMediumSoftScore score = HardMediumSoftScore.of(1200, 30, 4);
        assertSerializeAndDeserialize(score, new TestHardMediumSoftScoreWrapper(score));
        score = HardMediumSoftScore.ofUninitialized(-7, 1200, 30, 4);
        assertSerializeAndDeserialize(score, new TestHardMediumSoftScoreWrapper(score));
    }

    public static class TestHardMediumSoftScoreWrapper extends TestScoreWrapper<HardMediumSoftScore> {

        @JsonbTypeAdapter(HardMediumSoftScoreJsonbAdapter.class)
        private HardMediumSoftScore score;

        // Empty constructor required by JSON-B
        @SuppressWarnings("unused")
        public TestHardMediumSoftScoreWrapper() {
        }

        public TestHardMediumSoftScoreWrapper(HardMediumSoftScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardMediumSoftScore score) {
            this.score = score;
        }

    }
}
