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

import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat(changeSet).isNotNull();
        
        Collection<Resource> resourcesAdded = changeSet.getResourcesAdded();

        assertThat(resourcesAdded).isNotNull();

        assertThat(resourcesAdded.size()).isEqualTo(4);
        
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

        assertThat(resource1).isNotNull();
        assertThat(resource2).isNotNull();
        assertThat(resource3).isNotNull();
        assertThat(secureResource).isNotNull();

        assertThat(resource1.getDescription()).isNull();

        assertThat(resource2.getDescription()).isEqualTo("another description");

        assertThat(secureResource.getDescription()).isEqualTo("some useful description");

        assertThat(changeSetReader.getParser().getAttrs().getLength()).isEqualTo(2);
        assertThat(changeSetReader.getParser().getAttrs().getValue("type")).isEqualTo("DRL");
    }
}
