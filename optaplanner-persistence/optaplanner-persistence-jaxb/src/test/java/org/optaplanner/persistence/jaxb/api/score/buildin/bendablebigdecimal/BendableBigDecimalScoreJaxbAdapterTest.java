package org.optaplanner.persistence.jaxb.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapterTest;

class BendableBigDecimalScoreJaxbAdapterTest extends AbstractScoreJaxbAdapterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestBendableBigDecimalScoreWrapper(null));

        BendableBigDecimalScore score = BendableBigDecimalScore.of(
                new BigDecimal[] { new BigDecimal("1000.0001"), new BigDecimal("200.0020") },
                new BigDecimal[] { new BigDecimal("34.4300") });
        assertSerializeAndDeserialize(score, new TestBendableBigDecimalScoreWrapper(score));

        score = BendableBigDecimalScore.ofUninitialized(-7,
                new BigDecimal[] { new BigDecimal("1000.0001"), new BigDecimal("200.0020") },
                new BigDecimal[] { new BigDecimal("34.4300") });
        assertSerializeAndDeserialize(score, new TestBendableBigDecimalScoreWrapper(score));
    }

    @XmlRootElement
    public static class TestBendableBigDecimalScoreWrapper extends TestScoreWrapper<BendableBigDecimalScore> {

        @XmlJavaTypeAdapter(BendableBigDecimalScoreJaxbAdapter.class)
        private BendableBigDecimalScore score;

        @SuppressWarnings("unused")
        private TestBendableBigDecimalScoreWrapper() {
        }

        public TestBendableBigDecimalScoreWrapper(BendableBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public BendableBigDecimalScore getScore() {
            return score;
        }

    }

}
