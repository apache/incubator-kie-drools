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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.DMNDI;
import org.kie.dmn.model.api.dmndi.DMNDiagram;
import org.kie.dmn.model.api.dmndi.DMNStyle;

public class DMNDIConverter extends DMNModelInstrumentedBaseConverter {

    private static final String DMN_STYLE = "DMNStyle";
    private static final String DMN_DIAGRAM = "DMNDiagram";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DMNDI list = (DMNDI) parent;
        
        if (child instanceof DMNDiagram) {
            list.getDMNDiagram().add((DMNDiagram) child);
        } else if (child instanceof DMNStyle) {
            list.getDMNStyle().add((DMNStyle) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        
        // no attributes.
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DMNDI list = (DMNDI) parent;
        
        for (DMNDiagram e : list.getDMNDiagram()) {
            writeChildrenNode(writer, context, e, DMN_DIAGRAM);
        }
        for (DMNStyle e : list.getDMNStyle()) {
            writeChildrenNode(writer, context, e, DMN_STYLE);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public DMNDIConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_2.dmndi.DMNDI();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(org.kie.dmn.model.v1_2.dmndi.DMNDI.class);
    }

}
