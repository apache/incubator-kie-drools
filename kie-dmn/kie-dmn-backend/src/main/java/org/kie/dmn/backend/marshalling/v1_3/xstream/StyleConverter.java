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
package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.dmndi.Style;
import org.kie.dmn.model.api.dmndi.Style.Extension;

public abstract class StyleConverter extends DMNModelInstrumentedBaseConverter {

    private static final String EXTENSION = "extension";
    private static final String ID = "id";

    public StyleConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Style style = (Style) parent;
        
        if (child instanceof Style.Extension) {
            style.setExtension((Extension) child);
        } else {
            super.assignChildElement(style, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Style style = (Style) parent;
        String id = reader.getAttribute(ID);
        if (id != null) {
            style.setId(id);
        }

    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Style style = (Style) parent;
        
        if (style.getExtension() != null) {
            writeChildrenNode(writer, context, style.getExtension(), EXTENSION);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Style style = (Style) parent;

        if (style.getId() != null) {
            writer.addAttribute(ID, style.getId());
        }

    }


}
