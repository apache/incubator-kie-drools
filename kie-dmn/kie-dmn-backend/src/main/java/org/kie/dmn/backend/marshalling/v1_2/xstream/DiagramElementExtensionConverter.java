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

package org.kie.dmn.backend.marshalling.v1_2.xstream;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1_2.dmndi.DiagramElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagramElementExtensionConverter extends DMNModelInstrumentedBaseConverter {

    private static final Logger LOG = LoggerFactory.getLogger(DiagramElementExtensionConverter.class);

    private List<DMNExtensionRegister> extensionRegisters = new ArrayList<>();

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        // no attributes.
    }

    public DiagramElementExtensionConverter(XStream xStream, List<DMNExtensionRegister> extensionRegisters) {
        super(xStream);
        if (!extensionRegisters.isEmpty()) {
            this.extensionRegisters.addAll(extensionRegisters);
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        DMNModelInstrumentedBase obj = createModelObject();
        assignAttributes(reader, obj);
        if (extensionRegisters.size() == 0) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                // skipping nodeName
                reader.moveUp();
            }
        } else {
            // do as default behavior, but in case cannot unmarshall an extension element child, just skip it.
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                try {
                    Object object = readItem(reader, context, null);
                    if (object instanceof DMNModelInstrumentedBase) {
                        ((KieDMNModelInstrumentedBase) object).setParent(obj);
                        ((KieDMNModelInstrumentedBase) obj).addChildren((KieDMNModelInstrumentedBase) object);
                    }
                    assignChildElement(obj, nodeName, object);
                } catch (CannotResolveClassException e) {
                    // do nothing; I tried to convert the extension element child with the converters, but no converter is registered for this child.
                    LOG.debug("Tried to convert the extension element child {}, but no converter is registered for this child.", nodeName);
                }
                reader.moveUp();
            }
        }
        return obj;
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        // no attributes.
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);

        if (extensionRegisters.size() == 0) {
            return;
        }

        org.kie.dmn.model.api.dmndi.DiagramElement.Extension ee = (org.kie.dmn.model.api.dmndi.DiagramElement.Extension) parent;
        if (ee.getAny() != null) {
            for (Object a : ee.getAny()) {
                writeItem(a, context, writer);
            }
        }
    }

    public DiagramElementExtensionConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new DiagramElement.Extension();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(DiagramElement.Extension.class);
    }

    @Override
    public void assignChildElement(Object parent, String nodeName, Object child) {
        org.kie.dmn.model.api.dmndi.DiagramElement.Extension id = (org.kie.dmn.model.api.dmndi.DiagramElement.Extension) parent;
        id.getAny().add(child);
    }

}
