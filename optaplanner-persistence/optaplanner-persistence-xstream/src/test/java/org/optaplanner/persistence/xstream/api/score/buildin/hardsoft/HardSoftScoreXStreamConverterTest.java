package org.optaplanner.persistence.xstream.api.score.buildin.hardsoft;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverterTest;

import com.thoughtworks.xstream.annotations.XStreamConverter;

class HardSoftScoreXStreamConverterTest extends AbstractScoreXStreamConverterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardSoftScoreWrapper(null));
        HardSoftScore score = HardSoftScore.of(1200, 34);
        assertSerializeAndDeserialize(score, new TestHardSoftScoreWrapper(score));
        score = HardSoftScore.ofUninitialized(-7, 1200, 34);
        assertSerializeAndDeserialize(score, new TestHardSoftScoreWrapper(score));
    }

    public static class TestHardSoftScoreWrapper extends TestScoreWrapper<HardSoftScore> {

        @XStreamConverter(HardSoftScoreXStreamConverter.class)
        private HardSoftScore score;

        public TestHardSoftScoreWrapper(HardSoftScore score) {
            this.score = score;
        }

        @Override
        public HardSoftScore getScore() {
            return score;
        }

    }

}
