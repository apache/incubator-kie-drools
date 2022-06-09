package org.optaplanner.persistence.xstream.api.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverterTest;

import com.thoughtworks.xstream.annotations.XStreamConverter;

class HardSoftBigDecimalScoreXStreamConverterTest extends AbstractScoreXStreamConverterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardSoftBigDecimalScoreWrapper(null));
        HardSoftBigDecimalScore score = HardSoftBigDecimalScore.of(new BigDecimal("1200.0021"), new BigDecimal("34.4300"));
        assertSerializeAndDeserialize(score, new TestHardSoftBigDecimalScoreWrapper(score));
        score = HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("1200.0021"), new BigDecimal("34.4300"));
        assertSerializeAndDeserialize(score, new TestHardSoftBigDecimalScoreWrapper(score));
    }

    public static class TestHardSoftBigDecimalScoreWrapper extends TestScoreWrapper<HardSoftBigDecimalScore> {

        @XStreamConverter(HardSoftBigDecimalScoreXStreamConverter.class)
        private HardSoftBigDecimalScore score;

        public TestHardSoftBigDecimalScoreWrapper(HardSoftBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public HardSoftBigDecimalScore getScore() {
            return score;
        }

    }

}
