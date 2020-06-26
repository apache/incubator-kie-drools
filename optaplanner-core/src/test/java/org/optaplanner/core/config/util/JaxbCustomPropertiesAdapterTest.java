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

package org.optaplanner.core.config.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.util.JaxbCustomPropertiesAdapter;

public class JaxbCustomPropertiesAdapterTest {

    private final Unmarshaller unmarshaller;

    public JaxbCustomPropertiesAdapterTest() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TestBean.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Test
    public void readCustomProperties() throws JAXBException {
        String xmlFragment = "<testBean>"
                + "  <customProperties>"
                + "    <property name=\"firstKey\" value=\"firstValue\"/>"
                + "    <property name=\"secondKey\" value=\"secondValue\"/>"
                + "  </customProperties>"
                + "</testBean>";
        Reader stringReader = new StringReader(xmlFragment);
        TestBean testBean = (TestBean) unmarshaller.unmarshal(stringReader);
        assertThat(testBean.customProperties).hasSize(2);
        assertThat(testBean.customProperties.get("firstKey")).isEqualTo("firstValue");
        assertThat(testBean.customProperties.get("secondKey")).isEqualTo("secondValue");
    }

    @Test
    public void nullValues() {
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
