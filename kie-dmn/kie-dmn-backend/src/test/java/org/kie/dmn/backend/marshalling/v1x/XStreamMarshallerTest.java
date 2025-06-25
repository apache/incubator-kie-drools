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
package org.kie.dmn.backend.marshalling.v1x;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class XStreamMarshallerTest {

    public static Map<XStreamMarshaller.DMN_VERSION, Map<XStreamMarshaller.URI_NAMESPACE, String>> mappedNamespaces;

    static {
        mappedNamespaces = new HashMap<>();
        Map<XStreamMarshaller.URI_NAMESPACE, String> dmn11nameSpaces = new EnumMap<>(XStreamMarshaller.URI_NAMESPACE.class);
        dmn11nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMN, org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN);
        dmn11nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_FEEL, org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_FEEL);
        dmn11nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMNDI, null);
        dmn11nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DI, null);
        dmn11nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DC, null);
        mappedNamespaces.put(XStreamMarshaller.DMN_VERSION.DMN_v1_1, dmn11nameSpaces);

        Map<XStreamMarshaller.URI_NAMESPACE, String> dmn12nameSpaces = new EnumMap<>(XStreamMarshaller.URI_NAMESPACE.class);
        dmn12nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMN, org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMN);
        dmn12nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_FEEL, org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_FEEL);
        dmn12nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMNDI, org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMNDI);
        dmn12nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DI, org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DI);
        dmn12nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DC, org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DC);
        mappedNamespaces.put(XStreamMarshaller.DMN_VERSION.DMN_v1_2, dmn12nameSpaces);

        Map<XStreamMarshaller.URI_NAMESPACE, String> dmn13nameSpaces = new EnumMap<>(XStreamMarshaller.URI_NAMESPACE.class);
        dmn13nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMN, org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase.URI_DMN);
        dmn13nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_FEEL, org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase.URI_FEEL);
        dmn13nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMNDI, org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase.URI_DMNDI);
        dmn13nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DI, org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase.URI_DI);
        dmn13nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DC, org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase.URI_DC);
        mappedNamespaces.put(XStreamMarshaller.DMN_VERSION.DMN_v1_3, dmn13nameSpaces);

        Map<XStreamMarshaller.URI_NAMESPACE, String> dmn14nameSpaces = new EnumMap<>(XStreamMarshaller.URI_NAMESPACE.class);
        dmn14nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMN, org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase.URI_DMN);
        dmn14nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_FEEL, org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase.URI_FEEL);
        dmn14nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMNDI, org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase.URI_DMNDI);
        dmn14nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DI, org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase.URI_DI);
        dmn14nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DC, org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase.URI_DC);
        mappedNamespaces.put(XStreamMarshaller.DMN_VERSION.DMN_v1_4, dmn14nameSpaces);

        Map<XStreamMarshaller.URI_NAMESPACE, String> dmn15nameSpaces = new EnumMap<>(XStreamMarshaller.URI_NAMESPACE.class);
        dmn15nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMN, org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase.URI_DMN);
        dmn15nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_FEEL, org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase.URI_FEEL);
        dmn15nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMNDI, org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase.URI_DMNDI);
        dmn15nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DI, org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase.URI_DI);
        dmn15nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DC, org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase.URI_DC);
        mappedNamespaces.put(XStreamMarshaller.DMN_VERSION.DMN_v1_5, dmn15nameSpaces);

        Map<XStreamMarshaller.URI_NAMESPACE, String> dmn16nameSpaces = new EnumMap<>(XStreamMarshaller.URI_NAMESPACE.class);
        dmn16nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMN, org.kie.dmn.model.v1_6.KieDMNModelInstrumentedBase.URI_DMN);
        dmn16nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_FEEL, org.kie.dmn.model.v1_6.KieDMNModelInstrumentedBase.URI_FEEL);
        dmn16nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DMNDI, org.kie.dmn.model.v1_6.KieDMNModelInstrumentedBase.URI_DMNDI);
        dmn16nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DI, org.kie.dmn.model.v1_6.KieDMNModelInstrumentedBase.URI_DI);
        dmn16nameSpaces.put(org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller.URI_NAMESPACE.URI_DC, org.kie.dmn.model.v1_6.KieDMNModelInstrumentedBase.URI_DC);
        mappedNamespaces.put(XStreamMarshaller.DMN_VERSION.DMN_v1_6, dmn16nameSpaces);
    }

    @Test
    void getNamespaceValueReflectively() {
        Arrays.stream(XStreamMarshaller.DMN_VERSION.values()).forEach(version -> {
            if (version != XStreamMarshaller.DMN_VERSION.UNKNOWN) {
                Arrays.stream(XStreamMarshaller.URI_NAMESPACE.values()).forEach(uri -> {
                    String retrieved = XStreamMarshaller.getNamespaceValueReflectively(version,
                                                                                       uri);
                    assertThat(retrieved).isEqualTo(mappedNamespaces.get(version).get(uri));
                });
            }
        });
    }
}