package org.optaplanner.persistence.jaxb.api.score.buildin.simple;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapterTest;

class SimpleScoreJaxbAdapterTest extends AbstractScoreJaxbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestSimpleScoreWrapper(null));

        SimpleScore score = SimpleScore.of(1234);
        assertSerializeAndDeserialize(score, new TestSimpleScoreWrapper(score));

        score = SimpleScore.ofUninitialized(-7, 1234);
        assertSerializeAndDeserialize(score, new TestSimpleScoreWrapper(score));
    }

    @XmlRootElement
    public static class TestSimpleScoreWrapper extends TestScoreWrapper<SimpleScore> {

        @XmlJavaTypeAdapter(SimpleScoreJaxbAdapter.class)
        private SimpleScore score;

        @SuppressWarnings("unused")
        private TestSimpleScoreWrapper() {
        }

        public TestSimpleScoreWrapper(SimpleScore score) {
            this.score = score;
        }

        @Override
        public SimpleScore getScore() {
            return score;
        }

    }

}
