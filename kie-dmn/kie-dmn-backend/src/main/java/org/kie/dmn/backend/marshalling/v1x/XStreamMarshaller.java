/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.kie.dmn.api.marshalling.v1x.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.v1x.DMNMarshaller;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1x.Definitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XStreamMarshaller implements DMNMarshaller {

    private static Logger logger = LoggerFactory.getLogger( XStreamMarshaller.class );
    private List<DMNExtensionRegister> extensionRegisters = new ArrayList<>();
    private final org.kie.dmn.backend.marshalling.v1_1.xstream.XStreamMarshaller xstream11;
    private final org.kie.dmn.backend.marshalling.v1_2.xstream.XStreamMarshaller xstream12;

    public XStreamMarshaller() {
        xstream11 = new org.kie.dmn.backend.marshalling.v1_1.xstream.XStreamMarshaller();
        xstream12 = new org.kie.dmn.backend.marshalling.v1_2.xstream.XStreamMarshaller();
    }

    public XStreamMarshaller (List<DMNExtensionRegister> extensionRegisters) {
        this.extensionRegisters.addAll(extensionRegisters);
        xstream11 = new org.kie.dmn.backend.marshalling.v1_1.xstream.XStreamMarshaller(extensionRegisters);
        xstream12 = new org.kie.dmn.backend.marshalling.v1_2.xstream.XStreamMarshaller(extensionRegisters);
    }

    @Override
    public Definitions unmarshal(String xml) {
        try {
            StaxDriver staxDriver = new StaxDriver();
            XMLStreamReader reader2 = staxDriver.getInputFactory().createXMLStreamReader(new StringReader(xml));
            CustomStaxReader r = new CustomStaxReader(new QNameMap(), reader2);
            if (r.getNsContext().values().stream().anyMatch(s -> KieDMNModelInstrumentedBase.URI_DMN.equals(s))) {
                return xstream12.unmarshal(new StringReader(xml));
            } else if (r.getNsContext().values().stream().anyMatch(s -> org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN.equals(s))) {
                return xstream11.unmarshal(new StringReader(xml));
            } else {
                return xstream12.unmarshal(new StringReader(xml));
            }
        } catch ( Exception e ) {
            logger.error( "Error unmarshalling DMN model from reader.", e );
        }
        return null;
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
        if (o instanceof KieDMNModelInstrumentedBase) {
            return xstream12.marshal(o);
        } else if (o instanceof org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase) {
            return xstream11.marshal(o);
        } else {
            return xstream12.marshal(o);
        }
    }

    @Override
    public void marshal(Object o, Writer out) {
        if (o instanceof KieDMNModelInstrumentedBase) {
            xstream12.marshal(o, out);
        } else if (o instanceof org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase) {
            xstream11.marshal(o, out);
        } else {
            xstream12.marshal(o, out);
        }
    }

}
