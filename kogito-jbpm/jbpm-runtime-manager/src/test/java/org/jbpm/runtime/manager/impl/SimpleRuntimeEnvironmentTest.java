package org.jbpm.runtime.manager.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.io.ResourceFactory;

public class SimpleRuntimeEnvironmentTest extends SimpleRuntimeEnvironment {

    @Test
    public void addAssetCsvXlsTest() { 
       this.kbuilder = mock(KnowledgeBuilder.class);
       doNothing().when(this.kbuilder).add(any(Resource.class), any(ResourceType.class), any(ResourceConfiguration.class));
       
       doThrow(new IllegalStateException("CSV resource not handled correctly!")).when(this.kbuilder).add(any(Resource.class), any(ResourceType.class));
       Resource resource = ResourceFactory.newClassPathResource("/data/resource.csv", getClass());
       addAsset(resource, ResourceType.DTABLE);
       
       doThrow(new IllegalStateException("XLS resource not handled correctly!")).when(this.kbuilder).add(any(Resource.class), any(ResourceType.class));
       resource = ResourceFactory.newClassPathResource("/data/resource.xls", getClass());
       addAsset(resource, ResourceType.DTABLE);
       
       doThrow(new IllegalStateException("BPMN2 resource not handled correctly!")).when(this.kbuilder).add(any(Resource.class), any(ResourceType.class), any(ResourceConfiguration.class));
       doNothing().when(this.kbuilder).add(any(Resource.class), any(ResourceType.class));
       
       resource = ResourceFactory.newClassPathResource("/data/resource.bpmn2", getClass());
       addAsset(resource, ResourceType.BPMN2);
    }
}
