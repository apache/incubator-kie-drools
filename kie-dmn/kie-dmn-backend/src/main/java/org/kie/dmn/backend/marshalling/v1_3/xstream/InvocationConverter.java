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
package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.Binding;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.Invocation;
import org.kie.dmn.model.v1_3.TInvocation;

public class InvocationConverter extends ExpressionConverter {
    public static final String BINDING = "binding";
    public static final String EXPRESSION = "expression";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Invocation i = (Invocation) parent;
        
        if (child instanceof Expression) {
            i.setExpression((Expression) child);
        } else if (BINDING.equals(nodeName)) {
            i.getBinding().add((Binding) child);
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
        Invocation i = (Invocation) parent;
        
        if (i.getExpression() != null) writeChildrenNode(writer, context, i.getExpression(), MarshallingUtils.defineExpressionNodeName(xstream, i.getExpression()));
        for (Binding b : i.getBinding()) {
            writeChildrenNode(writer, context, b, BINDING);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
    }

    public InvocationConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TInvocation();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TInvocation.class);
    }

}
