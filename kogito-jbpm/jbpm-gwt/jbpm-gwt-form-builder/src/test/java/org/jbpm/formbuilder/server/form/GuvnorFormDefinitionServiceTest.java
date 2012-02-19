/*
 * Copyright 2011 JBoss Inc
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
package org.jbpm.formbuilder.server.form;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.easymock.EasyMock;
import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;
import org.jbpm.formbuilder.server.GuvnorHelper;
import org.jbpm.formbuilder.server.RESTAbstractTest;
import org.jbpm.formbuilder.server.mock.MockAnswer;
import org.jbpm.formbuilder.server.mock.MockDeleteMethod;
import org.jbpm.formbuilder.server.mock.MockGetMethod;
import org.jbpm.formbuilder.server.mock.MockPostMethod;
import org.jbpm.formbuilder.server.mock.MockPutMethod;
import org.jbpm.formbuilder.shared.form.FormServiceException;

public class GuvnorFormDefinitionServiceTest extends TestCase {

	private String baseUrl = "http://www.redhat.com";
	private GuvnorHelper helper = new GuvnorHelper(baseUrl, "", "");
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
    }
    
    //test happy path for insert for GuvnorFormDefinitionService.saveForm(...)
    public void testSaveFormOK() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form1AutoForm.formdef", "{}");
        responses.put("POST " + helper.getApiSearchUrl("somePackage") + "form1AutoForm.formdef", "OK");
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        FormRepresentation form = RESTAbstractTest.createMockForm("form1", "oneParam");
        
        EasyMock.replay(client);
        String formId = service.saveForm("somePackage", form);
        EasyMock.verify(client);
        
        assertNotNull("formId shouldn't be null", formId);
    }
    
    public void testSaveFormHttpProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form1AutoForm.formdef", "{}");
        responses.put("POST " + helper.getApiSearchUrl("somePackage") + "form1AutoForm.formdef", "PROBLEM");
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        FormRepresentation form = RESTAbstractTest.createMockForm("form1", "oneParam");
        
        EasyMock.replay(client);
        try {
            service.saveForm("somePackage", form);
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            String message = e.getMessage();
            assertTrue("message should contain PROBLEM", message.contains("PROBLEM"));
        }
        EasyMock.verify(client);
        
    }
    
    //test happy path for update for GuvnorFormDefinitionService.saveForm(...)
    public void testSaveFormUpdateOK() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        FormRepresentation form = RESTAbstractTest.createMockForm("form2", "oneParam");
        String jsonForm = FormEncodingFactory.getEncoder().encode(form);
        Map<String, String> responses1 = new HashMap<String, String>();
        responses1.put("GET " + helper.getApiSearchUrl("somePackage") + "form2AutoForm.formdef", jsonForm);
        Map<String, String> responses2 = new HashMap<String, String>();
        responses2.put("PUT " + helper.getApiSearchUrl("somePackage") + "form2AutoForm.formdef", "OK");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses1, new IllegalArgumentException("unexpected call"))).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPutMethod.class))).
            andAnswer(new MockAnswer(responses2, new IllegalArgumentException("unexpected call"))).once();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        String formId = service.saveForm("somePackage", form);
        EasyMock.verify(client);
        
        assertNotNull("formId shouldn't be null", formId);
    }
    
    //test response to a FormEncodingException for GuvnorFormDefinitionService.saveForm(...)
    public void testSaveFormDecodingProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        FormRepresentation form = RESTAbstractTest.createMockForm("form2", "oneParam");
        String jsonForm = FormEncodingFactory.getEncoder().encode(form);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form2AutoForm.formdef", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        FormRepresentationDecoder decoder = EasyMock.createMock(FormRepresentationDecoder.class);
        EasyMock.expect(decoder.decode(EasyMock.eq(jsonForm))).andThrow(new FormEncodingException("Something going wrong")).once();
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), decoder);
        
        EasyMock.replay(client, decoder);
        try {
            service.saveForm("somePackage", form);
            fail("saveForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type FormEncodingException", cause instanceof FormEncodingException);
        }
        EasyMock.verify(client, decoder);
    }
    
  //test response to a FormEncodingException for GuvnorFormDefinitionService.saveForm(...)
    public void testSaveFormEncodingProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        FormRepresentation form = RESTAbstractTest.createMockForm("form2", "oneParam");
        String jsonForm = FormEncodingFactory.getEncoder().encode(form);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form2AutoForm.formdef", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        FormRepresentationEncoder encoder = EasyMock.createMock(FormRepresentationEncoder.class);
        EasyMock.expect(encoder.encode(EasyMock.eq(form))).andThrow(new FormEncodingException("Something going wrong")).once();
        FormEncodingFactory.register(encoder, FormEncodingServerFactory.getDecoder());
        
        EasyMock.replay(client, encoder);
        try {
            service.saveForm("somePackage", form);
            fail("saveForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type FormEncodingException", cause instanceof FormEncodingException);
        }
        EasyMock.verify(client, encoder);
    }
    
    //test response to a IOException for GuvnorFormDefinitionService.saveForm(...)
    public void testSaveFormIOProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        FormRepresentation form = RESTAbstractTest.createMockForm("form2", "oneParam");
        String jsonForm = FormEncodingFactory.getEncoder().encode(form);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form2AutoForm.formdef", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IOException("MOCKING IO ERROR"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.saveForm("somePackage", form);
            fail("saveForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    //test response to a NullPointerException for GuvnorFormDefinitionService.saveForm(...)
    public void testSaveFormUnknownProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        FormRepresentation form = RESTAbstractTest.createMockForm("form2", "oneParam");
        String jsonForm = FormEncodingFactory.getEncoder().encode(form);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form2AutoForm.formdef", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new NullPointerException("MOCKING IO ERROR"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.saveForm("somePackage", form);
            fail("saveForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }

    //test happy path for GuvnorFormDefinitionService.saveFormItem(...)
    public void testSaveFormItemOK() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        responses.put("POST " + helper.getApiSearchUrl("somePackage") + "formItemDefinition_item1.json", "{}");
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        FormItemRepresentation item = RESTAbstractTest.createMockForm("form1", "oneParam").getFormItems().iterator().next();
        
        EasyMock.replay(client);
        String itemId = service.saveFormItem("somePackage", "item1", item);
        EasyMock.verify(client);
        
        assertNotNull("itemId shouldn't be null", itemId);
    }
    
    //test response to a FormEncodingException for GuvnorFormDefinitionService.saveFormItem(...)
    public void testSaveFormItemEncodingProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        responses.put("POST " + helper.getApiSearchUrl("somePackage") + "formItemDefinition_item1.json", "{}");
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        FormItemRepresentation item = RESTAbstractTest.createMockForm("form1", "oneParam").getFormItems().iterator().next();
        FormRepresentationEncoder encoder = EasyMock.createMock(FormRepresentationEncoder.class);
        FormEncodingFactory.register(encoder, FormEncodingFactory.getDecoder());
        EasyMock.expect(encoder.encode(EasyMock.eq(item))).andThrow(new FormEncodingException("Something wrong")).once();
        
        EasyMock.replay(client, encoder);
        try {
            service.saveFormItem("somePackage", "item1", item);
            fail("Shouldn't have succeeded");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type FormEncodingException", cause instanceof FormEncodingException);
        }
        EasyMock.verify(client, encoder);
    }
    
    //test response to a IOException for GuvnorFormDefinitionService.saveFormItem(...)
    public void testSaveFormItemIOProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IOException("MOCK IO ERROR"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        FormItemRepresentation item = RESTAbstractTest.createMockForm("form1", "oneParam").getFormItems().iterator().next();
        
        EasyMock.replay(client);
        try {
            service.saveFormItem("somePackage", "item1", item);
            fail("Shouldn't have succeeded");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    //test response to an Exception for GuvnorFormDefinitionService.saveFormItem(...)
    public void testSaveFormItemUnkownProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new NullPointerException())).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        FormItemRepresentation item = RESTAbstractTest.createMockForm("form1", "oneParam").getFormItems().iterator().next();
        
        EasyMock.replay(client);
        try {
            service.saveFormItem("somePackage", "item1", item);
            fail("Shouldn't have succeeded");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    //test happy path for GuvnorFormDefinitionService.getForm(...)
    public void testGetFormOK() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        FormRepresentation form = RESTAbstractTest.createMockForm("form1", "oneParam");
        String jsonForm = FormEncodingFactory.getEncoder().encode(form);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form1AutoForm.formdef", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("Unexpected call"))).anyTimes();
		GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        FormRepresentation form1 = service.getForm("somePackage", "form1AutoForm");
        EasyMock.verify(client);
        
        assertNotNull("form1 shouldn't be null", form1);
        assertEquals("form and form1 should be identical", form, form1);
    }
    
    public void testGetFormEmptyName() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        FormRepresentation form1 = service.getForm("somePackage", "");
        assertNull("form1 should be null", form1);
        
        FormRepresentation form2 = service.getForm("somePackage", null);
        assertNull("form2 should be null", form2);
    }
    
    //test response to a FormEncodingException for GuvnorFormDefinitionService.getForm(...)
    public void testGetFormEncodingProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        FormRepresentation form = RESTAbstractTest.createMockForm("form2", "oneParam");
        String jsonForm = FormEncodingFactory.getEncoder().encode(form);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form2AutoForm.formdef", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        FormRepresentationDecoder decoder = EasyMock.createMock(FormRepresentationDecoder.class);
        EasyMock.expect(decoder.decode(EasyMock.eq(jsonForm))).andThrow(new FormEncodingException("Something going wrong")).once();
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), decoder);
        
        EasyMock.replay(client, decoder);
        try {
            service.getForm("somePackage", "form2AutoForm");
            fail("getForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type FormEncodingException", cause instanceof FormEncodingException);
        }
        EasyMock.verify(client, decoder);
    }
    
    //test response to a IOException for GuvnorFormDefinitionService.getForm(...)
    public void testGetFormIOProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IOException("MOCK IO ERROR"))).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getForm("somePackage", "form2AutoForm");
            fail("getForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type FormEncodingException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetFormUnkownProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        EasyMock.expect(client.executeMethod(EasyMock.anyObject(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new NullPointerException())).anyTimes();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getForm("somePackage", "form2AutoForm");
            fail("getForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type FormEncodingException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetFormByUUIDEmptyPackage() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        FormRepresentation form1 = service.getFormByUUID(null, null);
        assertNull("form1 should be null", form1);
        FormRepresentation form2 = service.getFormByUUID("", null);
        assertNull("form1 should be null", form2);
    }
    
    public void testGetFormByUUIDIOProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new IOException("mock io error")).once();
        service.getHelper().setClient(client);
        String uuid = UUID.randomUUID().toString();
        
        EasyMock.replay(client);
        try {
            service.getFormByUUID("somePackage", uuid);
            fail("getFormByUUID(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }

    public void testGetFormByUUIDJAXBProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        responses.put("GET " + helper.getRestBaseUrl(), "<invalidXmlLetsSeeWhatHappens></WHAT>");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        String uuid = UUID.randomUUID().toString();
        
        EasyMock.replay(client);
        try {
            service.getFormByUUID("somePackage", uuid);
            fail("getFormByUUID(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type JAXBException", cause instanceof JAXBException);
        }
        EasyMock.verify(client);
    }

    public void testGetFormByUUIDUnknownProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        responses.put("GET " + helper.getRestBaseUrl(), "<invalidXmlLetsSeeWhatHappens></WHAT>");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        String uuid = UUID.randomUUID().toString();
        
        EasyMock.replay(client);
        try {
            service.getFormByUUID("somePackage", uuid);
            fail("getFormByUUID(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }

    public void testGetFormByUUIDEncodingProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        String uuid = UUID.randomUUID().toString();
        Map<String, String> responses1 = new HashMap<String, String>();
        String xml1 = "<packages><package>" +
                "<title>somePackage</title>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/asset1</assets>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/asset2</assets>" +
                "</package></packages>";
        String xml2 = "<asset>" +
                "<sourceLink>" + helper.getRestBaseUrl() + "somePackage/asset1/source</sourceLink>" +
                "<metadata>" +
                "<format>formdef</format>" +
                "<uuid>" + uuid + "</uuid>" +
                "</metadata>" +
                "</asset>";
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myOnlyParam");
        String jsonForm = FormEncodingServerFactory.getEncoder().encode(form);
        responses1.put("GET " + helper.getRestBaseUrl(), xml1);
        responses1.put("GET " + helper.getRestBaseUrl() + "somePackage/asset1", xml2);
        responses1.put("GET " + helper.getRestBaseUrl() + "somePackage/asset1/source", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses1, new IllegalArgumentException("unexpected call"))).times(3);
        EasyMock.expect(client);
        service.getHelper().setClient(client);
        FormRepresentationDecoder decoder = EasyMock.createMock(FormRepresentationDecoder.class);
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        EasyMock.expect(decoder.decode(EasyMock.isA(String.class))).andThrow(exception).once();
        FormEncodingFactory.register(FormEncodingFactory.getEncoder(), decoder);
        
        EasyMock.replay(client, decoder);
        try {
            service.getFormByUUID("somePackage", uuid);
            fail("getFormByUUID(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type FormEncodingException", cause instanceof FormEncodingException);
        }
        EasyMock.verify(client, decoder);
    }
    
    public void testGetFormByUUIDOK() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        String uuid = UUID.randomUUID().toString();
        Map<String, String> responses1 = new HashMap<String, String>();
        String xml1 = "<packages><package>" +
                "<title>somePackage</title>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/asset1</assets>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/asset2</assets>" +
                "</package></packages>";
        String xml2 = "<asset>" +
                "<sourceLink>" + helper.getRestBaseUrl() + "somePackage/asset1/source</sourceLink>" +
                "<metadata>" +
                "<format>formdef</format>" +
                "<uuid>" + uuid + "</uuid>" +
                "</metadata>" +
                "</asset>";
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myOnlyParam");
        String jsonForm = FormEncodingServerFactory.getEncoder().encode(form);
        responses1.put("GET " + helper.getRestBaseUrl(), xml1);
        responses1.put("GET " + helper.getRestBaseUrl() + "somePackage/asset1", xml2);
        responses1.put("GET " + helper.getRestBaseUrl() + "somePackage/asset1/source", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses1, new IllegalArgumentException("unexpected call"))).times(3);
        EasyMock.expect(client);
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        FormRepresentation form2 = service.getFormByUUID("somePackage", uuid);
        EasyMock.verify(client);
        
        assertNotNull("form2 shouldn't be null", form2);
        assertEquals("form and form2 should be the same", form, form2);
    }

    public void testGetFormItemOK() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        Map<String, String> responses = new HashMap<String, String>();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam");
        String jsonForm = FormEncodingServerFactory.getEncoder().encode(form);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "myForm.formdef", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        
        EasyMock.replay(client);
        FormRepresentation form2 = service.getForm("somePackage", "myForm");
        EasyMock.verify(client);
        
        assertNotNull("form2 shouldn't be null", form2);
        assertEquals("form and form2 should be equal", form, form2);
    }
    
    public void testGetFormEmptyFormId() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        
        FormRepresentation form1 = service.getForm("somePackage", null);
        assertNull("form1 should be null", form1);
        
        FormRepresentation form2 = service.getForm("somePackage", "");
        assertNull("form2 should be null", form2);
    }
    
    public void testGetFormItemEncodingProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        Map<String, String> responses = new HashMap<String, String>();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam");
        String jsonForm = FormEncodingServerFactory.getEncoder().encode(form);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "myForm.formdef", jsonForm);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        FormRepresentationDecoder decoder = EasyMock.createMock(FormRepresentationDecoder.class);
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        EasyMock.expect(decoder.decode(EasyMock.isA(String.class))).andThrow(exception).once();
        FormEncodingFactory.register(FormEncodingFactory.getEncoder(), decoder);
        
        EasyMock.replay(client, decoder);
        try {
            service.getForm("somePackage", "myForm");
            fail("getForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type FormEncodingException", cause instanceof FormEncodingException);
        }
        EasyMock.verify(client, decoder);
    }
    
    public void testGetFormItemIOProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new IOException("mock io error")).once();
        
        EasyMock.replay(client);
        try {
            service.getForm("somePackage", "myForm");
            fail("getForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetFormItemUnkownProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        
        EasyMock.replay(client);
        try {
            service.getForm("somePackage", "myForm");
            fail("getForm(...) Shouldn't succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetFormItemsOK() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        FormItemRepresentation item1 = RESTAbstractTest.createMockForm("form1", "oneParam").getFormItems().get(1);
        String jsonItem1 = FormEncodingFactory.getEncoder().encode(item1);
        FormItemRepresentation item2 = RESTAbstractTest.createMockForm("form2", "anotherParam").getFormItems().get(2);
        String jsonItem2 = FormEncodingFactory.getEncoder().encode(item2);
        StringBuilder props = new StringBuilder();
        props.append("form1AutoForm.formdef=AAAAA\n");
        props.append("formItemDefinition_item1.json=AAAAA\n");
        props.append("formItemDefinition_item2.json=AAAAA\n");
        responses.put("GET " + helper.getApiSearchUrl("somePackage"), props.toString());
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "formItemDefinition_item1.json", jsonItem1);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "formItemDefinition_item2.json", jsonItem2);        
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("Unexpected call"))).times(3);
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        Map<String, FormItemRepresentation> items = service.getFormItems("somePackage");
        EasyMock.verify(client);
        
        assertNotNull("items shouldn't be null", items);
        assertEquals("items should have 2 elements", 2, items.size());
        assertTrue("forms should contain form1", items.containsValue(item1));
        assertTrue("forms should contain form2", items.containsValue(item2));
    }
    
    public void testGetFormItemServiceProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        StringBuilder props = new StringBuilder();
        props.append("form1AutoForm.formdef=AAAAA\n");
        props.append("formItemDefinition_someItem.json=AAAAA\n");
        props.append("form2AutoForm.formdef=AAAAA\n");
        responses.put("GET " + helper.getApiSearchUrl("somePackage"), props.toString());
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IOException("Problem reading one item"))).times(2);
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getFormItems("somePackage");
            fail("getFormItems(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetFormItemsIOProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new IOException("mock io error")).once();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getFormItems("somePackage");
            fail("getFormItems(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetFormItemsUnknownProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getFormItems("somePackage");
            fail("getFormItems(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }

    public void testGetFormsOK() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        FormRepresentation form1 = RESTAbstractTest.createMockForm("form1", "oneParam");
        String jsonForm1 = FormEncodingFactory.getEncoder().encode(form1);
        FormRepresentation form2 = RESTAbstractTest.createMockForm("form2", "anotherParam");
        String jsonForm2 = FormEncodingFactory.getEncoder().encode(form2);
        StringBuilder props = new StringBuilder();
        props.append("form1AutoForm.formdef=AAAAA\n");
        props.append("somethingElse.json=AAAAA\n");
        props.append("form2AutoForm.formdef=AAAAA\n");
        responses.put("GET " + helper.getApiSearchUrl("somePackage"), props.toString());
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form1AutoForm.formdef", jsonForm1);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "form2AutoForm.formdef", jsonForm2);        
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("Unexpected call"))).times(3);
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        List<FormRepresentation> forms = service.getForms("somePackage");
        EasyMock.verify(client);
        
        assertNotNull("forms shouldn't be null", forms);
        assertEquals("forms should have 2 elements", 2, forms.size());
        assertTrue("forms should contain form1", forms.contains(form1));
        assertTrue("forms should contain form2", forms.contains(form2));
    }
    
    public void testGetFormsIOProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new IOException("mock io error")).once();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getForms("somePackage");
            fail("getForms(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetFormsServiceProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        StringBuilder props = new StringBuilder();
        props.append("form1AutoForm.formdef=AAAAA\n");
        props.append("somethingElse.json=AAAAA\n");
        props.append("form2AutoForm.formdef=AAAAA\n");
        responses.put("GET " + helper.getApiSearchUrl("somePackage"), props.toString());
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IOException("Problem reading one form"))).times(2);
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getForms("somePackage");
            fail("getForms(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetFormsUnknownProblem() throws Exception {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getForms("somePackage");
            fail("getForms(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testDeleteFormOK() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        responses.put("DELETE " + helper.getApiSearchUrl("somePackage") + "myForm.formdef", "OK");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        service.deleteForm("somePackage", "myForm");
        EasyMock.verify(client);
    }
    
    public void testDeleteFormEmptyId() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        
        service.deleteForm(null, null);
        service.deleteForm("", null);
        service.deleteForm("somePackage", null);
        service.deleteForm("somePackage", "");
    }
    
    public void testDeleteFormIOProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).andThrow(new IOException("mock io error")).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.deleteForm("somePackage", "myForm");
            fail("deleteForm(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testDeleteFormUnknownProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.deleteForm("somePackage", "myForm");
            fail("deleteForm(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }

    public void testDeleteFormItemOK() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        responses.put("DELETE " + helper.getApiSearchUrl("somePackage") + "myItem.json", "OK");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        service.deleteFormItem("somePackage", "myItem");
        EasyMock.verify(client);
    }
    
    public void testDeleteFormItemIOProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).andThrow(new IOException("mock io error")).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.deleteFormItem("somePackage", "myForm");
            fail("deleteFormItem(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testDeleteFormItemUnknownProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockDeleteMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.deleteFormItem("somePackage", "myForm");
            fail("deleteFormItem(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }

    public void testSaveTemplateInsertOK() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);

        Map<String, String> responses2 = new HashMap<String, String>();
        Map<String, String> responses3 = new HashMap<String, String>();
        
        Map<String, Integer> statuses1 = new HashMap<String, Integer>();
        statuses1.put("GET " + helper.getApiSearchUrl("somePackage") + "template.txt", 404);
        responses2.put("POST " + helper.getRestBaseUrl() + "somePackage/assets", "OK");
        responses3.put("PUT " + helper.getRestBaseUrl() + "somePackage/assets/template/source", "OK");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(statuses1)).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPostMethod.class))).
            andAnswer(new MockAnswer(responses2, new IllegalArgumentException("unexpected call"))).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPutMethod.class))).
            andAnswer(new MockAnswer(responses3, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        service.saveTemplate("somePackage", "template.txt", "my template content");
        EasyMock.verify(client);
    }
    
    public void testSaveTemplateUpdateOK() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);

        Map<String, String> responses1 = new HashMap<String, String>();
        Map<String, String> responses2 = new HashMap<String, String>();
        
        responses1.put("GET " + helper.getApiSearchUrl("somePackage") + "template.txt", "old template content");
        responses2.put("PUT " + helper.getRestBaseUrl() + "somePackage/assets/template/source", "OK");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses1, new IllegalArgumentException("unexpected call"))).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPutMethod.class))).
            andAnswer(new MockAnswer(responses2, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        service.saveTemplate("somePackage", "template.txt", "my template content");
        EasyMock.verify(client);
    }

    public void testSaveTemplateGetProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);

        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.saveTemplate("somePackage", "template.txt", "my template content");
            fail ("saveTemplate(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testSaveTemplatePostProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);

        Map<String, Integer> statuses1 = new HashMap<String, Integer>();
        statuses1.put("GET " + helper.getApiSearchUrl("somePackage") + "template.txt", 404);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(statuses1)).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPostMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.saveTemplate("somePackage", "template.txt", "my template content");
            fail ("saveTemplate(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testSaveTemplatePutProblem() throws Exception {
        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);

        Map<String, String> responses2 = new HashMap<String, String>();
        
        Map<String, Integer> statuses1 = new HashMap<String, Integer>();
        statuses1.put("GET " + helper.getApiSearchUrl("somePackage") + "template.txt", 404);
        responses2.put("POST " + helper.getRestBaseUrl() + "somePackage/assets", "OK");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(statuses1)).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPostMethod.class))).
            andAnswer(new MockAnswer(responses2, new IllegalArgumentException("unexpected call"))).once();
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPutMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.saveTemplate("somePackage", "template.txt", "my template content");
            fail ("saveTemplate(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    private GuvnorFormDefinitionService createService(String baseUrl, String user, String pass) {
        GuvnorFormDefinitionService service = new GuvnorFormDefinitionService();
        service.setBaseUrl(baseUrl);
        service.setUser(user);
        service.setPassword(pass);
        service.setHelper(new GuvnorHelper(baseUrl, user, pass) {
            @Override
            public GetMethod createGetMethod(String url) {
                return new MockGetMethod(url);
            }
            @Override
            public PostMethod createPostMethod(String url) {
                return new MockPostMethod(url);
            }
            @Override
            public DeleteMethod createDeleteMethod(String url) {
                return new MockDeleteMethod(url);
            }
            @Override
            public PutMethod createPutMethod(String url) {
                return new MockPutMethod(url);
            }
        });
        return service;
    }
}
