package org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverterTest;

import com.thoughtworks.xstream.annotations.XStreamConverter;

class HardMediumSoftLongScoreXStreamConverterTest extends AbstractScoreXStreamConverterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardMediumSoftLongScoreWrapper(null));
        HardMediumSoftLongScore score = HardMediumSoftLongScore.of(1200L, 30L, 4L);
        assertSerializeAndDeserialize(score, new TestHardMediumSoftLongScoreWrapper(score));
        score = HardMediumSoftLongScore.ofUninitialized(-7, 1200L, 30L, 4L);
        assertSerializeAndDeserialize(score, new TestHardMediumSoftLongScoreWrapper(score));
    }

    public static class TestHardMediumSoftLongScoreWrapper extends TestScoreWrapper<HardMediumSoftLongScore> {

        @XStreamConverter(HardMediumSoftLongScoreXStreamConverter.class)
        private HardMediumSoftLongScore score;

        public TestHardMediumSoftLongScoreWrapper(HardMediumSoftLongScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftLongScore getScore() {
            return score;
        }

    }

}
