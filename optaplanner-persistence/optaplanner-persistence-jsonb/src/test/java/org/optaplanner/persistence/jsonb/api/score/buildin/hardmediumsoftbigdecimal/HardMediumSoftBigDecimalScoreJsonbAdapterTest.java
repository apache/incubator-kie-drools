package org.optaplanner.persistence.jsonb.api.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;

import javax.json.bind.annotation.JsonbTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapterTest;

class HardMediumSoftBigDecimalScoreJsonbAdapterTest extends AbstractScoreJsonbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardMediumSoftBigDecimalScoreWrapper(null));
        HardMediumSoftBigDecimalScore score = HardMediumSoftBigDecimalScore.of(new BigDecimal("1200.0021"),
                new BigDecimal("-3.1415"),
                new BigDecimal("34.4300"));
        assertSerializeAndDeserialize(score, new TestHardMediumSoftBigDecimalScoreWrapper(score));
        score = HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("1200.0021"),
                new BigDecimal("-3.1415"),
                new BigDecimal("34.4300"));
        assertSerializeAndDeserialize(score, new TestHardMediumSoftBigDecimalScoreWrapper(score));
    }

    public static class TestHardMediumSoftBigDecimalScoreWrapper extends TestScoreWrapper<HardMediumSoftBigDecimalScore> {

        @JsonbTypeAdapter(HardMediumSoftBigDecimalScoreJsonbAdapter.class)
        private HardMediumSoftBigDecimalScore score;

        // Empty constructor required by JSON-B
        @SuppressWarnings("unused")
        public TestHardMediumSoftBigDecimalScoreWrapper() {
        }

        public TestHardMediumSoftBigDecimalScoreWrapper(HardMediumSoftBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftBigDecimalScore getScore() {
            return score;
        }

        @Override
        public void setScore(HardMediumSoftBigDecimalScore score) {
            this.score = score;
        }

    }
}
