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
package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.BusinessKnowledgeModel;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.KnowledgeRequirement;
import org.kie.dmn.model.v1_2.TBusinessKnowledgeModel;

public class BusinessKnowledgeModelConverter extends InvocableConverter {
    public static final String ENCAPSULATED_LOGIC = "encapsulatedLogic";
    public static final String VARIABLE = "variable";
    public static final String KNOWLEDGE_REQUIREMENT = "knowledgeRequirement";
    public static final String AUTHORITY_REQUIREMENT = "authorityRequirement";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) parent;
        
        if (ENCAPSULATED_LOGIC.equals(nodeName)) {
            bkm.setEncapsulatedLogic((FunctionDefinition) child);
        } else if (KNOWLEDGE_REQUIREMENT.equals(nodeName)) {
            bkm.getKnowledgeRequirement().add((KnowledgeRequirement) child);
        } else if (AUTHORITY_REQUIREMENT.equals(nodeName)) {
            bkm.getAuthorityRequirement().add((AuthorityRequirement) child);
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
        BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) parent;
        
        if (bkm.getEncapsulatedLogic() != null) writeChildrenNode(writer, context, bkm.getEncapsulatedLogic(), ENCAPSULATED_LOGIC);
        // Now as Invocable: if (bkm.getVariable() != null) writeChildrenNode(writer, context, bkm.getVariable(), VARIABLE);
        for (KnowledgeRequirement i : bkm.getKnowledgeRequirement()) {
            writeChildrenNode(writer, context, i, KNOWLEDGE_REQUIREMENT);
        }
        for (AuthorityRequirement a : bkm.getAuthorityRequirement()) {
            writeChildrenNode(writer, context, a, AUTHORITY_REQUIREMENT);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public BusinessKnowledgeModelConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TBusinessKnowledgeModel();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TBusinessKnowledgeModel.class);
    }

}
