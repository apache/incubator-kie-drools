/**
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

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamReader;

import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XStreamMarshaller implements DMNMarshaller {

    private static Logger logger = LoggerFactory.getLogger( XStreamMarshaller.class );
    private List<DMNExtensionRegister> extensionRegisters = new ArrayList<>();
    private final org.kie.dmn.backend.marshalling.v1_1.xstream.XStreamMarshaller xstream11;
    private final org.kie.dmn.backend.marshalling.v1_2.xstream.XStreamMarshaller xstream12;
    private final org.kie.dmn.backend.marshalling.v1_3.xstream.XStreamMarshaller xstream13;
    private final org.kie.dmn.backend.marshalling.v1_4.xstream.XStreamMarshaller xstream14;
    private final org.kie.dmn.backend.marshalling.v1_5.xstream.XStreamMarshaller xstream15;
    private static final StaxDriver staxDriver = new StaxDriver();

    public XStreamMarshaller() {
        xstream11 = new org.kie.dmn.backend.marshalling.v1_1.xstream.XStreamMarshaller();
        xstream12 = new org.kie.dmn.backend.marshalling.v1_2.xstream.XStreamMarshaller();
        xstream13 = new org.kie.dmn.backend.marshalling.v1_3.xstream.XStreamMarshaller();
        xstream14 = new org.kie.dmn.backend.marshalling.v1_4.xstream.XStreamMarshaller();
        xstream15 = new org.kie.dmn.backend.marshalling.v1_5.xstream.XStreamMarshaller();
    }

    public XStreamMarshaller (List<DMNExtensionRegister> extensionRegisters) {
        this.extensionRegisters.addAll(extensionRegisters);
        xstream11 = new org.kie.dmn.backend.marshalling.v1_1.xstream.XStreamMarshaller(extensionRegisters);
        xstream12 = new org.kie.dmn.backend.marshalling.v1_2.xstream.XStreamMarshaller(extensionRegisters);
        xstream13 = new org.kie.dmn.backend.marshalling.v1_3.xstream.XStreamMarshaller(extensionRegisters);
        xstream14 = new org.kie.dmn.backend.marshalling.v1_4.xstream.XStreamMarshaller(extensionRegisters);
        xstream15 = new org.kie.dmn.backend.marshalling.v1_5.xstream.XStreamMarshaller(extensionRegisters);
    }

    @Override
    public Definitions unmarshal(String xml) {
        try (Reader firstStringReader = new StringReader(xml);
                Reader secondStringReader = new StringReader(xml);) {
            DMN_VERSION inferDMNVersion = inferDMNVersion(firstStringReader);

            Definitions result;
            switch (inferDMNVersion) {
                case DMN_v1_1:
                    result = xstream11.unmarshal(secondStringReader);
                    break;
                case DMN_v1_2:
                    result = xstream12.unmarshal(secondStringReader);
                    break;
                case DMN_v1_3:
                    result = xstream13.unmarshal(secondStringReader);
                    break;
                case DMN_v1_4:
                    result = xstream14.unmarshal(secondStringReader);
                    break;
                case UNKNOWN:
                case DMN_v1_5:
                default:
                    result = xstream15.unmarshal(secondStringReader);
                    break;

            }
            return result;
        } catch ( Exception e ) {
            logger.error( "Error unmarshalling DMN model from reader.", e );
        }
        return null;
    }

    public enum DMN_VERSION {
        UNKNOWN, DMN_v1_1, DMN_v1_2, DMN_v1_3, DMN_v1_4, DMN_v1_5;
    }

    public static DMN_VERSION inferDMNVersion(Reader from) {
        try {
            XMLStreamReader xmlReader = staxDriver.getInputFactory().createXMLStreamReader(from);
            CustomStaxReader customStaxReader = new CustomStaxReader(new QNameMap(), xmlReader);
            DMN_VERSION result = DMN_VERSION.UNKNOWN;
            if (customStaxReader.getNsContext().values().stream().anyMatch(org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
                result = DMN_VERSION.DMN_v1_5;
            } else if (customStaxReader.getNsContext().values().stream().anyMatch(org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
                result = DMN_VERSION.DMN_v1_4;
            } else if (customStaxReader.getNsContext().values().stream().anyMatch(org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
                result = DMN_VERSION.DMN_v1_3;
            } else if (customStaxReader.getNsContext().values().stream().anyMatch(org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
                result = DMN_VERSION.DMN_v1_2;
            } else if (customStaxReader.getNsContext().values().stream().anyMatch(org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
                result = DMN_VERSION.DMN_v1_1;
            }
            xmlReader.close();
            customStaxReader.close();
            return result;
        } catch (Exception e) {
            logger.error("Error unmarshalling DMN model from reader.", e);
        }
        return DMN_VERSION.UNKNOWN;
    }

    @Override
    public Definitions unmarshal(Reader isr) {
        try (BufferedReader buffer = new BufferedReader(isr)) {
            String xml = buffer.lines().collect(Collectors.joining("\n"));
            return unmarshal(xml);
        } catch (Exception e) {
            logger.error("Error unmarshalling DMN model from reader.", e);
        }
        return null;
    }

    @Override
    public String marshal(Object o) {
        if (o instanceof org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase) {
            return xstream15.marshal(o);
        } else if (o instanceof org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase) {
            return xstream14.marshal(o);
        } else if (o instanceof org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase) {
            return xstream13.marshal(o);
        } else if (o instanceof org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase) {
            return xstream12.marshal(o);
        } else if (o instanceof org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase) {
            return xstream11.marshal(o);
        } else {
            return xstream14.marshal(o);
        }
    }

    @Override
    public void marshal(Object o, Writer out) {
        if (o instanceof org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase) {
            xstream15.marshal(o, out);
        } else if (o instanceof org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase) {
            xstream14.marshal(o, out);
        } else if (o instanceof org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase) {
            xstream13.marshal(o, out);
        } else if (o instanceof org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase) {
            xstream12.marshal(o, out);
        } else if (o instanceof org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase) {
            xstream11.marshal(o, out);
        } else {
            xstream14.marshal(o, out);
        }
    }

}
