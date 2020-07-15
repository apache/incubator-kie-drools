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

package org.optaplanner.core.impl.io.jaxb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.io.XmlUnmarshallingException;

public class JaxbIOTest {

    private final JaxbIO<DummyJaxbClass> xmlIO = new JaxbIO<>(DummyJaxbClass.class);

    @Test
    public void readWriteSimpleObject() {
        DummyJaxbClass original = new DummyJaxbClass(1);

        StringWriter stringWriter = new StringWriter();
        xmlIO.write(original, stringWriter);

        DummyJaxbClass marshalledObject = xmlIO.read(new StringReader(stringWriter.toString()));
        assertThat(marshalledObject).isEqualTo(original);
    }

    @Test
    public void writeThrowsExceptionOnNullParameters() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(assertThatNullPointerException().isThrownBy(() -> xmlIO.write(null, new StringWriter())));
            softly.assertThat(assertThatNullPointerException().isThrownBy(() -> xmlIO.write(new DummyJaxbClass(1), null)));
        });
    }

    @Test
    public void readThrowsExceptionOnNullParameter() {
        assertThatNullPointerException().isThrownBy(() -> new JaxbIO<>(DummyJaxbClass.class).read(null));
    }

    @Test
    public void readThrowsExceptionOnInvalidXml() {
        String invalidXml = "<unknownRootElement/>";
        assertThatExceptionOfType(XmlUnmarshallingException.class).isThrownBy(() -> xmlIO.read(new StringReader(invalidXml)));
    }

    @XmlRootElement
    private static class DummyJaxbClass {
        @XmlAttribute
        private int id;

        private DummyJaxbClass() {
            // Required by JAXB
        }

        private DummyJaxbClass(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DummyJaxbClass)) {
                return false;
            }
            DummyJaxbClass that = (DummyJaxbClass) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
