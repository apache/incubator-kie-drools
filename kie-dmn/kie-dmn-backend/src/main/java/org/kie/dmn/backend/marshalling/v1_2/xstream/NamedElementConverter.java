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

package org.kie.dmn.backend.marshalling.v1_2.xstream;

import org.kie.dmn.model.api.*;
import org.kie.dmn.model.v1_2.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class NamedElementConverter
        extends DMNElementConverter {
    private static final String NAME = "name";

    public NamedElementConverter(XStream xstream) {
        super( xstream );
    }

    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement( parent, nodeName, child );
    }

    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        String name = reader.getAttribute( NAME );
        ((NamedElement) parent).setName( name );
    }
    
    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        
        // no children.
    }
    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        NamedElement ne = (NamedElement) parent;
        
        writer.addAttribute( NAME , ne.getName() );
    }
}
