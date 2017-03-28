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
import org.kie.dmn.model.v1_1.LiteralExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class LiteralExpressionConverter
        extends ExpressionConverter {

    public static final String IMPORTED_VALUES = "importedValues";
    public static final String TEXT = "text";
    public static final String EXPR_LANGUAGE = "expressionLanguage";

    public LiteralExpressionConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals( LiteralExpression.class );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        LiteralExpression le = (LiteralExpression)parent;
        
        if( TEXT.equals( nodeName ) ) {
            le.setText( (String) child );
        } else if( IMPORTED_VALUES.equals( nodeName ) ) {
            le.setImportedValues( (ImportedValues) child );
        } else {
            super.assignChildElement( parent, nodeName, child );
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        LiteralExpression le = (LiteralExpression) parent;
        
        String exprLanguage = reader.getAttribute( EXPR_LANGUAGE );

        le.setExpressionLanguage( exprLanguage );
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new LiteralExpression();
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        LiteralExpression le = (LiteralExpression) parent;
        
        if ( le.getText() != null ) writeChildrenNodeAsValue(writer, context, le.getText(), TEXT);
        // TODO Or if-else ?
        if ( le.getImportedValues() != null ) writeChildrenNode(writer, context, le.getImportedValues(), IMPORTED_VALUES);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        LiteralExpression le = (LiteralExpression) parent;
        
        if ( le.getExpressionLanguage() != null ) writer.addAttribute(EXPR_LANGUAGE, le.getExpressionLanguage());
    }

    
}
