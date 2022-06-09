package org.optaplanner.persistence.jsonb.api.score.buildin.bendable;

import javax.json.bind.annotation.JsonbTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapterTest;

class BendableScoreJsonbAdapterTest extends AbstractScoreJsonbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestBendableScoreWrapper(null));
        BendableScore score = BendableScore.of(new int[] { 1000, 200 }, new int[] { 34 });
        assertSerializeAndDeserialize(score, new TestBendableScoreWrapper(score));
        score = BendableScore.ofUninitialized(-7, new int[] { 1000, 200 }, new int[] { 34 });
        assertSerializeAndDeserialize(score, new TestBendableScoreWrapper(score));
    }

    public static class TestBendableScoreWrapper extends TestScoreWrapper<BendableScore> {

        @JsonbTypeAdapter(BendableScoreJsonbAdapter.class)
        private BendableScore score;

        // Empty constructor required by JSON-B
        @SuppressWarnings("unused")
        public TestBendableScoreWrapper() {
        }

        public TestBendableScoreWrapper(BendableScore score) {
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
