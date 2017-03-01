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

import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public abstract class DMNBaseConverter
        extends AbstractCollectionConverter {

    public DMNBaseConverter(Mapper mapper) {
        super( mapper );
    }

    public void marshal(
            Object object,
            HierarchicalStreamWriter writer,
            MarshallingContext context) {
        writeAttributes(writer, object);
        writeChildren(writer, context, object);
    }
    
    protected void writeChildrenNode(HierarchicalStreamWriter writer, MarshallingContext context, Object node, String nodeAlias) {
        writer.startNode(nodeAlias);
        context.convertAnother(node);
        writer.endNode();
    }
    
    protected void writeChildrenNodeAsValue(HierarchicalStreamWriter writer, MarshallingContext context, String nodeValue, String nodeAlias) {
        writer.startNode(nodeAlias);
        writer.setValue(nodeValue);
        writer.endNode();
    }

    protected abstract void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent);

    protected abstract void writeAttributes(HierarchicalStreamWriter writer, Object parent);

    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        DMNModelInstrumentedBase obj = createModelObject();
        assignAttributes( reader, obj );
        parseElements( reader, context, obj );
        return obj;
    }

    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            Object object = readItem(
                    reader,
                    context,
                    null );
            if( object instanceof DMNModelInstrumentedBase ) {
                ((DMNModelInstrumentedBase) object).setParent( (DMNModelInstrumentedBase) parent );
                ( (DMNModelInstrumentedBase)parent).addChildren((DMNModelInstrumentedBase) object);
            }
            reader.moveUp();
            assignChildElement( parent, nodeName, object );
        }
    }

    protected abstract DMNModelInstrumentedBase createModelObject();

    protected abstract void assignChildElement(Object parent, String nodeName, Object child);

    protected abstract void assignAttributes(HierarchicalStreamReader reader, Object parent);

}
