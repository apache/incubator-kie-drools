package org.optaplanner.persistence.jaxb.api.score.buildin.bendable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapterTest;

class BendableScoreJaxbAdapterTest extends AbstractScoreJaxbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestBendableScoreWrapper(null));

        BendableScore score = BendableScore.of(new int[] { 1000, 200 }, new int[] { 34 });
        assertSerializeAndDeserialize(score, new TestBendableScoreWrapper(score));

        score = BendableScore.ofUninitialized(-7, new int[] { 1000, 200 }, new int[] { 34 });
        assertSerializeAndDeserialize(score, new TestBendableScoreWrapper(score));
    }

    @XmlRootElement
    public static class TestBendableScoreWrapper extends TestScoreWrapper<BendableScore> {

        @XmlJavaTypeAdapter(BendableScoreJaxbAdapter.class)
        private BendableScore score;

        @SuppressWarnings("unused")
        private TestBendableScoreWrapper() {
        }

        public TestBendableScoreWrapper(BendableScore score) {
            this.score = score;
        }

        @Override
        public BendableScore getScore() {
            return score;
        }

    }

}
