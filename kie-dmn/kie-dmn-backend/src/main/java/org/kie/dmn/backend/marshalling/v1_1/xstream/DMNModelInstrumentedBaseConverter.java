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

package org.kie.dmn.backend.marshalling.v1_1.xstream;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.stream.Location;

import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.CustomStaxWriter;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxWriter;

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
        DMNModelInstrumentedBase mib = (DMNModelInstrumentedBase) parent;

        CustomStaxReader customStaxReader = (CustomStaxReader) reader.underlyingReader();
        
        Map<String, String> currentNSCtx = customStaxReader.getNsContext();
        mib.getNsContext().putAll(currentNSCtx);

        mib.setLocation( customStaxReader.getLocation() );
    }
    
    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        // no call to super as super is abstract method.
    }
    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        DMNModelInstrumentedBase mib = (DMNModelInstrumentedBase) parent;

        CustomStaxWriter staxWriter = ((CustomStaxWriter) writer.underlyingWriter());
        for (Entry<String, String> kv : mib.getNsContext().entrySet()) {
            try {
                if (XMLConstants.DEFAULT_NS_PREFIX.equals(kv.getKey())) {
                    // skip as that is the default namespace is handled by the stax driver.
                } else {
                    staxWriter.writeNamespace(kv.getKey(), kv.getValue());
                }
            } catch (Exception e) {
                //TODO what to do?
                e.printStackTrace();
            }
        }
    }
}
