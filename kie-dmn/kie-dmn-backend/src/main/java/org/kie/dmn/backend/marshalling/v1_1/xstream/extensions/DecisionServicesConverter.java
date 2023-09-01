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
package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.backend.marshalling.v1_1.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.extensions.DecisionServices;

public class DecisionServicesConverter extends DMNModelInstrumentedBaseConverter {

    public static final String DECISION_SERVICE = "decisionService";

    public DecisionServicesConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new DecisionServices();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(DecisionServices.class);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DecisionServices decisionServices = (DecisionServices) parent;

        // We need to clear the namespace prefix for drools:KIE because DMNModelInstrumentedBaseConverter#writeAttributes will try to write it again.
        // The namespace is registered in the XStreamMapper#configureQNameMap method by the extension register
        decisionServices.getNsContext().remove("drools", KieDMNModelInstrumentedBase.URI_KIE);

        switch (nodeName) {
        case DECISION_SERVICE:
            decisionServices.getDecisionService().add((DecisionService) child);
            break;
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DecisionServices decisionServices = (DecisionServices) parent;

        for (DecisionService ds : decisionServices.getDecisionService()) {
            writeChildrenNode(writer, context, ds, DECISION_SERVICE);
        }
    }

}
