/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.webservice;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.transport.http.HTTPConduit;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessWorkItemHandlerException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mockito;
import org.apache.cxf.configuration.security.AuthorizationPolicy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebServiceWorkItemHandlerTest {

    @Mock
    KieSession kieSession;

    @Mock
    Client client;

    @Mock
    ConcurrentHashMap<String, Client> clients;

    @Test
    public void testExecuteSyncOperation() throws Exception {

        when(clients.containsKey(anyObject())).thenReturn(true);
        when(clients.get(anyObject())).thenReturn(client);

        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Interface",
                              "someInterface");
        workItem.setParameter("Operation",
                              "someOperation");
        workItem.setParameter("Parameter",
                              "myParam");
        workItem.setParameter("Mode",
                              "SYNC");

        WebServiceWorkItemHandler handler = new WebServiceWorkItemHandler(kieSession);
        handler.setClients(clients);

        handler.executeWorkItem(workItem,
                                manager);
        assertNotNull(manager.getResults());
        assertEquals(1,
                     manager.getResults().size());
        assertTrue(manager.getResults().containsKey(workItem.getId()));
    }

    @Test
    public void testExecuteSyncOperationWithBasicAuth() throws Exception {

        HTTPConduit http = Mockito.mock(HTTPConduit.class,
                                        Mockito.CALLS_REAL_METHODS);

        when(clients.containsKey(anyObject())).thenReturn(true);
        when(clients.get(anyObject())).thenReturn(client);
        when(client.getConduit()).thenReturn(http);

        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Interface",
                              "someInterface");
        workItem.setParameter("Operation",
                              "someOperation");
        workItem.setParameter("Parameter",
                              "myParam");
        workItem.setParameter("Mode",
                              "SYNC");

        WebServiceWorkItemHandler handler = new WebServiceWorkItemHandler(kieSession,
                                                                          "testusername",
                                                                          "testpassword");
        handler.setClients(clients);

        handler.executeWorkItem(workItem,
                                manager);
        assertNotNull(manager.getResults());
        assertEquals(1,
                     manager.getResults().size());
        assertTrue(manager.getResults().containsKey(workItem.getId()));

        assertNotNull(http.getAuthorization());
        AuthorizationPolicy authorizationPolicy = http.getAuthorization();
        assertEquals("Basic",
                     authorizationPolicy.getAuthorizationType());
        assertEquals("testusername",
                     authorizationPolicy.getUserName());
        assertEquals("testpassword",
                     authorizationPolicy.getPassword());
    }
    
    @Test
    public void testExecuteSyncOperationHandlingException() throws Exception {

        when(clients.containsKey(anyObject())).thenReturn(true);
        when(clients.get(anyObject())).thenReturn(null);

        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Interface",
                              "someInterface");
        workItem.setParameter("Operation",
                              "someOperation");
        workItem.setParameter("Parameter",
                              "myParam");
        workItem.setParameter("Mode",
                              "SYNC");

        WebServiceWorkItemHandler handler = new WebServiceWorkItemHandler("test", "COMPLETE", kieSession);
        handler.setClients(clients);
        try {
            handler.executeWorkItem(workItem,
                                manager);
            fail("Should throw exception as it was instructed to do so");
        } catch (ProcessWorkItemHandlerException ex) {
                
            assertEquals("Unable to create client for web service someInterface - someOperation", ex.getCause().getMessage());
            assertEquals("test", ex.getProcessId());
            assertEquals("COMPLETE", ex.getStrategy().name());
        }
    }
}
