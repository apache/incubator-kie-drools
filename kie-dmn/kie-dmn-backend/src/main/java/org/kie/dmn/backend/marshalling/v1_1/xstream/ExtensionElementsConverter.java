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

import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.DMNElement.ExtensionElements;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Currently ignoring all extensionElements
 */
public class ExtensionElementsConverter extends DMNModelInstrumentedBaseConverter {

    private List<DMNExtensionRegister> extensionRegisters = new ArrayList<>();

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        // no attributes.
    }

    public ExtensionElementsConverter(XStream xStream, List<DMNExtensionRegister> extensionRegisters) {
        super(xStream);
        if ( !extensionRegisters.isEmpty() ) {
            this.extensionRegisters.addAll(extensionRegisters);
        }
    }


    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Object obj = createModelObject();
        assignAttributes( reader, obj );
        if(extensionRegisters.size() == 0) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                // skipping nodeName
                reader.moveUp();
            }
        } else {
            parseElements( reader, context, obj );
        }
        return obj;
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        // no attributes.
    }

    public ExtensionElementsConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new DMNElement.ExtensionElements();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( ExtensionElements.class );
    }

    @Override
    public void assignChildElement(Object parent, String nodeName, Object child) {
        ExtensionElements id = (ExtensionElements)parent;
        id.getAny().add(child);
    }

}
