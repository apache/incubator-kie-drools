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
import org.junit.Assert;
import org.junit.Test;
import org.kie.internal.ChangeSet;
import org.kie.api.io.Resource;

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
        
        Assert.assertNotNull(changeSet);
        
        Collection<Resource> resourcesAdded = changeSet.getResourcesAdded();
        
        Assert.assertNotNull(resourcesAdded);
        
        Assert.assertEquals(4, resourcesAdded.size());
        
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
        
        Assert.assertNotNull(resource1);
        Assert.assertNotNull(resource2);
        Assert.assertNotNull(resource3);
        Assert.assertNotNull(secureResource);
        
        Assert.assertNull(resource1.getDescription());
        
        Assert.assertEquals("another description", resource2.getDescription());
        
        Assert.assertEquals("some useful description", secureResource.getDescription());
        
    }
}
