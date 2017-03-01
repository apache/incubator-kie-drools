/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.backend.marshalling.v1_1.xstream;

import org.kie.dmn.model.v1_1.DMNElementReference;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.InformationRequirement;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class InformationRequirementConverter
        extends DMNModelInstrumentedBaseConverter {

    private static final String REQUIRED_INPUT    = "requiredInput";
    private static final String REQUIRED_DECISION = "requiredDecision";

    public InformationRequirementConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals( InformationRequirement.class );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        InformationRequirement ir = (InformationRequirement) parent;
        
        if ( REQUIRED_INPUT.equals( nodeName ) ) {
            ir.setRequiredInput( (DMNElementReference) child );
        } else if ( REQUIRED_DECISION.equals( nodeName ) ) {
            ir.setRequiredDecision( (DMNElementReference) child );
        } else {
            super.assignChildElement( parent, nodeName, child );
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new InformationRequirement();
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        InformationRequirement ir = (InformationRequirement) parent;
        
        if ( ir.getRequiredDecision() != null ) {
            writeChildrenNode(writer, context, ir.getRequiredDecision(), REQUIRED_DECISION);
        }
        // TODO or if else ?
        if ( ir.getRequiredInput() != null ) {
            writeChildrenNode(writer, context, ir.getRequiredInput(), REQUIRED_INPUT);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    

}
