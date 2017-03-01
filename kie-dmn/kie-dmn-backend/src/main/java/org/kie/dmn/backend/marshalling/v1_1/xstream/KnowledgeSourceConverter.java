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

import org.kie.dmn.model.v1_1.AuthorityRequirement;
import org.kie.dmn.model.v1_1.DMNElementReference;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.KnowledgeSource;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class KnowledgeSourceConverter extends DRGElementConverter {
    public static final String OWNER = "owner";
    public static final String TYPE = "type";
    public static final String AUTHORITY_REQUIREMENT = "authorityRequirement";
    public static final String LOCATION_URI = "locationURI";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        KnowledgeSource ks = (KnowledgeSource) parent;
        
        if (AUTHORITY_REQUIREMENT.equals(nodeName)) {
            ks.getAuthorityRequirement().add((AuthorityRequirement) child);
        } else if (TYPE.equals(nodeName)) {
            ks.setType((String) child);
        } else if (OWNER.equals(nodeName)) {
            ks.setOwner((DMNElementReference) child);
        } else { 
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        KnowledgeSource ks = (KnowledgeSource) parent;
        
        String locationUri = reader.getAttribute(LOCATION_URI);
        
        ks.setLocationURI(locationUri);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        KnowledgeSource ks = (KnowledgeSource) parent;
        
        for ( AuthorityRequirement ar : ks.getAuthorityRequirement() ) {
            writeChildrenNode(writer, context, ar, AUTHORITY_REQUIREMENT);
        }
        if (ks.getType() != null) writeChildrenNode(writer, context, ks.getType(), TYPE);
        if (ks.getOwner() != null) writeChildrenNode(writer, context, ks.getOwner(), OWNER);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        KnowledgeSource ks = (KnowledgeSource) parent;
        
        if (ks.getLocationURI() != null) writer.addAttribute(LOCATION_URI, ks.getLocationURI());
    }

    public KnowledgeSourceConverter(XStream xstream) {
        super(xstream);
    }
    
    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new KnowledgeSource();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( KnowledgeSource.class );
    }

}
