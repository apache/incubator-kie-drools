package org.optaplanner.persistence.xstream.api.score.buildin.hardsoftlong;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverterTest;

import com.thoughtworks.xstream.annotations.XStreamConverter;

class HardSoftLongScoreXStreamConverterTest extends AbstractScoreXStreamConverterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardSoftLongScoreWrapper(null));
        HardSoftLongScore score = HardSoftLongScore.of(1200L, 34L);
        assertSerializeAndDeserialize(score, new TestHardSoftLongScoreWrapper(score));
        score = HardSoftLongScore.ofUninitialized(-7, 1200L, 34L);
        assertSerializeAndDeserialize(score, new TestHardSoftLongScoreWrapper(score));
    }

    public static class TestHardSoftLongScoreWrapper extends TestScoreWrapper<HardSoftLongScore> {

        @XStreamConverter(HardSoftLongScoreXStreamConverter.class)
        private HardSoftLongScore score;

        public TestHardSoftLongScoreWrapper(HardSoftLongScore score) {
            this.score = score;
        }

        @Override
        public HardSoftLongScore getScore() {
            return score;
        }

    }

}
