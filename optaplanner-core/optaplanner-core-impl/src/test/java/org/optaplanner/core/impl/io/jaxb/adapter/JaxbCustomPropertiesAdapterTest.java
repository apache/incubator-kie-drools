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

package org.optaplanner.core.impl.io.jaxb.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.jupiter.api.Test;

class JaxbCustomPropertiesAdapterTest {

    private final Unmarshaller unmarshaller;

    public JaxbCustomPropertiesAdapterTest() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TestBean.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Test
    void readCustomProperties() throws JAXBException {
        String xmlFragment = "<testBean>"
                + "  <customProperties>"
                + "    <property xmlns=\"https://www.optaplanner.org/xsd/solver\" name=\"firstKey\" value=\"firstValue\"/>"
                + "    <property xmlns=\"https://www.optaplanner.org/xsd/solver\" name=\"secondKey\" value=\"secondValue\"/>"
                + "  </customProperties>"
                + "</testBean>";
        Reader stringReader = new StringReader(xmlFragment);
        TestBean testBean = (TestBean) unmarshaller.unmarshal(stringReader);
        assertThat(testBean.customProperties)
                .hasSize(2)
                .containsEntry("firstKey", "firstValue")
                .containsEntry("secondKey", "secondValue");
    }

    @Test
    void nullValues() {
        JaxbCustomPropertiesAdapter jaxbCustomPropertiesAdapter = new JaxbCustomPropertiesAdapter();
        assertThat(jaxbCustomPropertiesAdapter.marshal(null)).isNull();
        assertThat(jaxbCustomPropertiesAdapter.unmarshal(null)).isNull();
    }

    @XmlAccessorType(value = XmlAccessType.FIELD)
    @XmlRootElement
    private static class TestBean {

        @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
        private Map<String, String> customProperties = null;

        public TestBean() {
        }
    }
}
