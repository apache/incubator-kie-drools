package org.optaplanner.persistence.jaxb.api.score.buildin.simplelong;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapterTest;

class SimpleLongScoreJaxbAdapterTest extends AbstractScoreJaxbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestSimpleLongScoreWrapper(null));

        SimpleLongScore score = SimpleLongScore.of(1234L);
        assertSerializeAndDeserialize(score, new TestSimpleLongScoreWrapper(score));

        score = SimpleLongScore.ofUninitialized(-7, 1234L);
        assertSerializeAndDeserialize(score, new TestSimpleLongScoreWrapper(score));
    }

    @XmlRootElement
    public static class TestSimpleLongScoreWrapper extends AbstractScoreJaxbAdapterTest.TestScoreWrapper<SimpleLongScore> {

        @XmlJavaTypeAdapter(SimpleLongScoreJaxbAdapter.class)
        private SimpleLongScore score;

        @SuppressWarnings("unused")
        private TestSimpleLongScoreWrapper() {
        }

        public TestSimpleLongScoreWrapper(SimpleLongScore score) {
            this.score = score;
        }

        @Override
        public SimpleLongScore getScore() {
            return score;
        }

    }

}
