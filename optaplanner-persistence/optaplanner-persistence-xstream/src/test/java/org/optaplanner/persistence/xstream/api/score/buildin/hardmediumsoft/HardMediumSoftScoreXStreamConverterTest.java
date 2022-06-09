package org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoft;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverterTest;

import com.thoughtworks.xstream.annotations.XStreamConverter;

class HardMediumSoftScoreXStreamConverterTest extends AbstractScoreXStreamConverterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardMediumSoftScoreWrapper(null));
        HardMediumSoftScore score = HardMediumSoftScore.of(1200, 30, 4);
        assertSerializeAndDeserialize(score, new TestHardMediumSoftScoreWrapper(score));
        score = HardMediumSoftScore.ofUninitialized(-7, 1200, 30, 4);
        assertSerializeAndDeserialize(score, new TestHardMediumSoftScoreWrapper(score));
    }

    public static class TestHardMediumSoftScoreWrapper extends TestScoreWrapper<HardMediumSoftScore> {

        @XStreamConverter(HardMediumSoftScoreXStreamConverter.class)
        private HardMediumSoftScore score;

        public TestHardMediumSoftScoreWrapper(HardMediumSoftScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftScore getScore() {
            return score;
        }

    }

}
