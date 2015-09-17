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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class XStreamScoreConverterTest {

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
        TestXStreamObject input = new TestXStreamObject(HardSoftScore.valueOf(1200, 34));
        assertXStreamXml("<TestXStreamObject>\\s*<score>1200hard/34soft</score>\\s*</TestXStreamObject>", input);
        PlannerTestUtils.serializeAndDeserializeWithAll(input,
                new PlannerTestUtils.OutputAsserter<TestXStreamObject>() {
                    public void assertOutput(TestXStreamObject output) {
                        assertEquals(HardSoftScore.valueOf(1200, 34), output.getScore());
                    }
                }
        );
    }

    public static void assertXStreamXml(String regex, Object input) {
        XStream xStream = new XStream();
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.processAnnotations(input.getClass());
        String xml = xStream.toXML(input);
        if (!xml.matches(regex)) {
            fail("Regular expression match failed.\nExpected regular expression: " + regex + "\nActual: " + xml);
        }
        assertTrue(xml.matches(regex));
    }

    @XStreamAlias("TestXStreamObject")
    public static class TestXStreamObject implements Serializable {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftScoreDefinition.class})
        private HardSoftScore score;

        public TestXStreamObject(HardSoftScore score) {
            this.score = score;
        }

        public HardSoftScore getScore() {
            return score;
        }

        public void setScore(HardSoftScore score) {
            this.score = score;
        }
    }

}
