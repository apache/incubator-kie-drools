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
import org.kie.dmn.trisotech.model.api.Conditional;
import org.kie.dmn.trisotech.model.api.NamedExpression;
import org.kie.dmn.trisotech.model.v1_3.TConditional;
import org.kie.dmn.trisotech.model.v1_3.TNamedExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ConditionalConverter extends ExpressionConverter implements ConverterDefinesExpressionNodeName {

    public static final String IF = "if";
    public static final String THEN = "then";
    public static final String ELSE = "else";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Conditional cond = (Conditional) parent;

        if (IF.equals(nodeName)) {
            cond.setIf(((NamedExpression) child).getExpression());
        } else if (THEN.equals(nodeName)) {
            cond.setThen(((NamedExpression) child).getExpression());
        } else if (ELSE.equals(nodeName)) {
            cond.setElse(((NamedExpression) child).getExpression());
        } else {
            super.assignChildElement(parent, nodeName, child);
        }

    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Conditional cond = (Conditional) parent;
        writeChildrenNode(writer, context,  new TNamedExpression(IF,cond.getIf()), IF);
        writeChildrenNode(writer, context, new TNamedExpression(THEN,cond.getThen()), THEN);
        writeChildrenNode(writer, context,new TNamedExpression(ELSE,cond.getElse()), ELSE);

    }

    public ConditionalConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TConditional();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TConditional.class);
    }

    @Override
    public String defineExpressionNodeName(Expression e) {
        return "conditional";
    }

}
