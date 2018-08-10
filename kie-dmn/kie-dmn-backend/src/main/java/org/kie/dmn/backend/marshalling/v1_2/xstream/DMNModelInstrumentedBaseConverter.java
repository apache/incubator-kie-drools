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

package org.kie.dmn.backend.marshalling.v1_2.xstream;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.CustomStaxWriter;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1_2.TDefinitions;

public abstract class DMNModelInstrumentedBaseConverter
        extends DMNBaseConverter {

    public DMNModelInstrumentedBaseConverter(XStream xstream) {
        super( xstream.getMapper() );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        KieDMNModelInstrumentedBase mib = (KieDMNModelInstrumentedBase) parent;

        CustomStaxReader customStaxReader = (CustomStaxReader) reader.underlyingReader();
        
        Map<String, String> currentNSCtx = customStaxReader.getNsContext();
        mib.getNsContext().putAll(currentNSCtx);

        mib.setLocation( customStaxReader.getLocation() );
        
        mib.setAdditionalAttributes( customStaxReader.getAdditionalAttributes() );
    }
    
    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        // no call to super as super is abstract method.
    }
    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        KieDMNModelInstrumentedBase mib = (KieDMNModelInstrumentedBase) parent;

        CustomStaxWriter staxWriter = ((CustomStaxWriter) writer.underlyingWriter());
        for (Entry<String, String> kv : mib.getNsContext().entrySet()) {
            try {
                if (KieDMNModelInstrumentedBase.URI_DMN.equals(kv.getValue())) {
                    // skip as that is the default namespace xmlns<:prefix>=DMN is handled by the stax driver.
                } else {
                    staxWriter.writeNamespace(kv.getKey(), kv.getValue());
                }
            } catch (Exception e) {
                //TODO what to do?
                e.printStackTrace();
            }
        }
        
        for ( Entry<QName, String> kv : mib.getAdditionalAttributes().entrySet() ) {
            staxWriter.addAttribute(kv.getKey().getPrefix() + ":" + kv.getKey().getLocalPart(), kv.getValue());
        }

        // TODO un-hardcode this XStream hack... peek namespace value from node NSContext and populate with the Definitions NSContext actual prefixes.
        if (parent instanceof TDefinitions) {
            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DMNDI/", "DMNDI", "dmndi"), "DMNDI");
            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DMNDI/", "DMNDiagram", "dmndi"), "DMNDiagram");
            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DMNDI/", "DMNStyle", "dmndi"), "style");
            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DMNDI/", "DMNStyle", "dmndi"), "DMNStyle");
            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DMNDI/", "DMNShape", "dmndi"), "DMNShape");
            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DMNDI/", "DMNEdge", "dmndi"), "DMNEdge");
            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DMNDI/", "DMNLabel", "dmndi"), "DMNLabel");
            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DMNDI/", "Size", "dmndi"), "Size");

            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DMNDI/", "FillColor", "dmndi"), "FillColor");

            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DI/", "waypoint", "di"), "waypoint");
            staxWriter.getQNameMap().registerMapping(new QName("http://www.omg.org/spec/DMN/20180521/DC/", "Bounds", "dc"), "Bounds");
        }
    }
}
