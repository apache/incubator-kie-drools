/*
 * Copyright 2011 JBoss by Red Hat.
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
package org.drools.xml;

import java.util.Collection;
import org.drools.ChangeSet;
import org.drools.io.Resource;
import org.junit.Assert;
import org.junit.Test;

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
        
        ChangeSet changeSet = changeSetReader.read(XmlChangeSetReaderTest.class.getClassLoader().getResourceAsStream("org/drools/xml/test-change-set.xml"));
        
        Assert.assertNotNull(changeSet);
        
        Collection<Resource> resourcesAdded = changeSet.getResourcesAdded();
        
        Assert.assertNotNull(resourcesAdded);
        
        Assert.assertEquals(4, resourcesAdded.size());
        
        Resource resource1 = null;
        Resource resource2 = null;
        Resource resource3 = null;
        Resource secureResource = null;
        
        for (Resource resource : resourcesAdded) {
            if (resource.getName() != null && resource.getName().equals("resource1")){
                resource1 = resource;
            } else if (resource.getName() != null && resource.getName().equals("secureResource")){
                secureResource = resource;
            } else if (resource.getName() == null && resource.getDescription() == null){
                resource3 = resource;
            } else if (resource.getName() == null){
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
