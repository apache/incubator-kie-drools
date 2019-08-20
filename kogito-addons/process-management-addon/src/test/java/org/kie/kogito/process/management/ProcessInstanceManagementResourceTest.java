/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.process.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.RuntimeDelegate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.Processes;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ProcessInstanceManagementResourceTest {

    private static RuntimeDelegate runtimeDelegate;
    private ResponseBuilder responseBuilder;
    
    private Processes processes;
    @SuppressWarnings("rawtypes")
    private ProcessInstance processInstance;
    private ProcessError error;
    
    @BeforeAll
    public static void configureEnvironment() {
        runtimeDelegate = mock(RuntimeDelegate.class);
        RuntimeDelegate.setInstance(runtimeDelegate);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @BeforeEach
    public void setup() {
        
        responseBuilder = mock(ResponseBuilder.class);
        Response response = mock(Response.class);        
        
        when((runtimeDelegate).createResponseBuilder()).thenReturn(responseBuilder);
        when((responseBuilder).status(any(StatusType.class))).thenReturn(responseBuilder);
        when((responseBuilder).entity(any())).thenReturn(responseBuilder);
        when((responseBuilder).build()).thenReturn(response);
                
                
        processes = mock(Processes.class);
        Process process = mock(Process.class);
        ProcessInstances instances = mock(ProcessInstances.class);
        processInstance = mock(ProcessInstance.class);
        error = mock(ProcessError.class);
                      
        when(processes.processById(anyString())).thenReturn(process);
        when(process.instances()).thenReturn(instances);
        when(instances.findById(anyString())).thenReturn(Optional.of(processInstance));
        when(processInstance.error()).thenReturn(Optional.of(error));
        when(processInstance.id()).thenReturn("abc-def");
        when(processInstance.status()).thenReturn(ProcessInstance.STATE_ERROR);
        when(error.failedNodeId()).thenReturn("xxxxx");
        when(error.errorMessage()).thenReturn("Test error message");
        
    }
    
    @Test
    public void testGetErrorInfo() {
        
        ProcessInstanceManagementResource resource = new ProcessInstanceManagementResource();
        resource.processes = this.processes;
        
        Response response = resource.getInstanceInError("test", "xxxxx");
        assertThat(response).isNotNull();
        
        verify(responseBuilder, times(1)).status((StatusType)Status.OK);
        verify(responseBuilder, times(1)).entity(any());
        
        verify(processInstance, times(2)).error();
        verify(error, times(0)).retrigger();
        verify(error, times(0)).skip();

    }
    
    @Test
    public void testRetriggerErrorInfo() {
        
        ProcessInstanceManagementResource resource = new ProcessInstanceManagementResource();
        resource.processes = this.processes;
        
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                when(processInstance.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
                return null;
            }
        }).when(error).retrigger();
        
        Response response = resource.retriggerInstanceInError("test", "xxxxx");
        assertThat(response).isNotNull();
        
        verify(responseBuilder, times(1)).status((StatusType)Status.OK);
        verify(responseBuilder, times(1)).entity(any());
        
        verify(processInstance, times(2)).error();
        verify(error, times(1)).retrigger();
        verify(error, times(0)).skip();

    }
    
    @Test
    public void testSkipErrorInfo() {
        
        ProcessInstanceManagementResource resource = new ProcessInstanceManagementResource();
        resource.processes = this.processes;
        
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                when(processInstance.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
                return null;
            }
        }).when(error).skip();
        
        Response response = resource.skipInstanceInError("test", "xxxxx");
        assertThat(response).isNotNull();
        
        verify(responseBuilder, times(1)).status((StatusType)Status.OK);
        verify(responseBuilder, times(1)).entity(any());
        
        verify(processInstance, times(2)).error();
        verify(error, times(0)).retrigger();
        verify(error, times(1)).skip();

    }
}
