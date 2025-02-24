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
package org.kie.dmn.validation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.junit.jupiter.api.Test;
import org.kie.api.builder.Message;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DC;
import static org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DI;
import static org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMN;
import static org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMNDI;
import static org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_FEEL;
import static org.kie.dmn.validation.DMNValidatorImpl.DMNVERSION_SCHEMA_MAP;

class DMNValidatorImplTest {

    @Test
    void validateNsContextValuesValid() {
        Collection<String> nsContextValues = new HashSet<>();
        nsContextValues.add("http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442");
        nsContextValues.add("https://www.omg.org/spec/DMN/20230324/MODEL/");
        nsContextValues.add("http://www.omg.org/spec/DMN/20180521/DI/");
        nsContextValues.add("https://www.omg.org/spec/DMN/20230324/FEEL/");
        nsContextValues.add("https://www.omg.org/spec/DMN/20230324/DMNDI/");
        nsContextValues.add("http://www.omg.org/spec/DMN/20180521/DC/");
        nsContextValues.add("http://www.w3.org/2001/XMLSchema-instance");
        List<DMNMessage> retrieved =  DMNValidatorImpl.validateNsContextValues(nsContextValues, "path", XStreamMarshaller.DMN_VERSION.DMN_v1_5);
        assertThat(retrieved).isNotNull().isEmpty();
    }

    @Test
    void validateNsContextValuesInvalid() {
        Collection<String> nsContextValues = new HashSet<>();
        nsContextValues.add("http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442");
        nsContextValues.add("https://www.omg.org/spec/DMN/20230324/MODEL/");
        nsContextValues.add("http://www.omg.org/spec/DMN/20180505/DI/");
        nsContextValues.add("https://www.omg.org/spec/DMN/20230324/FEEL/");
        nsContextValues.add("https://www.omg.org/spec/DMN/20191111/DMNDI/");
        nsContextValues.add("http://www.omg.org/spec/DMN/20180505/DC/");
        nsContextValues.add("http://www.w3.org/2001/XMLSchema-instance");

        List<DMNMessage> retrieved =  DMNValidatorImpl.validateNsContextValues(nsContextValues, "path", XStreamMarshaller.DMN_VERSION.DMN_v1_5);
        assertThat(retrieved).hasSize(3).allMatch(dmnMessage -> dmnMessage.getLevel().equals(Message.Level.ERROR))
                        .anyMatch(dmnMessage -> dmnMessage.getText().contains("http://www.omg.org/spec/DMN/20180505/DI/"))
                .anyMatch(dmnMessage -> dmnMessage.getText().contains("http://www.omg.org/spec/DMN/20180505/DI/"))
                .anyMatch(dmnMessage -> dmnMessage.getText().contains("https://www.omg.org/spec/DMN/20191111/DMNDI/"))
                .anyMatch(dmnMessage -> dmnMessage.getText().contains("http://www.omg.org/spec/DMN/20180505/DC/"));
    }

    @Test
    void getMappedNamespaces() {
        Collection<String> nsContextValues = new HashSet<>();
        nsContextValues.add("http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442");
        nsContextValues.add("https://www.omg.org/spec/DMN/20230324/MODEL/");
        nsContextValues.add("http://www.omg.org/spec/DMN/20180505/DI/");
        nsContextValues.add("https://www.omg.org/spec/DMN/20230324/FEEL/");
        nsContextValues.add("https://www.omg.org/spec/DMN/20191111/DMNDI/");
        nsContextValues.add("http://www.omg.org/spec/DMN/20180505/DC/");
        nsContextValues.add("http://www.w3.org/2001/XMLSchema-instance");
        Map<XStreamMarshaller.URI_NAMESPACE, String> expected = Map.of(URI_DMN, "https://www.omg.org/spec/DMN/20230324/MODEL/",
                                                                       URI_FEEL,  "https://www.omg.org/spec/DMN/20230324/FEEL/",
                                                                       URI_DMNDI, "https://www.omg.org/spec/DMN/20191111/DMNDI/",
                                                                       URI_DI, "http://www.omg.org/spec/DMN/20180505/DI/",
                                                                       URI_DC, "http://www.omg.org/spec/DMN/20180505/DC/");
        Map<XStreamMarshaller.URI_NAMESPACE, String> retrieved = DMNValidatorImpl.getMappedNamespaces(nsContextValues);
        assertThat(retrieved).hasSize(expected.size()).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void determineSchemaWithoutOverride() {
        Arrays.stream(XStreamMarshaller.DMN_VERSION.values()).forEach(version -> assertThat(DMNValidatorImpl.determineSchema(version, null)).isEqualTo(DMNVERSION_SCHEMA_MAP.get(version)));
    }

    @Test
    void determineSchemaWithOverride() {
        Schema overrideSchema = getSchema();
        Arrays.stream(XStreamMarshaller.DMN_VERSION.values()).forEach(version -> assertThat(DMNValidatorImpl.determineSchema(version, overrideSchema)).isEqualTo(overrideSchema));
    }

    private Schema getSchema() {
        return new Schema() {
            @Override
            public Validator newValidator() {
                return null;
            }

            @Override
            public ValidatorHandler newValidatorHandler() {
                return null;
            }
        };
    }
}