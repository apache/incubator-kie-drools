/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.DMNDiagram;
import org.kie.dmn.model.api.dmndi.DiagramElement;
import org.kie.dmn.model.api.dmndi.Dimension;

public class DMNDiagramConverter extends DiagramConverter {

    private static final String SIZE = "Size";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DMNDiagram style = (DMNDiagram) parent;

        if (child instanceof Dimension) {
            style.setSize((Dimension) child);
        } else if (child instanceof DiagramElement) {
            style.getDMNDiagramElement().add((DiagramElement) child);
        } else {
            super.assignChildElement(style, nodeName, child);
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
        DMNDiagram style = (DMNDiagram) parent;
        
        if (style.getSize() != null) {
            writeChildrenNode(writer, context, style.getSize(), SIZE);
        }
        for (DiagramElement de : style.getDMNDiagramElement()) {
            writeChildrenNode(writer, context, de, de.getClass().getSimpleName());
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        // no attributes.
    }

    public DMNDiagramConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_3.dmndi.DMNDiagram();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(org.kie.dmn.model.v1_3.dmndi.DMNDiagram.class);
    }

}
