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
import org.kie.dmn.model.v1_1.ImportedValues;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ImportedValuesConverter extends ImportConverter {
    public static final String IMPORTED_ELEMENT = "importedElement";
    public static final String EXPRESSION_LANGUAGE = "expressionLanguage";

    public ImportedValuesConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        ImportedValues iv = (ImportedValues) parent;
        
        if (IMPORTED_ELEMENT.equals(nodeName)) {
            iv.setImportedElement((String) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        ImportedValues iv = (ImportedValues) parent;
        
        String expressionLanguage = reader.getAttribute(EXPRESSION_LANGUAGE);
        
        iv.setExpressionLanguage(expressionLanguage);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        ImportedValues iv = (ImportedValues) parent;
        
        writeChildrenNode(writer, context, iv.getImportedElement(), IMPORTED_ELEMENT);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        ImportedValues iv = (ImportedValues) parent;
        
        if (iv.getExpressionLanguage() != null) writer.addAttribute(EXPRESSION_LANGUAGE, iv.getExpressionLanguage());
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new ImportedValues();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( ImportedValues.class );
    }
    
    
}
