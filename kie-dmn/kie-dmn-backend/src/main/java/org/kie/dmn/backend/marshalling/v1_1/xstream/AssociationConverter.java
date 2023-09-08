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
package org.kie.dmn.backend.marshalling.v1_1.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.Association;
import org.kie.dmn.model.api.AssociationDirection;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.TAssociation;

public class AssociationConverter extends ArtifactConverter {
    public static final String TARGET_REF = "targetRef";
    public static final String SOURCE_REF = "sourceRef";
    public static final String ASSOCIATION_DIRECTION = "associationDirection";

    public AssociationConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TAssociation();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TAssociation.class);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Association a = (Association) parent;
        
        if( SOURCE_REF.equals( nodeName ) ) {
            a.setSourceRef( (DMNElementReference) child );
        } else if( TARGET_REF.equals( nodeName ) ) {
            a.setTargetRef( (DMNElementReference) child );
        } else {
            super.assignChildElement( parent, nodeName, child );
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Association a = (Association) parent;
        
        String associationDirectionValue = reader.getAttribute(ASSOCIATION_DIRECTION);
        
        if (associationDirectionValue != null) a.setAssociationDirection(AssociationDirection.fromValue(associationDirectionValue));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Association a = (Association) parent;
        
        writeChildrenNode(writer, context, a.getSourceRef(), SOURCE_REF);
        writeChildrenNode(writer, context, a.getTargetRef(), TARGET_REF);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Association a = (Association) parent;
        
        if (a.getAssociationDirection() != null) writer.addAttribute(ASSOCIATION_DIRECTION, a.getAssociationDirection().value());
    }
}
