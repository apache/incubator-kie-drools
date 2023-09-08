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
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.v1_4.TFunctionItem;

public class FunctionItemConverter extends DMNElementConverter {

    private static final String OUTPUT_TYPE_REF = "outputTypeRef";
    private static final String PARAMETERS = "parameters";

    public FunctionItemConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals(TFunctionItem.class);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        FunctionItem ii = (FunctionItem) parent;

        if (PARAMETERS.equals(nodeName)) {
            ii.getParameters().add((InformationItem) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        FunctionItem ii = (FunctionItem) parent;

        String typeRef = reader.getAttribute(OUTPUT_TYPE_REF);
        ii.setOutputTypeRef(MarshallingUtils.parseQNameString(typeRef));
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TFunctionItem();
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);

        FunctionItem ii = (FunctionItem) parent;

        for (InformationItem ic : ii.getParameters()) {
            writeChildrenNode(writer, context, ic, PARAMETERS);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        FunctionItem ii = (FunctionItem) parent;
        
        if (ii.getOutputTypeRef() != null) {
            writer.addAttribute(OUTPUT_TYPE_REF, MarshallingUtils.formatQName(ii.getOutputTypeRef(), ii));
        }
    }

}
