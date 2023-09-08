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
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.model.v1_3.TOutputClause;

public class OutputClauseConverter extends DMNElementConverter {
    public static final String DEFAULT_OUTPUT_ENTRY = "defaultOutputEntry";
    public static final String OUTPUT_VALUES = "outputValues";
    public static final String NAME = "name";
    public static final String TYPE_REF ="typeRef";
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        OutputClause oc = (OutputClause) parent;
        
        if (OUTPUT_VALUES.equals(nodeName)) {
            oc.setOutputValues((UnaryTests) child);
        } else if (DEFAULT_OUTPUT_ENTRY.equals(nodeName)) {
            oc.setDefaultOutputEntry((LiteralExpression) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        OutputClause oc = (OutputClause) parent;
        
        String name = reader.getAttribute(NAME);
        String typeRefValue = reader.getAttribute(TYPE_REF);
        
        oc.setName(name);
        if (typeRefValue != null) oc.setTypeRef(MarshallingUtils.parseQNameString(typeRefValue));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        OutputClause oc = (OutputClause) parent;
        
        if (oc.getOutputValues() != null) writeChildrenNode(writer, context, oc.getOutputValues(), OUTPUT_VALUES);
        if (oc.getDefaultOutputEntry() != null) writeChildrenNode(writer, context, oc.getDefaultOutputEntry(), DEFAULT_OUTPUT_ENTRY);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        OutputClause oc = (OutputClause) parent;
        
        if (oc.getName() != null) writer.addAttribute(NAME, oc.getName());
        if (oc.getTypeRef() != null) {
            writer.addAttribute(TYPE_REF, MarshallingUtils.formatQName(oc.getTypeRef(), oc));
        }
    }

    public OutputClauseConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TOutputClause();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TOutputClause.class);
    }

}
