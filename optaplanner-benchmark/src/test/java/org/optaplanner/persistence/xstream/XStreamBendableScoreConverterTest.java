/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.persistence.xstream;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.testdata.util.SerializationTestUtils;
import org.optaplanner.persistence.xstream.impl.score.XStreamBendableScoreConverter;

import static org.junit.Assert.*;

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
                        assertEquals(1, score.getHardLevelsSize());
                        assertEquals(-5, score.getHardScore(0));
                        assertEquals(2, score.getSoftLevelsSize());
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
