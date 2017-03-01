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

import org.kie.dmn.model.v1_1.Association;
import org.kie.dmn.model.v1_1.AssociationDirection;
import org.kie.dmn.model.v1_1.DMNElementReference;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class AssociationConverter extends ArtifactConverter {
    public static final String TARGET_REF = "targetRef";
    public static final String SOURCE_REF = "sourceRef";
    public static final String ASSOCIATION_DIRECTION = "associationDirection";

    public AssociationConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new Association();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( Association.class );
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
