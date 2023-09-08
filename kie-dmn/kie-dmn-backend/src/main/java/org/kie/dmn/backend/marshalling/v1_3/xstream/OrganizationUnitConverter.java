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
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.OrganizationUnit;
import org.kie.dmn.model.v1_3.TOrganizationUnit;

public class OrganizationUnitConverter extends BusinessContextElementConverter {
    public static final String DECISION_OWNED = "decisionOwned";
    public static final String DECISION_MADE = "decisionMade";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        OrganizationUnit ou = (OrganizationUnit) parent;
        
        if (DECISION_MADE.equals(nodeName)) {
            ou.getDecisionMade().add((DMNElementReference) child);
        } else if (DECISION_OWNED.equals(nodeName)) {
            ou.getDecisionOwned().add((DMNElementReference) child);
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
        OrganizationUnit ou = (OrganizationUnit) parent;
        
        for (DMNElementReference dm : ou.getDecisionMade()) {
            writeChildrenNode(writer, context, dm, DECISION_MADE);
        }
        for (DMNElementReference downed : ou.getDecisionOwned()) {
            writeChildrenNode(writer, context, downed, DECISION_OWNED);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public OrganizationUnitConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TOrganizationUnit();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TOrganizationUnit.class);
    }

}
