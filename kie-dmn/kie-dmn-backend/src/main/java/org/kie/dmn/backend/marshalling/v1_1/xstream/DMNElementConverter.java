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

import org.kie.dmn.model.v1_1.DMNElement;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class DMNElementConverter
        extends DMNModelInstrumentedBaseConverter {
    public static final String ID          = "id";
    public static final String LABEL       = "label";
    public static final String DESCRIPTION = "description";
    public static final String EXTENSION_ELEMENTS = "extensionElements";

    public DMNElementConverter(XStream xstream) {
        super( xstream );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        if ( DESCRIPTION.equals( nodeName ) && child instanceof String ) {
            ((DMNElement) parent).setDescription( (String) child );
        } else if(EXTENSION_ELEMENTS.equals(nodeName)
                && child instanceof DMNElement.ExtensionElements) {
            ((DMNElement)parent).setExtensionElements((DMNElement.ExtensionElements)child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        String id = reader.getAttribute( ID );
        String label = reader.getAttribute( LABEL );

        DMNElement dmne = (DMNElement) parent;

        dmne.setId( id );
        dmne.setLabel( label );
    }
    
    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DMNElement e = (DMNElement) parent;

        if (e.getDescription() !=null) writeChildrenNodeAsValue(writer, context, e.getDescription(), DESCRIPTION);
        // TODO what about DMNElement.ExtensionElements extensionElements;
    }
    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        DMNElement e = (DMNElement) parent;
        
        if (e.getId() != null) writer.addAttribute( ID , e.getId() );
        if (e.getLabel() != null) writer.addAttribute( LABEL , e.getLabel() );
    }
}
