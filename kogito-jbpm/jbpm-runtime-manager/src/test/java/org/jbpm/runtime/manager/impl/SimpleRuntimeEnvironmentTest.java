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
}
