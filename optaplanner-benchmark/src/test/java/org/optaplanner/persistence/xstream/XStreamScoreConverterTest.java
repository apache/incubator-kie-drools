package org.optaplanner.persistence.xstream;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.util.SerializationTestUtils;

import static org.junit.Assert.assertEquals;

public class XStreamScoreConverterTest {

    @Test
    public void serializeAndDeserializeWithNullField() {
        XStreamScoreConverterTestObject input = new XStreamScoreConverterTestObject(null);
        SerializationTestUtils.serializeAndDeserializeWithAll(input,
                new SerializationTestUtils.OutputAsserter<XStreamScoreConverterTestObject>() {
                    public void assertOutput(XStreamScoreConverterTestObject output) {
                        assertEquals(null, output.getScore());
                    }
                }
        );
    }

    @Test
    public void serializeAndDeserialize() {
        XStreamScoreConverterTestObject input = new XStreamScoreConverterTestObject(SimpleScore.valueOf(123));
        SerializationTestUtils.serializeAndDeserializeWithAll(input,
                new SerializationTestUtils.OutputAsserter<XStreamScoreConverterTestObject>() {
                    public void assertOutput(XStreamScoreConverterTestObject output) {
                        assertEquals(123, output.getScore().getScore());
                    }
                }
        );
    }

    public static class XStreamScoreConverterTestObject implements Serializable {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {SimpleScoreDefinition.class})
        private SimpleScore score;

        public XStreamScoreConverterTestObject(SimpleScore score) {
            this.score = score;
        }

        public SimpleScore getScore() {
            return score;
        }

        public void setScore(SimpleScore score) {
            this.score = score;
        }
    }

}
