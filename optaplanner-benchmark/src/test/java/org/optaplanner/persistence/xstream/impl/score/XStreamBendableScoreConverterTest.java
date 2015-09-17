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

package org.optaplanner.persistence.xstream.impl.score;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class XStreamBendableScoreConverterTest {

    @Test
    public void serializeAndDeserializeWithNullField() {
        TestXStreamObject input = new TestXStreamObject(null);
        PlannerTestUtils.serializeAndDeserializeWithAll(input,
                new PlannerTestUtils.OutputAsserter<TestXStreamObject>() {
                    public void assertOutput(TestXStreamObject output) {
                        assertEquals(null, output.getScore());
                    }
                }
        );
    }

    @Test
    public void serializeAndDeserialize() {
        TestXStreamObject input = new TestXStreamObject(
                BendableScore.valueOf(new int[]{-5}, new int[]{-300, -4000}));
        XStreamScoreConverterTest.assertXStreamXml(
                "<TestXStreamObject>\\s*<score>-5/-300/-4000</score>\\s*</TestXStreamObject>", input);
        PlannerTestUtils.serializeAndDeserializeWithAll(input,
                new PlannerTestUtils.OutputAsserter<TestXStreamObject>() {
                    public void assertOutput(TestXStreamObject output) {
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

    @XStreamAlias("TestXStreamObject")
    public static class TestXStreamObject implements Serializable {

        @XStreamConverter(value = XStreamBendableScoreConverter.class, ints = {1, 2})
        private BendableScore score;

        public TestXStreamObject(BendableScore score) {
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
