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

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.model.v1_3.TItemDefinition;

public class ItemDefinitionConverter extends NamedElementConverter {
    public static final String ITEM_COMPONENT = "itemComponent";
    public static final String ALLOWED_VALUES = "allowedValues";
    public static final String TYPE_REF = "typeRef";
    public static final String TYPE_LANGUAGE = "typeLanguage";
    public static final String IS_COLLECTION = "isCollection";
    public static final String FUNCTION_ITEM = "functionItem";
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        ItemDefinition id = (ItemDefinition) parent;
        
        if (TYPE_REF.equals(nodeName)) {
            id.setTypeRef((QName) child);
        } else if (ALLOWED_VALUES.equals(nodeName)) {
            id.setAllowedValues((UnaryTests) child);
        } else if (ITEM_COMPONENT.equals(nodeName)) {
            id.getItemComponent().add((ItemDefinition) child);
        } else if (FUNCTION_ITEM.equals(nodeName)) {
            id.setFunctionItem((FunctionItem) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        ItemDefinition id = (ItemDefinition) parent;
        
        String typeLanguage = reader.getAttribute(TYPE_LANGUAGE);
        String isCollectionValue = reader.getAttribute(IS_COLLECTION);
        
        id.setTypeLanguage(typeLanguage);
        id.setIsCollection(Boolean.valueOf(isCollectionValue));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        ItemDefinition id = (ItemDefinition) parent;
        
        if (id.getTypeRef() != null) writeChildrenNode(writer, context, id.getTypeRef(), TYPE_REF);
        if (id.getAllowedValues() != null) writeChildrenNode(writer, context, id.getAllowedValues(), ALLOWED_VALUES);
        for ( ItemDefinition ic : id.getItemComponent() ) {
            writeChildrenNode(writer, context, ic, ITEM_COMPONENT);
        }
        if (id.getFunctionItem() != null) {
            writeChildrenNode(writer, context, id.getFunctionItem(), FUNCTION_ITEM);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        ItemDefinition id = (ItemDefinition) parent;
        
        if (id.getTypeLanguage() != null) writer.addAttribute(TYPE_LANGUAGE, id.getTypeLanguage());
        writer.addAttribute(IS_COLLECTION, Boolean.valueOf(id.isIsCollection()).toString());
    }

    public ItemDefinitionConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TItemDefinition();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TItemDefinition.class);
    }

}
