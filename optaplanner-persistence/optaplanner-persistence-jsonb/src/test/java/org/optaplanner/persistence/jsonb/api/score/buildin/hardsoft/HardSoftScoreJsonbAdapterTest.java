package org.optaplanner.persistence.jsonb.api.score.buildin.hardsoft;

import javax.json.bind.annotation.JsonbTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapterTest;

class HardSoftScoreJsonbAdapterTest extends AbstractScoreJsonbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardSoftScoreWrapper(null));
        HardSoftScore score = HardSoftScore.of(1200, 34);
        assertSerializeAndDeserialize(score, new TestHardSoftScoreWrapper(score));
        score = HardSoftScore.ofUninitialized(-7, 1200, 34);
        assertSerializeAndDeserialize(score, new TestHardSoftScoreWrapper(score));
    }

    public static class TestHardSoftScoreWrapper extends TestScoreWrapper<HardSoftScore> {

        @JsonbTypeAdapter(HardSoftScoreJsonbAdapter.class)
        private HardSoftScore score;

        // Empty constructor required by JSON-B
        @SuppressWarnings("unused")
        public TestHardSoftScoreWrapper() {
        }

        public TestHardSoftScoreWrapper(HardSoftScore score) {
            this.score = score;
        }

        @Override
        public HardSoftScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardSoftScore score) {
            this.score = score;
        }

    }
}
