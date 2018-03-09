/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.workitem.rest;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.http.impl.client.CloseableHttpClient;

import org.drools.core.process.instance.impl.WorkItemImpl;

import org.jbpm.process.workitem.core.TestWorkItemManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.kie.api.runtime.process.WorkItemManager;

import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestWorkitemHandlerClientCreationTest {

    private final boolean httpClient43 = true;

    private final static String serverURL = "http://localhost:9998/test";
    private static Server server;

    @SuppressWarnings({"rawtypes"})
    @BeforeClass
    public static void initialize() throws Exception {

        SimpleRESTApplication application = new SimpleRESTApplication();
        RuntimeDelegate delegate = RuntimeDelegate.getInstance();

        JAXRSServerFactoryBean bean = delegate.createEndpoint(application,
                                                              JAXRSServerFactoryBean.class);
        bean.setProvider(new JAXBElementProvider());
        bean.setAddress("http://localhost:9998" + bean.getAddress());
        server = bean.create();
        server.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        if (server != null) {
            server.stop();
            server.destroy();
        }
    }

    @Before
    public void setClientApiVersion() {
        RESTWorkItemHandler.HTTP_CLIENT_API_43 = httpClient43;
    }

    @Test
    public void testPooledClientCreationWithDefaultTimeouts() {
        RESTWorkItemHandler handler = spy(RESTWorkItemHandler.class);
        when(handler.getDoCacheClient()).thenReturn(true);

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL);
        workItem.setParameter("Method",
                              "GET");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);
        // second call to executeWorkItem
        handler.executeWorkItem(workItem,
                                manager);

        verify(handler,
               times(2)).getHttpClient(anyInt(),
                                       anyInt());

        verify(handler,
               times(1)).getNewPooledHttpClient(anyInt(),
                                                anyInt());

        assertNotNull(handler.cachedClient);
        assertTrue(handler.cachedClient instanceof CloseableHttpClient);

        assertNotNull(handler.cachedClient.getConnectionManager());
    }

    @Test
    public void testPooledClientCreationWithSetTimeouts() {
        RESTWorkItemHandler handler = spy(RESTWorkItemHandler.class);
        when(handler.getDoCacheClient()).thenReturn(true);

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("ConnectTimeout",
                              "4000");
        workItem.setParameter("ReadTimeout",
                              "3000");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);
        // second call to executeWorkItem
        handler.executeWorkItem(workItem,
                                manager);

        verify(handler,
               times(2)).getHttpClient(anyInt(),
                                       anyInt());

        // should use existing already since cached
        verify(handler,
               times(0)).getNewPooledHttpClient(anyInt(),
                                                anyInt());

        assertNotNull(handler.cachedClient);
        assertTrue(handler.cachedClient instanceof CloseableHttpClient);
        assertNotNull(handler.cachedClient.getConnectionManager());
    }

    @Test
    public void testSingleClientCreationOnMultipleCalls() {
        RESTWorkItemHandler handler = spy(RESTWorkItemHandler.class);

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("ConnectTimeout",
                              "4000");
        workItem.setParameter("ReadTimeout",
                              "3000");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        workItem.setParameter("ConnectTimeout",
                              "5000");
        workItem.setParameter("ReadTimeout",
                              "4000");
        // second call to executeWorkItem
        handler.executeWorkItem(workItem,
                                manager);

        verify(handler,
               times(2)).getHttpClient(anyInt(),
                                       anyInt());
        verify(handler,
               times(0)).getNewPooledHttpClient(anyInt(),
                                                anyInt());
    }
}
