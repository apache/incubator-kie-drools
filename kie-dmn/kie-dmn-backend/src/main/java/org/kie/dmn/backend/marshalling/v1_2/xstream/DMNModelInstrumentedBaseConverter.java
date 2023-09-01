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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DMNModelInstrumentedBaseConverter
        extends DMNBaseConverter {

    private static final Logger LOG = LoggerFactory.getLogger(DMNModelInstrumentedBaseConverter.class);

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
                LOG.warn("The XML driver writer failed to manage writing namespace, namespaces prefixes could be wrong in the resulting file.", e);
            }
        }
        
        for ( Entry<QName, String> kv : mib.getAdditionalAttributes().entrySet() ) {
            staxWriter.addAttribute(kv.getKey().getPrefix() + ":" + kv.getKey().getLocalPart(), kv.getValue());
        }

        if (parent instanceof TDefinitions) {
            TDefinitions tDefinitions = (TDefinitions) parent;
            String dmndiPrefix = tDefinitions.getPrefixForNamespaceURI(KieDMNModelInstrumentedBase.URI_DMNDI).orElse("dmndi");
            String diPrefix = tDefinitions.getPrefixForNamespaceURI(KieDMNModelInstrumentedBase.URI_DI).orElse("di");
            String dcPrefix = tDefinitions.getPrefixForNamespaceURI(KieDMNModelInstrumentedBase.URI_DC).orElse("dc");

            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "DMNDI", dmndiPrefix), "DMNDI");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "DMNDiagram", dmndiPrefix), "DMNDiagram");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "DMNStyle", dmndiPrefix), "style");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "DMNStyle", dmndiPrefix), "DMNStyle");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "DMNShape", dmndiPrefix), "DMNShape");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "DMNEdge", dmndiPrefix), "DMNEdge");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "DMNDecisionServiceDividerLine", dmndiPrefix), "DMNDecisionServiceDividerLine");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "DMNLabel", dmndiPrefix), "DMNLabel");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, DMNLabelConverter.TEXT, dmndiPrefix), DMNLabelConverter.TEXT);
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "Size", dmndiPrefix), "Size");

            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "FillColor", dmndiPrefix), "FillColor");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "StrokeColor", dmndiPrefix), "StrokeColor");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DMNDI, "FontColor", dmndiPrefix), "FontColor");

            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DI, "waypoint", diPrefix), "waypoint");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DI, "extension", diPrefix), "extension");
            staxWriter.getQNameMap().registerMapping(new QName(KieDMNModelInstrumentedBase.URI_DC, "Bounds", dcPrefix), "Bounds");
        }
    }
}
