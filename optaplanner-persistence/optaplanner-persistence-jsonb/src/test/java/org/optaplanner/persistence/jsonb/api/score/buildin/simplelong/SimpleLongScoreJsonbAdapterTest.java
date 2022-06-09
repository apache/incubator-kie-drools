package org.optaplanner.persistence.jsonb.api.score.buildin.simplelong;

import javax.json.bind.annotation.JsonbTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapterTest;

class SimpleLongScoreJsonbAdapterTest extends AbstractScoreJsonbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestSimpleLongScoreWrapper(null));
        SimpleLongScore score = SimpleLongScore.of(1234L);
        assertSerializeAndDeserialize(score, new TestSimpleLongScoreWrapper(score));
        score = SimpleLongScore.ofUninitialized(-7, 1234L);
        assertSerializeAndDeserialize(score, new TestSimpleLongScoreWrapper(score));
    }

    public static class TestSimpleLongScoreWrapper extends TestScoreWrapper<SimpleLongScore> {

        @JsonbTypeAdapter(SimpleLongScoreJsonbAdapter.class)
        private SimpleLongScore score;

        // Empty constructor required by JSON-B
        @SuppressWarnings("unused")
        public TestSimpleLongScoreWrapper() {
        }

        public TestSimpleLongScoreWrapper(SimpleLongScore score) {
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
