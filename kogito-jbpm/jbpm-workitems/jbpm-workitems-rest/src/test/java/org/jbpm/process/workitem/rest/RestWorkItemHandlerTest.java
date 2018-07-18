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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.process.WorkItemManager;

import static org.jbpm.process.workitem.rest.RESTWorkItemHandler.PARAM_CONNECT_TIMEOUT;
import static org.jbpm.process.workitem.rest.RESTWorkItemHandler.PARAM_CONTENT;
import static org.jbpm.process.workitem.rest.RESTWorkItemHandler.PARAM_CONTENT_DATA;
import static org.jbpm.process.workitem.rest.RESTWorkItemHandler.PARAM_CONTENT_TYPE;
import static org.jbpm.process.workitem.rest.RESTWorkItemHandler.PARAM_READ_TIMEOUT;
import static org.jbpm.process.workitem.rest.RESTWorkItemHandler.PARAM_RESULT;
import static org.jbpm.process.workitem.rest.RESTWorkItemHandler.PARAM_STATUS;
import static org.jbpm.process.workitem.rest.RESTWorkItemHandler.PARAM_STATUS_MSG;
import static org.jbpm.process.workitem.rest.RESTWorkItemHandler.PARAM_HEADERS;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class RestWorkItemHandlerTest {

    @Parameters(name = "Http Client 4.3 api = {0}, Content Param = {1}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                {true, PARAM_CONTENT}, {false, PARAM_CONTENT}, {true, PARAM_CONTENT_DATA}, {false, PARAM_CONTENT_DATA}
        });
    }

    private final boolean httpClient43;
    private String contentParamName;

    private final static String serverURL = "http://localhost:9998/test";
    private static Server server;

    public RestWorkItemHandlerTest(boolean httpClient43,
                                   String contentParamName) {
        this.httpClient43 = httpClient43;
        this.contentParamName = contentParamName;
    }

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
    public void testGETOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL);
        workItem.setParameter("Method",
                              "GET");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        assertNotNull("results cannot be null",
                      results);
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Hello from REST",
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testGETOperationWithJSONHeader() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("AcceptHeader",
                              "application/json");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(406,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("endpoint " + serverURL + " could not be reached: ",
                     responseMsg);
    }

    @Test
    public void testGETOperationWithXMLHeader() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("AcceptHeader",
                              "application/xml");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(406,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("endpoint " + serverURL + " could not be reached: ",
                     responseMsg);
    }

    @Test
    public void testGETOperationWithTextHeader() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("AcceptHeader",
                              "text/plain");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testGETOperationWithCustomTimeout() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter(PARAM_CONNECT_TIMEOUT,
                              "30000");
        workItem.setParameter(PARAM_READ_TIMEOUT,
                              "25000");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Hello from REST",
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testGETOperationWithInvalidTimeout() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter(PARAM_CONNECT_TIMEOUT,
                              "");
        workItem.setParameter(PARAM_READ_TIMEOUT,
                              "");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Hello from REST",
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testGETOperationWithQueryParam() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "?param=test");
        workItem.setParameter("Method",
                              "GET");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Hello from REST test",
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPOSTOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>25</age><name>Post john</name></person>";

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "POST");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "application/xml");
        workItem.setParameter(contentParamName,
                              "<person><name>john</name><age>25</age></person>");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals(expected,
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPOSTOperationWthJSONHeader() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>25</age><name>Post john</name></person>";

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "POST");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "application/xml");
        workItem.setParameter(contentParamName,
                              "<person><name>john</name><age>25</age></person>");

        workItem.setParameter("AcceptHeader",
                              "application/json");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(406,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("endpoint " + serverURL + "/xml could not be reached: ",
                     responseMsg);
    }

    @Test
    public void testPOSTOperationWithXMLHeader() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>25</age><name>Post john</name></person>";

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "POST");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "application/xml");
        workItem.setParameter(contentParamName,
                              "<person><name>john</name><age>25</age></person>");
        workItem.setParameter("AcceptHeader",
                              "application/xml");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals(expected,
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPOSTOperationWithPathParamAndNoContent() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/john");
        workItem.setParameter("Method",
                              "POST");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Created resource with name john",
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPUTOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>25</age><name>Put john</name></person>";

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "PUT");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "application/xml");
        workItem.setParameter(contentParamName,
                              "<person><name>john</name><age>25</age></person>");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals(expected,
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPUTOperationWithXMLHeader() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>25</age><name>Put john</name></person>";

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "PUT");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "application/xml");
        workItem.setParameter(contentParamName,
                              "<person><name>john</name><age>25</age></person>");
        workItem.setParameter("AcceptHeader",
                              "application/xml");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals(expected,
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPUTOperationWithJSONHeader() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>25</age><name>Put john</name></person>";

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "PUT");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "application/xml");
        workItem.setParameter(contentParamName,
                              "<person><name>john</name><age>25</age></person>");
        workItem.setParameter("AcceptHeader",
                              "application/json");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(406,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("endpoint " + serverURL + "/xml could not be reached: ",
                     responseMsg);
    }

    @Test
    public void testDELETEOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>-1</age><name>deleted john</name></person>";

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml/john");
        workItem.setParameter("Method",
                              "DELETE");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals(expected,
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml/john");
        workItem.setParameter("Method",
                              "HEAD");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);
    }

    @Test
    public void testHandleErrorOnNotSuccessfulResponse() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/notexisting");
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("HandleResponseErrors",
                              "true");

        WorkItemManager manager = new TestWorkItemManager();
        try {
            handler.executeWorkItem(workItem,
                                    manager);
            fail("Should throw exception as it was instructed to do so");
        } catch (WorkItemHandlerRuntimeException ex) {

            RESTServiceException e = (RESTServiceException) ex.getCause().getCause();
            assertEquals(405,
                         e.getStatus());
            assertEquals(serverURL + "/notexisting",
                         e.getEndoint());
            assertEquals("",
                         e.getResponse());
        }
    }

    @Test
    public void testGETOperationWithXmlTrasformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("ResultClass",
                              Person.class.getName());

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        Person result = (Person) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Person Xml",
                     result.getName());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testGETOperationWithJsonTrasformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/json");
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("ResultClass",
                              Person.class.getName());

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        Person result = (Person) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Person Json",
                     result.getName());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPOSTOperationWithXmlTransformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "POST");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "Application/XML;charset=utf-8");
        workItem.setParameter(contentParamName,
                              "<person><name>john</name><age>25</age></person>");
        workItem.setParameter("ResultClass",
                              Person.class.getName());

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        Person result = (Person) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Post john",
                     result.getName());
        assertEquals(25,
                     result.getAge().intValue());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPUTOperationWithXmlTransformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "PUT");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "Application/Xml;charset=utf-8");
        workItem.setParameter(contentParamName,
                              "<person><name>john</name><age>25</age></person>");
        workItem.setParameter("ResultClass",
                              Person.class.getName());

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        Person result = (Person) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Put john",
                     result.getName());
        assertEquals(25,
                     result.getAge().intValue());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPOSTOperationWithCompleteXmlTransformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        Person request = new Person();
        request.setAge(25);
        request.setName("john");

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml");
        workItem.setParameter("Method",
                              "POST");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "application/xml");
        workItem.setParameter(contentParamName,
                              request);
        workItem.setParameter("ResultClass",
                              Person.class.getName());

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        Person result = (Person) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Post john",
                     result.getName());
        assertEquals(25,
                     result.getAge().intValue());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testGETOperationWithXmlCharsetTrasformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml-charset");
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("ResultClass",
                              Person.class.getName());

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        Person result = (Person) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Person Xml",
                     result.getName());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testGETOperationWithJsonCharsetTrasformation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/json-charset");
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter("ResultClass",
                              Person.class.getName());

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        Person result = (Person) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals("Person Json",
                     result.getName());
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPUTOperationWithDefaultCharset() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String nonAsciiData = "\u0418\u0432\u0430\u043d";
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>25</age><name>Put ????</name></person>";

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml-charset");
        workItem.setParameter("Method",
                              "PUT");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "application/xml");
        workItem.setParameter(contentParamName,
                              "<person><name>" + nonAsciiData + "</name><age>25</age></person>");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals(expected,
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }

    @Test
    public void testPUTOperationWithCharsetSpecified() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String nonAsciiData = "\u0418\u0432\u0430\u043d";
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>25</age><name>Put " + nonAsciiData + "</name></person>";

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/xml-charset");
        workItem.setParameter("Method",
                              "PUT");
        workItem.setParameter(PARAM_CONTENT_TYPE,
                              "application/xml; charset=utf-8");
        workItem.setParameter(contentParamName,
                              "<person><name>" + nonAsciiData + "</name><age>25</age></person>");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);
        assertEquals(expected,
                     result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }
    
    @Test
    public void testHeadersNull() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String headerKey = "headerKey";
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/header/" + headerKey);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter(PARAM_HEADERS, headerKey + "=");
        
        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);
        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertTrue(result.trim().isEmpty());
    }
    
    @Test
    public void testHeadersSingleValue() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String headerKey = "headerKey";
        String headerValue = "headerValue";
        String headers = headerKey + "=" + headerValue;
         
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/header/" + headerKey);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter(PARAM_HEADERS, headers);
        
        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);
        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertEquals(result, headerValue);
    }
    
    @Test
    public void testHeadersMultipleValues() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String headerKey = "headerKey";
        String headerValues = "headerValue,headerValue2,headerValue3";
        String headers = headerKey + "=" + headerValues;
         
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/header/" + headerKey);
        workItem.setParameter("Method",
                              "GET");
        workItem.setParameter(PARAM_HEADERS, headers);
        
        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);
        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertEquals(result, headerValues);
    }    
    
    @Test
    public void testHeadersMultipleHeaders() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String headerKey1 = "headerKey";
        String headerValues1 = "headerValue,headerValue2,headerValue3";
        String headerKey2 = "headerKey2";
        String headerValues2 = "headerValue2,headerValue22,headerValue23";
        String headers = headerKey1 + "=" + headerValues1 + ";" 
                           + headerKey2 + "=" + headerValues2;
           
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                                serverURL + "/header/" + headerKey1);
        workItem.setParameter("Method",
                                "GET");
        workItem.setParameter(PARAM_HEADERS, headers);
        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                  manager);
        
        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        String result = (String) results.get(PARAM_RESULT);
        assertEquals(result, headerValues1);
        workItem.setParameter("Url",
                                serverURL + "/header/" + headerKey2);
        workItem.setParameter("Method",
                        "GET");
        workItem.setParameter(PARAM_HEADERS, headers);
        handler.executeWorkItem(workItem,
                          manager);
        results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        result = (String) results.get(PARAM_RESULT);
        assertEquals(result, headerValues2);
    }

    @Test
    public void testGETCharsetOperation() throws UnsupportedEncodingException {
        final String charset = "Windows-1252";
        String expected = "Š";

        RESTWorkItemHandler handler = new RESTWorkItemHandler();

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Url",
                              serverURL + "/charset");
        workItem.setParameter("AcceptCharset", charset);
        workItem.setParameter("Method",
                              "GET");

        WorkItemManager manager = new TestWorkItemManager();
        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = ((TestWorkItemManager) manager).getResults(workItem.getId());
        assertNotNull("results cannot be null",
                      results);
        String result = (String) results.get(PARAM_RESULT);
        assertNotNull("result cannot be null",
                      result);

        assertEquals(expected, result);
        int responseCode = (Integer) results.get(PARAM_STATUS);
        assertNotNull(responseCode);
        assertEquals(200,
                     responseCode);
        String responseMsg = (String) results.get(PARAM_STATUS_MSG);
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") + " successfully completed OK",
                     responseMsg);
    }
}