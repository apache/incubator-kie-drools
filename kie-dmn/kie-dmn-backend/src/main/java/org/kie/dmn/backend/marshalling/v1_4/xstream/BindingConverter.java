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
package org.kie.dmn.backend.marshalling.v1_4.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.Binding;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.v1_4.TBinding;

public class BindingConverter extends DMNModelInstrumentedBaseConverter {
    public static final String EXPRESSION = "expression";
    public static final String PARAMETER = "parameter";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Binding b = (Binding) parent;
        
        if (PARAMETER.equals(nodeName)) {
            b.setParameter((InformationItem) child);
        } else if (child instanceof Expression) {
            b.setExpression((Expression) child);
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
        Binding b = (Binding) parent;
        
        writeChildrenNode(writer, context, b.getParameter(), PARAMETER);
        if (b.getExpression() != null) writeChildrenNode(writer, context, b.getExpression(), MarshallingUtils.defineExpressionNodeName(xstream, b.getExpression()));
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public BindingConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TBinding();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TBinding.class);
    }

}
