package org.optaplanner.persistence.xstream;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.util.SerializationTestUtils;

import static org.junit.Assert.assertEquals;

public class XStreamBendableScoreConverterTest {

    @Test
    public void serializeAndDeserializeWithNullField() {
        XStreamBendableScoreConverterTestObject input = new XStreamBendableScoreConverterTestObject(null);
        SerializationTestUtils.serializeAndDeserializeWithAll(input,
                new SerializationTestUtils.OutputAsserter<XStreamBendableScoreConverterTestObject>() {
                    public void assertOutput(XStreamBendableScoreConverterTestObject output) {
                        assertEquals(null, output.getScore());
                    }
                }
        );
    }

    @Test
    public void serializeAndDeserialize() {
        XStreamBendableScoreConverterTestObject input = new XStreamBendableScoreConverterTestObject(
                BendableScore.valueOf(new int[]{-5}, new int[]{-300, -4000}));
        SerializationTestUtils.serializeAndDeserializeWithAll(input,
                new SerializationTestUtils.OutputAsserter<XStreamBendableScoreConverterTestObject>() {
                    public void assertOutput(XStreamBendableScoreConverterTestObject output) {
                        BendableScore score = output.getScore();
                        assertEquals(1, score.getHardLevelCount());
                        assertEquals(-5, score.getHardScore(0));
                        assertEquals(2, score.getSoftLevelCount());
                        assertEquals(-300, score.getSoftScore(0));
                        assertEquals(-4000, score.getSoftScore(1));
                    }
                }
        );
    }

    public static class XStreamBendableScoreConverterTestObject implements Serializable {

        @XStreamConverter(value = XStreamBendableScoreConverter.class, ints = {1, 2})
        private BendableScore score;

        public XStreamBendableScoreConverterTestObject(BendableScore score) {
            this.score = score;
        }

        public BendableScore getScore() {
            return score;
        }

        public void setScore(BendableScore score) {
            this.score = score;
        }
    }


}
