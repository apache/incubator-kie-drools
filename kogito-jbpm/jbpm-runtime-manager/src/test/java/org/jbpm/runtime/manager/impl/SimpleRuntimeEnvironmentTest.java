/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.runtime.manager.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.io.ResourceFactory;
import org.mockito.ArgumentCaptor;

public class SimpleRuntimeEnvironmentTest extends SimpleRuntimeEnvironment {

    @Before
    public void before() { 
        this.kbuilder = mock(KnowledgeBuilder.class);
    }
    
    @Test
    public void addAssetCsvXlsTest() { 
       doNothing().when(this.kbuilder).add(any(Resource.class), any(ResourceType.class), any(ResourceConfiguration.class));
       
       doThrow(new IllegalStateException("CSV resource not handled correctly!")).when(this.kbuilder).add(any(Resource.class), any(ResourceType.class));
       Resource resource = ResourceFactory.newClassPathResource("/data/resource.csv", getClass());
       addAsset(resource, ResourceType.DTABLE);
       
       doThrow(new IllegalStateException("XLS resource not handled correctly!")).when(this.kbuilder).add(any(Resource.class), any(ResourceType.class));
       resource = ResourceFactory.newClassPathResource("/data/resource.xls", getClass());
       addAsset(resource, ResourceType.DTABLE);
    
       // control test
       doThrow(new IllegalStateException("BPMN2 resource not handled correctly!")).when(this.kbuilder).add(any(Resource.class), any(ResourceType.class), any(ResourceConfiguration.class));
       doNothing().when(this.kbuilder).add(any(Resource.class), any(ResourceType.class));
       
       resource = ResourceFactory.newClassPathResource("/data/resource.bpmn2", getClass());
       addAsset(resource, ResourceType.BPMN2);
    }
    
    @Test
    public void addAssetCsvXlsReplaceConfigTest() {
        // config preserved
        ArgumentCaptor<ResourceConfiguration> resourceConfigCaptor = ArgumentCaptor.forClass(ResourceConfiguration.class);
        doThrow(new IllegalStateException("XLS resource not handled correctly!")).when(this.kbuilder).add(any(Resource.class), any(ResourceType.class));
        Resource resource = ResourceFactory.newClassPathResource("/data/resource.xls", getClass());
        DecisionTableConfigurationImpl config = new DecisionTableConfigurationImpl();
        config.setInputType(DecisionTableInputType.CSV);
        String worksheetName = "test-worksheet-name";
        config.setWorksheetName(worksheetName);
        resource.setConfiguration(config);
       
        // do method
        addAsset(resource, ResourceType.DTABLE);
        
        verify(this.kbuilder).add(any(Resource.class), any(ResourceType.class), resourceConfigCaptor.capture());
        ResourceConfiguration replacedConfig = resourceConfigCaptor.getValue();
        assertTrue( "Not a DecisionTableConfiguration, but a " + replacedConfig.getClass().getSimpleName(), 
                replacedConfig instanceof DecisionTableConfiguration );
        assertEquals( "Incorrect file type", DecisionTableInputType.XLS, ((DecisionTableConfiguration) replacedConfig).getInputType());
        assertEquals( "Worksheet name not preserved",  worksheetName, ((DecisionTableConfiguration) replacedConfig).getWorksheetName());
    }

    @Test
    public void addAssetXLSDtableWithOwnConfigTest() {
        Resource resource = ResourceFactory.newClassPathResource("/data/resource.xls", getClass());
        DecisionTableConfigurationImpl config = new DecisionTableConfigurationImpl();
        config.setInputType(DecisionTableInputType.XLS);
        String worksheetName = "test-worksheet-name";
        config.setWorksheetName(worksheetName);
        resource.setConfiguration(config);

        addAsset(resource, ResourceType.DTABLE);
        verify(this.kbuilder).add(any(Resource.class), any(ResourceType.class));
    }
}
