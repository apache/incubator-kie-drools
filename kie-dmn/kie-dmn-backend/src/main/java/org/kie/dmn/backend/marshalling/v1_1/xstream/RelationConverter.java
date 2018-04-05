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
import org.kie.dmn.model.v1_1.InformationItem;
import org.kie.dmn.model.v1_1.Relation;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class RelationConverter extends ExpressionConverter {
    public static final String EXPRESSION = "expression";
    public static final String ROW = "row";
    public static final String COLUMN = "column";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Relation r = (Relation) parent;
        
        if (COLUMN.equals(nodeName)) {
            r.getColumn().add((InformationItem) child);
        } else if (ROW.equals(nodeName)) {
            r.getRow().add((org.kie.dmn.model.v1_1.List) child);
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
        Relation r = (Relation) parent;
        
        for (InformationItem c : r.getColumn()) {
            writeChildrenNode(writer, context, c, COLUMN);
        }
        for (org.kie.dmn.model.v1_1.List row : r.getRow()) {
            writeChildrenNode(writer, context, row, ROW);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public RelationConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new Relation();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( Relation.class );
    }

}
