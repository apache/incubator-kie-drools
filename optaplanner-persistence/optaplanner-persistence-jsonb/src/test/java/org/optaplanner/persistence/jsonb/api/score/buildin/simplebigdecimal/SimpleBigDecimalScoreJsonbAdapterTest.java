package org.optaplanner.persistence.jsonb.api.score.buildin.simplebigdecimal;

import java.math.BigDecimal;

import javax.json.bind.annotation.JsonbTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapterTest;

class SimpleBigDecimalScoreJsonbAdapterTest extends AbstractScoreJsonbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestSimpleBigDecimalScoreWrapper(null));
        SimpleBigDecimalScore score = SimpleBigDecimalScore.of(new BigDecimal("1234.4321"));
        assertSerializeAndDeserialize(score, new TestSimpleBigDecimalScoreWrapper(score));
        score = SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("1234.4321"));
        assertSerializeAndDeserialize(score, new TestSimpleBigDecimalScoreWrapper(score));
    }

    public static class TestSimpleBigDecimalScoreWrapper extends TestScoreWrapper<SimpleBigDecimalScore> {

        @JsonbTypeAdapter(SimpleBigDecimalScoreJsonbAdapter.class)
        private SimpleBigDecimalScore score;

        // Empty constructor required by JSON-B
        @SuppressWarnings("unused")
        public TestSimpleBigDecimalScoreWrapper() {
        }

        public TestSimpleBigDecimalScoreWrapper(SimpleBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public SimpleBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(SimpleBigDecimalScore score) {
            this.score = score;
        }

    }
}
