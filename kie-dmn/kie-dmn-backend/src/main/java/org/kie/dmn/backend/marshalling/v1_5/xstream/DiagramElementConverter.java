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
package org.kie.dmn.backend.marshalling.v1_5.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.backend.marshalling.v1_5.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.api.dmndi.DiagramElement;
import org.kie.dmn.model.api.dmndi.Style;

public abstract class DiagramElementConverter extends DMNModelInstrumentedBaseConverter {

    private static final String STYLE = "style";
    private static final String SHARED_STYLE = "sharedStyle";
    private static final String EXTENSION = "extension";
    private static final String ID = "id";

    public DiagramElementConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DiagramElement abs = (DiagramElement) parent;
        
        if (child instanceof DiagramElement.Extension) {
            abs.setExtension((DiagramElement.Extension) child);
        } else if (child instanceof Style) {
            abs.setStyle((Style) child);
        } else {
            super.assignChildElement(abs, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        DiagramElement abs = (DiagramElement) parent;
        String id = reader.getAttribute(ID);
        if (id != null) {
            abs.setId(id);
        }

        String sharedStyleXmlSerialization = reader.getAttribute(SHARED_STYLE);
        if (sharedStyleXmlSerialization != null) {
            abs.setSharedStyle(new org.kie.dmn.model.v1_5.dmndi.Style.IDREFStubStyle(sharedStyleXmlSerialization));
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DiagramElement abs = (DiagramElement) parent;
        
        if (abs.getExtension() != null) {
            writeChildrenNode(writer, context, abs.getExtension(), EXTENSION);
        }
        if (abs.getStyle() != null) {
            writeChildrenNode(writer, context, abs.getStyle(), STYLE);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        DiagramElement abs = (DiagramElement) parent;

        if (abs.getId() != null) {
            writer.addAttribute(ID, abs.getId());
        }

        if (abs.getSharedStyle() != null) {
            writer.addAttribute(SHARED_STYLE, abs.getSharedStyle().getId());
        }
    }


}
