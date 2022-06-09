package org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverterTest;

import com.thoughtworks.xstream.annotations.XStreamConverter;

class HardMediumSoftBigDecimalScoreXStreamConverterTest extends AbstractScoreXStreamConverterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardMediumSoftBigDecimalScoreWrapper(null));
        HardMediumSoftBigDecimalScore score = HardMediumSoftBigDecimalScore.of(new BigDecimal("1200.0021"),
                new BigDecimal("-3.1415"), new BigDecimal("34.4300"));
        assertSerializeAndDeserialize(score, new TestHardMediumSoftBigDecimalScoreWrapper(score));
        score = HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("1200.0021"), new BigDecimal("-3.1415"),
                new BigDecimal("34.4300"));
        assertSerializeAndDeserialize(score, new TestHardMediumSoftBigDecimalScoreWrapper(score));
    }

    public static class TestHardMediumSoftBigDecimalScoreWrapper extends TestScoreWrapper<HardMediumSoftBigDecimalScore> {

        @XStreamConverter(HardMediumSoftBigDecimalScoreXStreamConverter.class)
        private final HardMediumSoftBigDecimalScore score;

        public TestHardMediumSoftBigDecimalScoreWrapper(HardMediumSoftBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftBigDecimalScore getScore() {
            return score;
        }

    }

}
