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
import org.kie.dmn.model.v1_1.InputClause;
import org.kie.dmn.model.v1_1.LiteralExpression;
import org.kie.dmn.model.v1_1.UnaryTests;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class InputClauseConverter extends DMNElementConverter {
    public static final String INPUT_VALUES = "inputValues";
    public static final String INPUT_EXPRESSION = "inputExpression";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        InputClause ic = (InputClause) parent;
        
        if (INPUT_EXPRESSION.equals(nodeName)) {
            ic.setInputExpression((LiteralExpression) child);
        } else if (INPUT_VALUES.equals(nodeName)) {
            ic.setInputValues((UnaryTests) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        InputClause ic = (InputClause) parent;
        
        writeChildrenNode(writer, context, ic.getInputExpression(), INPUT_EXPRESSION);
        if (ic.getInputValues() != null) writeChildrenNode(writer, context, ic.getInputValues(), INPUT_VALUES); 
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
    }

    public InputClauseConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new InputClause();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( InputClause.class );
    }

}
