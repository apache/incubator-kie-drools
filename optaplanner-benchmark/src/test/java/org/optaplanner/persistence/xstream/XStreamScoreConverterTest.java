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
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.testdata.util.SerializationTestUtils;
import org.optaplanner.persistence.xstream.impl.score.XStreamScoreConverter;

import static org.junit.Assert.*;

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
