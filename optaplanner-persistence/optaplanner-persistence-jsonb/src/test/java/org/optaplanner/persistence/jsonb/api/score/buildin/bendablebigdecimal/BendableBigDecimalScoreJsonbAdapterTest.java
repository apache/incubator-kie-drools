package org.optaplanner.persistence.jsonb.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;

import javax.json.bind.annotation.JsonbTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapterTest;

class BendableBigDecimalScoreJsonbAdapterTest extends AbstractScoreJsonbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestBendableBigDecimalScoreWrapper(null));
        BendableBigDecimalScore score = BendableBigDecimalScore.of(new BigDecimal[] { new BigDecimal("1000.0001"),
                new BigDecimal("200.0020") }, new BigDecimal[] { new BigDecimal("34.4300") });
        assertSerializeAndDeserialize(score, new TestBendableBigDecimalScoreWrapper(score));
        score = BendableBigDecimalScore.ofUninitialized(-7, new BigDecimal[] { new BigDecimal("1000.0001"),
                new BigDecimal("200.0020") }, new BigDecimal[] { new BigDecimal("34.4300") });
        assertSerializeAndDeserialize(score, new TestBendableBigDecimalScoreWrapper(score));
    }

    public static class TestBendableBigDecimalScoreWrapper extends TestScoreWrapper<BendableBigDecimalScore> {

        @JsonbTypeAdapter(BendableBigDecimalScoreJsonbAdapter.class)
        private BendableBigDecimalScore score;

        // Empty constructor required by JSON-B
        @SuppressWarnings("unused")
        public TestBendableBigDecimalScoreWrapper() {
        }

        public TestBendableBigDecimalScoreWrapper(BendableBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public BendableBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(BendableBigDecimalScore score) {
            this.score = score;
        }

    }
}
