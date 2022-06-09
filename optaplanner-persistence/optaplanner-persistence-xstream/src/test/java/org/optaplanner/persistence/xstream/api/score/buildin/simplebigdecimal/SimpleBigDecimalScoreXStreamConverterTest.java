package org.optaplanner.persistence.xstream.api.score.buildin.simplebigdecimal;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverterTest;

import com.thoughtworks.xstream.annotations.XStreamConverter;

class SimpleBigDecimalScoreXStreamConverterTest extends AbstractScoreXStreamConverterTest {

    @Test
    void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestSimpleBigDecimalScoreWrapper(null));
        SimpleBigDecimalScore score = SimpleBigDecimalScore.of(new BigDecimal("1234.4321"));
        assertSerializeAndDeserialize(score, new TestSimpleBigDecimalScoreWrapper(score));
        score = SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("1234.4321"));
        assertSerializeAndDeserialize(score, new TestSimpleBigDecimalScoreWrapper(score));
    }

    public static class TestSimpleBigDecimalScoreWrapper extends TestScoreWrapper<SimpleBigDecimalScore> {

        @XStreamConverter(SimpleBigDecimalScoreXStreamConverter.class)
        private SimpleBigDecimalScore score;

        public TestSimpleBigDecimalScoreWrapper(SimpleBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public SimpleBigDecimalScore getScore() {
            return score;
        }

    }

}
