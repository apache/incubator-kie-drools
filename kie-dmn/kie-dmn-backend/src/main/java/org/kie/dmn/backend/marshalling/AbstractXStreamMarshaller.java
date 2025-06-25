/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.backend.marshalling;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.AbstractPullReader;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import com.thoughtworks.xstream.security.TypeHierarchyPermission;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.impl.AbstractKieDMNModelInstrumentedBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractXStreamMarshaller
        implements DMNMarshaller {

    private static Logger logger = LoggerFactory.getLogger(AbstractXStreamMarshaller.class);
    protected List<DMNExtensionRegister> extensionRegisters = new ArrayList<>();
    private final StaxDriver staxDriver;

    protected AbstractXStreamMarshaller(StaxDriver staxDriver) {
        this.staxDriver = staxDriver;
    }

    protected AbstractXStreamMarshaller(StaxDriver staxDriver, List<DMNExtensionRegister> extensionRegisters) {
        this(staxDriver);
        this.extensionRegisters.addAll(extensionRegisters);
    }

    @Override
    public Definitions unmarshal(String xml) {
        return unmarshal(new StringReader(xml));
    }

    @Override
    public Definitions unmarshal(Reader isr) {
        try {
            XStream xStream = getXStream();
            Definitions def = (Definitions) xStream.fromXML(isr);

            return def;
        } catch (Exception e) {
            logger.error("Error unmarshalling DMN model from reader.", e);
        }
        return null;
    }

    @Override
    public String marshal(Object o) {
        try (Writer writer = new StringWriter();
             CustomStaxWriter hsWriter = (CustomStaxWriter) staxDriver.createWriter(writer);) {
            XStream xStream = getXStream();
            if (o instanceof DMNModelInstrumentedBase) {
                AbstractKieDMNModelInstrumentedBase base = (AbstractKieDMNModelInstrumentedBase) o;
                String dmnPrefix =
                        base.getNsContext().entrySet().stream().filter(kv -> isURIDMNEquals(kv.getValue())).findFirst().map(Map.Entry::getKey).orElse("");

                hsWriter.getQNameMap().setDefaultPrefix(dmnPrefix);
            }
            extensionRegisters.forEach(r -> r.beforeMarshal(o, hsWriter.getQNameMap()));
            xStream.marshal(o, hsWriter);
            hsWriter.flush();
            return writer.toString();
        } catch (Exception e) {
            logger.error("Error marshalling DMN model to XML.", e);
        }
        return null;
    }

    @Override
    public void marshal(Object o, Writer out) {
        try {
            out.write(marshal(o));
        } catch (Exception e) {
            logger.error("Error marshalling DMN model to XML.", e);
        }
    }

    protected abstract boolean isURIDMNEquals(String value);

    protected abstract XStream newXStream();

    private XStream getXStream() {
        XStream toReturn = newXStream();
        Set<String> allowedTypes = new HashSet<>();
        extensionRegisters.forEach(r -> {
            allowedTypes.addAll(r.allowedModelPackages());
        });
        allowedTypes.add("org.kie.dmn.model.**");
        toReturn.allowTypesByWildcard(allowedTypes.toArray(new String[0]));
        return toReturn;
    }
}
