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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.optaplanner.core.config.solver.SolverConfig;

public class JaxbCustomPropertiesAdapter extends XmlAdapter<JaxbCustomPropertiesAdapter.JaxbAdaptedMap, Map<String, String>> {

    @Override
    public Map<String, String> unmarshal(JaxbAdaptedMap jaxbAdaptedMap) {
        if (jaxbAdaptedMap == null) {
            return null;
        }
        return jaxbAdaptedMap.entries.stream()
                .collect(Collectors.toMap(JaxbAdaptedMapEntry::getName, JaxbAdaptedMapEntry::getValue));
    }

    @Override
    public JaxbAdaptedMap marshal(Map<String, String> originalMap) {
        if (originalMap == null) {
            return null;
        }
        List<JaxbAdaptedMapEntry> entries = originalMap.entrySet().stream()
                .map(entry -> new JaxbCustomPropertiesAdapter.JaxbAdaptedMapEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return new JaxbAdaptedMap(entries);
    }

    // Required to generate the XSD type in the same namespace.
    @XmlType(namespace = SolverConfig.XML_NAMESPACE)
    static class JaxbAdaptedMap {

        @XmlElement(name = "property", namespace = SolverConfig.XML_NAMESPACE)
        private List<JaxbAdaptedMapEntry> entries;

        private JaxbAdaptedMap() {
            // Required by JAXB
        }

        public JaxbAdaptedMap(List<JaxbAdaptedMapEntry> entries) {
            this.entries = entries;
        }
    }

    // Required to generate the XSD type in the same namespace.
    @XmlType(namespace = SolverConfig.XML_NAMESPACE)
    static class JaxbAdaptedMapEntry {

        @XmlAttribute
        private String name;

        @XmlAttribute
        private String value;

        public JaxbAdaptedMapEntry() {
        }

        public JaxbAdaptedMapEntry(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
