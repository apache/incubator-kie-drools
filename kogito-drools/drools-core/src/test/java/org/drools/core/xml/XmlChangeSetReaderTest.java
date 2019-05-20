/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.xml;

import java.util.Collection;

import org.drools.core.io.internal.InternalResource;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.internal.ChangeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 */
public class XmlChangeSetReaderTest {
    
    @Test
    public void testResourceAttributes() throws Exception{
        SemanticModules semanticModules = new SemanticModules();
        semanticModules.addSemanticModule( new ChangeSetSemanticModule() );
        
        XmlChangeSetReader changeSetReader = new XmlChangeSetReader(semanticModules);

        changeSetReader.setClassLoader(XmlChangeSetReaderTest.class.getClassLoader(),
                                   null );
        
        ChangeSet changeSet = changeSetReader.read(XmlChangeSetReaderTest.class.getClassLoader().getResourceAsStream("org/drools/core/xml/test-change-set.xml"));
        
        assertNotNull(changeSet);
        
        Collection<Resource> resourcesAdded = changeSet.getResourcesAdded();
        
        assertNotNull(resourcesAdded);
        
        assertEquals(4, resourcesAdded.size());
        
        InternalResource resource1 = null;
        InternalResource resource2 = null;
        InternalResource resource3 = null;
        InternalResource secureResource = null;
        
        for (Resource r : resourcesAdded) {
            InternalResource resource = (InternalResource) r;
            if (resource.getSourcePath() != null && resource.getSourcePath().equals("resource1")){
                resource1 = resource;
            } else if (resource.getSourcePath() != null && resource.getSourcePath().equals("secureResource")){
                secureResource = resource;
            } else if (resource.getSourcePath() == null && resource.getDescription() == null){
                resource3 = resource;
            } else if (resource.getSourcePath() == null){
                resource2 = resource;
            }
        }
        
        assertNotNull(resource1);
        assertNotNull(resource2);
        assertNotNull(resource3);
        assertNotNull(secureResource);
        
        assertNull(resource1.getDescription());
        
        assertEquals("another description", resource2.getDescription());
        
        assertEquals("some useful description", secureResource.getDescription());

        assertEquals(2, changeSetReader.getParser().getAttrs().getLength());
        assertEquals("DRL", changeSetReader.getParser().getAttrs().getValue("type"));
    }
}
