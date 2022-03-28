/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jaxb.api.score;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.io.jaxb.GenericJaxbIO;

class PolymorphicScoreJaxbAdapterTest {

    private final PolymorphicScoreJaxbAdapter scoreJaxbAdapter = new PolymorphicScoreJaxbAdapter();

    @Test
    void marshall() {
        Score<?> score = SimpleScore.of(1);
        PolymorphicScoreJaxbAdapter.JaxbAdaptedScore adaptedScore = scoreJaxbAdapter.marshal(score);
        assertThat(adaptedScore.getScoreClassName()).isEqualTo(SimpleScore.class.getName());
        assertThat(adaptedScore.getScoreString()).isEqualTo(score.toString());
    }

    @Test
    void unmarshall() {
        String xmlString = "<dummy>"
                + "<score class=\"org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore\">-1hard/-10soft</score>"
                + "</dummy>";

        GenericJaxbIO<DummyRootElement> xmlIO = new GenericJaxbIO<>(DummyRootElement.class);
        DummyRootElement dummyRootElement = xmlIO.read(new StringReader(xmlString));

        assertThat(dummyRootElement.score).isEqualTo(HardSoftLongScore.of(-1L, -10L));
    }

    @XmlRootElement(name = "dummy")
    private static class DummyRootElement {

        @XmlJavaTypeAdapter(PolymorphicScoreJaxbAdapter.class)
        private Score<?> score;

        private DummyRootElement() {
            // Required by JAXB
        }

        private DummyRootElement(Score<?> score) {
            this.score = score;
        }
    }
}
