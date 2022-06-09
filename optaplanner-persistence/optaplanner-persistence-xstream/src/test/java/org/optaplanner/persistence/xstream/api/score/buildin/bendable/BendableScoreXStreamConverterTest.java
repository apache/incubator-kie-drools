package org.optaplanner.persistence.xstream.api.score.buildin.bendable;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverterTest;

import com.thoughtworks.xstream.annotations.XStreamConverter;

class BendableScoreXStreamConverterTest extends AbstractScoreXStreamConverterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestBendableScoreWrapper(null));
        BendableScore score = BendableScore.of(new int[] { 1000, 200 }, new int[] { 34 });
        assertSerializeAndDeserialize(score, new TestBendableScoreWrapper(score));
        score = BendableScore.ofUninitialized(-7, new int[] { 1000, 200 }, new int[] { 34 });
        assertSerializeAndDeserialize(score, new TestBendableScoreWrapper(score));
    }

    public static class TestBendableScoreWrapper extends TestScoreWrapper<BendableScore> {

        @XStreamConverter(BendableScoreXStreamConverter.class)
        private BendableScore score;

        public TestBendableScoreWrapper(BendableScore score) {
            this.score = score;
        }

        @Override
        public BendableScore getScore() {
            return score;
        }

    }

}
