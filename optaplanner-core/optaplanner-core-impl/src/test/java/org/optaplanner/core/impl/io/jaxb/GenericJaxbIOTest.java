/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.io.jaxb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.io.OptaPlannerXmlSerializationException;
import org.xml.sax.SAXParseException;

class GenericJaxbIOTest {

    private final GenericJaxbIO<DummyJaxbClass> xmlIO = new GenericJaxbIO<>(DummyJaxbClass.class);

    @Test
    void readWriteSimpleObject() {
        DummyJaxbClass original = new DummyJaxbClass(1, "");

        StringWriter stringWriter = new StringWriter();
        xmlIO.write(original, stringWriter);

        DummyJaxbClass marshalledObject = xmlIO.read(new StringReader(stringWriter.toString()));
        assertThat(marshalledObject).isEqualTo(original);
    }

    @Test
    void writeThrowsExceptionOnNullParameters() {
        assertSoftly(softly -> {
            softly.assertThat(assertThatNullPointerException().isThrownBy(() -> xmlIO.write(null, new StringWriter())));
            softly.assertThat(assertThatNullPointerException().isThrownBy(() -> xmlIO.write(new DummyJaxbClass(1, ""), null)));
        });
    }

    @Test
    void readThrowsExceptionOnNullParameter() {
        assertThatNullPointerException().isThrownBy(() -> new GenericJaxbIO<>(DummyJaxbClass.class).read(null));
    }

    @Test
    void readThrowsExceptionOnInvalidXml() {
        String invalidXml = "<unknownRootElement/>";
        StringReader stringReader = new StringReader(invalidXml);
        assertThatExceptionOfType(OptaPlannerXmlSerializationException.class).isThrownBy(() -> xmlIO.read(stringReader));
    }

    @Test
    void readOverridingNamespaceIsProtectedFromXXE() {
        String maliciousXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<!DOCTYPE dummyJaxbClass [  \n"
                + "  <!ELEMENT dummyJaxbClass ANY >\n"
                + "  <!ENTITY xxe SYSTEM \"file:///etc/passwd\" >]"
                + ">"
                + "<dummyJaxbClass>&xxe;</dummyJaxbClass>";
        assertThatExceptionOfType(OptaPlannerXmlSerializationException.class)
                .isThrownBy(() -> xmlIO.readOverridingNamespace(new StringReader(maliciousXml)))
                .withRootCauseExactlyInstanceOf(SAXParseException.class)
                .withStackTraceContaining("accessExternalDTD");
    }

    @XmlRootElement
    private static class DummyJaxbClass {

        // This field is used only for evaluating the XXE attack protection.
        @XmlValue
        private String value;

        @XmlAttribute
        private int id;

        private DummyJaxbClass() {
            // Required by JAXB
        }

        private DummyJaxbClass(int id, String value) {
            this.id = id;
            this.value = value;
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
