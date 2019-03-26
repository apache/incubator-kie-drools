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

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.transport.http.HTTPConduit;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebServiceCommandTest {

    @Mock
    KieSession kieSession;

    @Mock
    Client client;

    @Mock
    ConcurrentHashMap<String, Client> clients;

    @Mock
    CommandContext commandContext;

    @Test
    public void testExecuteCommand() throws Exception {
        Object[] clientObject = Arrays.asList("testResults").toArray();

        when(clients.containsKey(anyObject())).thenReturn(true);
        when(clients.get(anyObject())).thenReturn(client);
        when(client.invoke(anyString(),
                           any(Object[].class))).thenReturn(clientObject);

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Interface",
                              "someInterface");
        workItem.setParameter("Operation",
                              "someOperation");

        when(commandContext.getData(anyString())).thenReturn(workItem);

        WebServiceCommand command = new WebServiceCommand();
        command.setClients(clients);
        ExecutionResults results = command.execute(commandContext);
        assertNotNull(results);

        assertEquals("testResults",
                     results.getData("Result"));
    }

    @Test
    public void testExecuteCommandWithBasicAuth() throws Exception {
        Object[] clientObject = Arrays.asList("testResults").toArray();

        when(clients.containsKey(anyObject())).thenReturn(true);
        when(clients.get(anyObject())).thenReturn(client);
        when(client.invoke(anyString(),
                           any(Object[].class))).thenReturn(clientObject);

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Interface",
                              "someInterface");
        workItem.setParameter("Operation",
                              "someOperation");
        workItem.setParameter("Username",
                              "testUserName");
        workItem.setParameter("Password",
                              "testPassword");

        when(commandContext.getData(anyString())).thenReturn(workItem);

        HTTPConduit http = Mockito.mock(HTTPConduit.class,
                                        Mockito.CALLS_REAL_METHODS);
        when(client.getConduit()).thenReturn(http);

        WebServiceCommand command = new WebServiceCommand();
        command.setClients(clients);
        ExecutionResults results = command.execute(commandContext);
        assertNotNull(results);

        assertEquals("testResults",
                     results.getData("Result"));

        assertNotNull(http.getAuthorization());
        AuthorizationPolicy authorizationPolicy = http.getAuthorization();
        assertEquals("Basic",
                     authorizationPolicy.getAuthorizationType());
        assertEquals("testUserName",
                     authorizationPolicy.getUserName());
        assertEquals("testPassword",
                     authorizationPolicy.getPassword());
    }
}
