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
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.PerformanceIndicator;
import org.kie.dmn.model.v1_4.TPerformanceIndicator;

public class PerformanceIndicatorConverter extends BusinessContextElementConverter {
    public static final String IMPACTING_DECISION = "impactingDecision";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        PerformanceIndicator pi = (PerformanceIndicator) parent;
        
        if (IMPACTING_DECISION.equals(nodeName)) {
            pi.getImpactingDecision().add((DMNElementReference) child);
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
        PerformanceIndicator pi = (PerformanceIndicator) parent;
        
        for ( DMNElementReference id : pi.getImpactingDecision() ) {
            writeChildrenNode(writer, context, id, IMPACTING_DECISION);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public PerformanceIndicatorConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TPerformanceIndicator();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TPerformanceIndicator.class);
    }

}
