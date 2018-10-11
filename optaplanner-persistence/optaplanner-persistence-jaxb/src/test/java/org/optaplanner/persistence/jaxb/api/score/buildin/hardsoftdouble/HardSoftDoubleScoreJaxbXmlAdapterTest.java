/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jaxb.api.score.buildin.hardsoftdouble;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoftdouble.HardSoftDoubleScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbXmlAdapterTest;

public class HardSoftDoubleScoreJaxbXmlAdapterTest extends AbstractScoreJaxbXmlAdapterTest {

    @Test
    public void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestHardSoftDoubleScoreWrapper(null));
        HardSoftDoubleScore score = HardSoftDoubleScore.of(1200.0021, 34.4300);
        assertSerializeAndDeserialize(score, new TestHardSoftDoubleScoreWrapper(score));
        score = HardSoftDoubleScore.ofUninitialized(-7, 1200.0021, 34.4300);
        assertSerializeAndDeserialize(score, new TestHardSoftDoubleScoreWrapper(score));
    }

    @XmlRootElement
    public static class TestHardSoftDoubleScoreWrapper extends TestScoreWrapper<HardSoftDoubleScore> {

        @XmlJavaTypeAdapter(HardSoftDoubleScoreJaxbXmlAdapter.class)
        private HardSoftDoubleScore score;

        @SuppressWarnings("unused")
        private TestHardSoftDoubleScoreWrapper() {
        }

        public TestHardSoftDoubleScoreWrapper(HardSoftDoubleScore score) {
            this.score = score;
        }

        @Override
        public HardSoftDoubleScore getScore() {
            return score;
        }

    }

}
