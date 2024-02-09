/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.trisotech.backend.marshalling.v1_3.xstream;

import org.kie.dmn.backend.marshalling.v1_3.xstream.ExpressionConverter;
import org.kie.dmn.backend.marshalling.v1x.ConverterDefinesExpressionNodeName;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.trisotech.model.api.Iterator;
import org.kie.dmn.trisotech.model.api.NamedExpression;
import org.kie.dmn.trisotech.model.v1_3.TIterator;
import org.kie.dmn.trisotech.model.v1_3.TNamedExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class IteratorConverter extends ExpressionConverter implements ConverterDefinesExpressionNodeName {

    public static final String VARIABLE = "iteratorVariable";
    public static final String TYPE = "iteratorType";
    public static final String IN = "in";
    public static final String RETURN = "return";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Iterator filter = (Iterator) parent;

        if (IN.equals(nodeName)) {
            Expression expression = ((NamedExpression) child).getExpression();
            if (((NamedExpression) child).getTypeRef() != null) {
                expression.setTypeRef(((NamedExpression) child).getTypeRef());
            }
            filter.setIn(expression);
        } else if (RETURN.equals(nodeName)) {
            filter.setReturn(((NamedExpression) child).getExpression());
        } else {
            super.assignChildElement(parent, nodeName, child);
        }

    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Iterator filter = (Iterator) parent;
        String type = reader.getAttribute(TYPE);
        if (type == null) {
            type = "for";
        }
        switch (type) {
            case "for":
            default:
                filter.setIteratorType(Iterator.IteratorType.FOR);
                break;
            case "some":
                filter.setIteratorType(Iterator.IteratorType.SOME);
                break;
            case "every":
                filter.setIteratorType(Iterator.IteratorType.EVERY);
                break;
        }
        filter.setVariable(reader.getAttribute(VARIABLE));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Iterator it = (Iterator) parent;
        writeChildrenNode(writer, context, new TNamedExpression(IN, it.getIn(), it.getTypeRef()), IN);
        writeChildrenNode(writer, context, new TNamedExpression(RETURN, it.getReturn()), RETURN);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Iterator it = (Iterator) parent;
        writer.addAttribute(TYPE, it.getIteratorType().toString().toLowerCase());
        writer.addAttribute(VARIABLE, it.getVariable());
    }

    public IteratorConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TIterator();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TIterator.class);
    }

    @Override
    public String defineExpressionNodeName(Expression e) {
        return "iterator";
    }

}
