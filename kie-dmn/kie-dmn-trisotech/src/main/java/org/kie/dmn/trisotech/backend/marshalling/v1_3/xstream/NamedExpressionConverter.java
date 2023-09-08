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

import org.kie.dmn.backend.marshalling.v1_3.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.backend.marshalling.v1_3.xstream.MarshallingUtils;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.trisotech.model.api.NamedExpression;
import org.kie.dmn.trisotech.model.v1_3.TNamedExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class NamedExpressionConverter extends DMNModelInstrumentedBaseConverter {

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        NamedExpression namedExp = (NamedExpression) parent;

        if (child instanceof Expression) {
            namedExp.setExpression((Expression) child);
            namedExp.setName(nodeName);
        } else
            super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        NamedExpression exp = (NamedExpression) parent;
        String typeRef = reader.getAttribute("typeRef");
        exp.setTypeRef(MarshallingUtils.parseQNameString(typeRef));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        NamedExpression namedExp = (NamedExpression) parent;
        writeChildrenNode(writer, context, namedExp.getExpression(), MarshallingUtils.defineExpressionNodeName(xstream, namedExp.getExpression()));
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        NamedExpression exp = (NamedExpression) parent;
        if (exp.getTypeRef() != null) {
            writer.addAttribute("typeRef", MarshallingUtils.formatQName(exp.getTypeRef(), exp));
        }

    }

    public NamedExpressionConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TNamedExpression();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(TNamedExpression.class);
    }

}
