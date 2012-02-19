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
package org.jbpm.formbuilder.server;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.easymock.EasyMock;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.server.render.Renderer;
import org.jbpm.formapi.server.render.RendererException;
import org.jbpm.formapi.server.trans.Translator;
import org.jbpm.formapi.server.trans.TranslatorException;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;
import org.jbpm.formbuilder.server.xml.FormDefDTO;
import org.jbpm.formbuilder.server.xml.FormItemDefDTO;
import org.jbpm.formbuilder.server.xml.FormPreviewDTO;
import org.jbpm.formbuilder.server.xml.FormPreviewParameterDTO;
import org.jbpm.formbuilder.server.xml.ListFormsDTO;
import org.jbpm.formbuilder.server.xml.ListFormsItemsDTO;
import org.jbpm.formbuilder.shared.form.FormDefinitionService;
import org.jbpm.formbuilder.shared.form.FormServiceException;
import org.jbpm.formbuilder.shared.form.MockFormDefinitionService;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class RESTFormServiceTest extends RESTAbstractTest {

    public void testSetContextOK() throws Exception {
        RESTFormService restService = new RESTFormService();
        URL pathToClasses = getClass().getResource("/FormBuilder.properties");
		String filePath = pathToClasses.toExternalForm();
		//assumes compilation is in target/classes
		filePath = filePath.replace("target/classes/FormBuilder.properties", "src/main/webapp");
		filePath = filePath + "/WEB-INF/springComponents.xml";
		FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(filePath);
		ServiceFactory.getInstance().setBeanFactory(ctx);
        ServletContext context = EasyMock.createMock(ServletContext.class);

        EasyMock.replay(context);
        restService.setContext(context);
        EasyMock.verify(context);

        FormDefinitionService service = restService.getFormService();
        assertNotNull("service shouldn't be null", service);
    }
    
    //test happy path for RESTFormService.getForms(...)
    public void testGetFormsOK() throws Exception {
        List<FormRepresentation> forms = new ArrayList<FormRepresentation>();
        forms.add(createMockForm("form1", "param1", "param2", "param3"));
        forms.add(createMockForm("form2", "paramA", "paramB", "paramC"));
        
        getFormsOK(forms);
    }

    //test happy path returning no forms for RESTFormService.getForms(...)
    public void testGetFormsOKEmpty() throws Exception {
        getFormsOK(new ArrayList<FormRepresentation>());
    }
    
    private void getFormsOK(List<FormRepresentation> retval) throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        EasyMock.expect(formService.getForms(EasyMock.same("somePackage"))).andReturn(retval).once();
        restService.setFormService(formService);
        
        EasyMock.replay(formService, context);
        Response resp = restService.getForms("somePackage", context);
        EasyMock.verify(formService, context);
        
        Object entity = assertXmlOkResponse(resp);
        assertTrue("entity should be of type ListFormsDTO", entity instanceof ListFormsDTO);
        ListFormsDTO dto = (ListFormsDTO) entity;
        assertNotNull("dto.getForm() shouldn't be null", dto.getForm());
        assertEquals("dto.getForm() should be of size " + retval.size() + " but it is of size " + dto.getForm().size(), 
                dto.getForm().size(), retval.size());
    }
    
    //test response to a FormServiceException for RESTFormService.getForms(...)
    public void testGetFormsServiceProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        FormServiceException exception = new FormServiceException("Something going wrong");
        
        EasyMock.expect(formService.getForms(EasyMock.same("somePackage"))).andThrow(exception).once();
        restService.setFormService(formService);
        
        EasyMock.replay(formService, context);
        Response resp = restService.getForms("somePackage", context);
        EasyMock.verify(formService, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to a FormEncodingException for RESTFormService.getForms(...)
    public void testGetFormsEncodingProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        
        FormRepresentationEncoder encoder = EasyMock.createMock(FormRepresentationEncoder.class);
        EasyMock.expect(encoder.encode(EasyMock.anyObject(FormRepresentation.class))).andThrow(exception).once();
        
        FormEncodingFactory.register(encoder, FormEncodingServerFactory.getDecoder());
        
        List<FormRepresentation> forms = new ArrayList<FormRepresentation>();
        forms.add(createMockForm("form1", "param1", "param2", "param3"));
        forms.add(createMockForm("form2", "paramA", "paramB", "paramC"));
        
        EasyMock.expect(formService.getForms(EasyMock.same("somePackage"))).andReturn(forms).once();
        restService.setFormService(formService);
        
        EasyMock.replay(formService, context, encoder);
        Response resp = restService.getForms("somePackage", context);
        EasyMock.verify(formService, context, encoder);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path for RESTFormService.getForm(...)
    public void testGetFormOK() throws Exception {
        RESTFormService restService = new RESTFormService();
        
        FormRepresentation form = createMockForm("myForm", "myParam1", "myParam2", "myParam3");
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(formService.getForm(EasyMock.same("somePackage"), EasyMock.same("myFormId"))).andReturn(form);
        restService.setFormService(formService);
        
        EasyMock.replay(formService, context);
        Response resp = restService.getForm("somePackage", "myFormId", context);
        EasyMock.verify(formService, context);
        
        Object entity = assertXmlOkResponse(resp);
        ListFormsDTO dto = (ListFormsDTO) entity;
        assertNotNull("dto.getForm() shouldn't be null", dto.getForm());
        assertEquals("dto.getForm() should be of one element but it is of size " + dto.getForm().size(), dto.getForm().size(), 1);
        FormDefDTO formDto = dto.getForm().iterator().next();
        assertTrue("formDto should be named myForm but it isn't", formDto.getJson().contains("myForm"));
        assertTrue("formDto should contain a parameter called myParam1 but it doesn't", formDto.getJson().contains("myParam1"));
        assertTrue("formDto should contain a parameter called myParam2 but it doesn't", formDto.getJson().contains("myParam2"));
        assertTrue("formDto should contain a parameter called myParam3 but it doesn't", formDto.getJson().contains("myParam3"));
    }
    
    //test response to a FormServiceException for RESTFormService.getForm(...)
    public void testGetFormServiceProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        FormServiceException exception = new FormServiceException("Something going wrong");
        
        EasyMock.expect(formService.getForm(EasyMock.same("somePackage"), EasyMock.same("myFormId"))).andThrow(exception).once();
        restService.setFormService(formService);
        
        EasyMock.replay(formService, context);
        Response resp = restService.getForm("somePackage", "myFormId", context);
        EasyMock.verify(formService, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to a FormEncodingException for RESTFormService.getForm(...)
    public void testGetFormEncodingProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        
        FormRepresentationEncoder encoder = EasyMock.createMock(FormRepresentationEncoder.class);
        EasyMock.expect(encoder.encode(EasyMock.anyObject(FormRepresentation.class))).andThrow(exception).once();
        
        FormEncodingFactory.register(encoder, FormEncodingServerFactory.getDecoder());
        
        FormRepresentation form = createMockForm("form1", "param1", "param2", "param3");
        
        EasyMock.expect(formService.getForm(EasyMock.same("somePackage"), EasyMock.same("myFormId"))).andReturn(form).once();
        restService.setFormService(formService);
        
        EasyMock.replay(formService, context, encoder);
        Response resp = restService.getForm("somePackage", "myFormId", context);
        EasyMock.verify(formService, context, encoder);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    //test happy path for RESTFormService.saveForm(...)
    public void testSaveFormOK() throws Exception {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormRepresentation form = RESTAbstractTest.createMockForm("formToBeSaved", "param1", "param2", "param3");
        EasyMock.expect(formService.saveForm(EasyMock.eq("somePackage"), EasyMock.eq(form))).andReturn("MY_FORM_ID").once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        
        EasyMock.replay(formService, context, session, request);
        Response resp = restService.saveForm(FormEncodingFactory.getEncoder().encode(form), "somePackage", request);
        EasyMock.verify(formService, context, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.CREATED);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        String xml = entity.toString();
        String expected = "<formId>MY_FORM_ID</formId>";
        assertEquals("xml should be " + expected + " but it is " + xml, xml, expected);
    }

    //test response to a FormServiceException for RESTFormService.saveForm(...)
    public void testSaveFormServiceProblem() throws Exception {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormRepresentation form = RESTAbstractTest.createMockForm("formToBeSaved", "param1", "param2", "param3");
        FormServiceException exception = new FormServiceException("Something going wrong");
        EasyMock.expect(formService.saveForm(EasyMock.eq("somePackage"), EasyMock.eq(form))).andThrow(exception).once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        
        EasyMock.replay(formService, context, session, request);
        Response resp = restService.saveForm(FormEncodingFactory.getEncoder().encode(form), "somePackage", request);
        EasyMock.verify(formService, context, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to a FormEncodingException for RESTFormService.saveForm(...)
    public void testSaveFormEncodingProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormRepresentationDecoder decoder = EasyMock.createMock(FormRepresentationDecoder.class);
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), decoder);
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormRepresentation form = RESTAbstractTest.createMockForm("formToBeSaved", "param1", "param2", "param3");
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        EasyMock.expect(decoder.decode(EasyMock.anyObject(String.class))).andThrow(exception).once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);

        
        EasyMock.replay(formService, context, decoder, session, request);
        Response resp = restService.saveForm(FormEncodingFactory.getEncoder().encode(form), "somePackage", request);
        EasyMock.verify(formService, context, decoder, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    //test happy path for RESTFormService.deleteForm(...)
    public void testDeleteFormOK() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        formService.deleteForm(EasyMock.eq("somePackage"), EasyMock.eq("myFormId"));
        EasyMock.expectLastCall().once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        
        EasyMock.replay(formService, context, session, request);
        Response resp = restService.deleteForm("somePackage", "myFormId", request);
        EasyMock.verify(formService, context, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
    }
    
    //test response to a FormServiceException for RESTFormService.deleteForm(...)
    public void testDeleteFormServiceProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        formService.deleteForm(EasyMock.eq("somePackage"), EasyMock.eq("myFormId"));
        FormServiceException exception = new FormServiceException("Something going wrong");
        EasyMock.expectLastCall().andThrow(exception).once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);

        EasyMock.replay(formService, context, session, request);
        Response resp = restService.deleteForm("somePackage", "myFormId", request);
        EasyMock.verify(formService, context, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path for RESTFormService.getFormItems(...)
    public void testGetFormItemsOK() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        Map<String, FormItemRepresentation> map = mockGetFormItems();
        EasyMock.expect(formService.getFormItems(EasyMock.eq("somePackage"))).andReturn(map).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        restService.setFormService(formService);
        
        EasyMock.replay(formService, context);
        Response resp = restService.getFormItems("somePackage", context);
        EasyMock.verify(formService, context);
        
        Object entity = assertXmlOkResponse(resp);
        assertTrue("entity should be of type ListFormsItemsDTO", entity instanceof ListFormsItemsDTO);
        ListFormsItemsDTO dto = (ListFormsItemsDTO) entity;
        List<FormItemDefDTO> items = dto.getFormItem();
        assertNotNull("items shouldn't be null", items);
        assertFalse("items shouldn't be empty", items.isEmpty());
        assertEquals("items size should be " + map.size() + " but is " + items.size(), items.size(), map.size());
    }
    
    //test response to a FormServiceException for RESTFormService.getFormItems(...)
    public void testGetFormItemsServiceProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormServiceException exception = new FormServiceException("Something going wrong");
        EasyMock.expect(formService.getFormItems(EasyMock.eq("somePackage"))).andThrow(exception).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        restService.setFormService(formService);
        
        EasyMock.replay(formService, context);
        Response resp = restService.getFormItems("somePackage", context);
        EasyMock.verify(formService, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to a FormEncodingException for RESTFormService.getFormItems(...)
    public void testGetFormItemsEncodingProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormRepresentationEncoder encoder = EasyMock.createMock(FormRepresentationEncoder.class);
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        EasyMock.expect(encoder.encode(EasyMock.notNull(FormItemRepresentation.class))).andThrow(exception).anyTimes();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        Map<String, FormItemRepresentation> map = mockGetFormItems();
        EasyMock.expect(formService.getFormItems(EasyMock.eq("somePackage"))).andReturn(map).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        restService.setFormService(formService);
        FormEncodingFactory.register(encoder, FormEncodingServerFactory.getDecoder());
        
        EasyMock.replay(formService, context, encoder);
        Response resp = restService.getFormItems("somePackage", context);
        EasyMock.verify(formService, context, encoder);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    private Map<String, FormItemRepresentation> mockGetFormItems() {
        Map<String, FormItemRepresentation> map = new HashMap<String, FormItemRepresentation>();
        FormRepresentation form = createMockForm("myForm", "param1", "param2", "param3");
        Iterator<FormItemRepresentation> myItems = form.getFormItems().iterator();
        map.put("name1", myItems.next());
        map.put("name2", myItems.next());
        map.put("name3", myItems.next());
        return map;
    }

    //test happy path for RESTFormService.getFormItem(...)
    public void testGetFormItemOK() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormItemRepresentation item = createMockForm("myForm", "param1").getFormItems().iterator().next();
        EasyMock.expect(formService.getFormItem(EasyMock.eq("somePackage"), EasyMock.eq("MY_FORM_ITEM_ID"))).andReturn(item).once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        EasyMock.replay(formService, context);
        Response resp = restService.getFormItem("somePackage", "MY_FORM_ITEM_ID", context);
        EasyMock.verify(formService, context);
        
        Object entity = assertXmlOkResponse(resp);
        assertTrue("entity should be of type ListFormsItemsDTO", entity instanceof ListFormsItemsDTO);
        ListFormsItemsDTO dto = (ListFormsItemsDTO) entity;
        List<FormItemDefDTO> items = dto.getFormItem();
        assertNotNull("items shouldn't be null", items);
        assertFalse("items shouldn't be empty", items.isEmpty());
        assertEquals("items size should be 1 but is " + items.size(), items.size(), 1);
    }

    //test response to FormServiceException for RESTFormService.getFormItem(...)
    public void testGetFormItemServiceProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormServiceException exception = new FormServiceException("Something going wrong");
        EasyMock.expect(formService.getFormItem(EasyMock.eq("somePackage"), EasyMock.eq("MY_FORM_ITEM_ID"))).andThrow(exception).once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        EasyMock.replay(formService, context);
        Response resp = restService.getFormItem("somePackage", "MY_FORM_ITEM_ID", context);
        EasyMock.verify(formService, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to FormServiceException for RESTFormService.getFormItem(...)
    public void testGetFormItemEncodingProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormRepresentationEncoder encoder = EasyMock.createMock(FormRepresentationEncoder.class);
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        EasyMock.expect(encoder.encode(EasyMock.notNull(FormItemRepresentation.class))).andThrow(exception).anyTimes();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormItemRepresentation item = createMockForm("myForm", "param1").getFormItems().iterator().next();
        EasyMock.expect(formService.getFormItem(EasyMock.eq("somePackage"), EasyMock.eq("MY_ITEM_ID"))).andReturn(item).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        restService.setFormService(formService);
        FormEncodingFactory.register(encoder, FormEncodingServerFactory.getDecoder());
        
        EasyMock.replay(formService, context, encoder);
        Response resp = restService.getFormItem("somePackage", "MY_ITEM_ID", context);
        EasyMock.verify(formService, context, encoder);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    //test happy path for RESTFormService.saveFormItem(...)
    public void testSaveFormItemOK() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormItemRepresentation item = RESTAbstractTest.createMockForm("formToBeSaved", "param1").getFormItems().iterator().next();
        EasyMock.expect(formService.saveFormItem(EasyMock.eq("somePackage"), EasyMock.eq("MY_FORM_ITEM_ID"), EasyMock.eq(item))).
            andReturn("MY_FORM_ITEM_ID").once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        
        EasyMock.replay(formService, context, session, request);
        Response resp = restService.saveFormItem(FormEncodingFactory.getEncoder().encode(item), "somePackage", "MY_FORM_ITEM_ID", request);
        EasyMock.verify(formService, context, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.CREATED);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        String xml = entity.toString();
        String expected = "<formItemId>MY_FORM_ITEM_ID</formItemId>";
        assertEquals("xml should be " + expected + " but it is " + xml, xml, expected);
    }
    
    //test response to a FormServiceException for RESTFormService.saveFormItem(...)
    public void testSaveFormItemServiceProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormItemRepresentation item = RESTAbstractTest.createMockForm("formToBeSaved", "param1").getFormItems().iterator().next();
        FormServiceException exception = new FormServiceException("Something going wrong");
        EasyMock.expect(formService.saveFormItem(EasyMock.eq("somePackage"), EasyMock.eq("MY_FORM_ITEM_ID"), EasyMock.eq(item))).andThrow(exception).once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        
        EasyMock.replay(formService, context, session, request);
        Response resp = restService.saveFormItem(FormEncodingFactory.getEncoder().encode(item), "somePackage", "MY_FORM_ITEM_ID", request);
        EasyMock.verify(formService, context, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to a FormEncodingException for RESTFormService.saveFormItem(...)
    public void testSaveFormItemEncodingProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormRepresentationDecoder decoder = EasyMock.createMock(FormRepresentationDecoder.class);
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), decoder);
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        FormItemRepresentation item = RESTAbstractTest.createMockForm("formToBeSaved", "param1").getFormItems().iterator().next();
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        EasyMock.expect(decoder.decodeItem(EasyMock.anyObject(String.class))).andThrow(exception).once();
        restService.setFormService(formService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);

        EasyMock.replay(formService, context, decoder, session, request);
        Response resp = restService.saveFormItem(FormEncodingFactory.getEncoder().encode(item), "somePackage", "MY_FORM_ITEM_ID", request);
        EasyMock.verify(formService, context, decoder, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path for RESTFormService.deleteForm(...)
    public void testDeleteFormItemOK() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        formService.deleteForm(EasyMock.eq("somePackage"), EasyMock.eq("MY_FORM_ID"));
        EasyMock.expectLastCall().once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        restService.setFormService(formService);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);

        EasyMock.replay(formService, context, session, request);
        Response resp = restService.deleteForm("somePackage", "MY_FORM_ID", request);
        EasyMock.verify(formService, context, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
    }
    
    //test response to a FormServiceException for RESTFormService.deleteForm(...)
    public void testDeleteFormItemServiceProblem() throws Exception {
        RESTFormService restService = new RESTFormService();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        formService.deleteForm(EasyMock.eq("somePackage"), EasyMock.eq("MY_FORM_ID"));
        FormServiceException exception = new FormServiceException("Something going wrong");
        EasyMock.expectLastCall().andThrow(exception).once();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        restService.setFormService(formService);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        
        EasyMock.replay(formService, context, session, request);
        Response resp = restService.deleteForm("somePackage", "MY_FORM_ID", request);
        EasyMock.verify(formService, context, session, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    private RESTFormService emulateRESTFormService(final Translator t, final TranslatorException e1, final Renderer r, final RendererException e2) {
        return new RESTFormService() {
            @Override
            protected Renderer getRenderer(String language) throws RendererException {
                if (r == null) {
                    if (e1 == null) return super.getRenderer(language);
                    else throw e2;
                }
                return r;
            }
            @Override
            protected Translator getTranslator(String language) throws TranslatorException {
                if (t == null) {
                    if (e2 == null) return super.getTranslator(language);
                    else throw e1;
                }
                return t;
            }
        };
    }

    private FormPreviewDTO createFormPreviewDTO(FormRepresentation form) throws Exception {
        FormPreviewDTO dto = new FormPreviewDTO();
        String jsonBody = FormEncodingFactory.getEncoder().encode(form);
        dto.setRepresentation(jsonBody);
        List<FormPreviewParameterDTO> inputs = new ArrayList<FormPreviewParameterDTO>();
        FormPreviewParameterDTO param1 = new FormPreviewParameterDTO();
        param1.setKey("key1");
        param1.setValue("value1");
        FormPreviewParameterDTO param2 = new FormPreviewParameterDTO();
        param2.setKey("key2");
        param2.setValue("value2");
        inputs.add(param1);
        inputs.add(param2);
        dto.setInput(inputs);
        return dto;
    }
    
    //test happy path for RESTFormService.getFormPreview(...)
    public void testGetFormPreviewOK() throws Exception {
        final Renderer renderer = EasyMock.createMock(Renderer.class);
        final Translator translator = EasyMock.createMock(Translator.class);
        final ServletContext context = EasyMock.createMock(ServletContext.class);
        final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        RESTFormService restService = emulateRESTFormService(translator, null, renderer, null);
        restService.setFormService(new MockFormDefinitionService());
        
        FormRepresentation form = createMockForm("myForm", "key1", "key2");
        FormPreviewDTO dto = createFormPreviewDTO(form);
        
        URL url = new URL("http://www.redhat.com");
        EasyMock.expect(translator.translateForm(EasyMock.eq(form))).andReturn(url).once();
        String htmlResult = "<html><body><div>Hello</div></body></html>";
        @SuppressWarnings("unchecked")
        Map<String, Object> anyObject = EasyMock.anyObject(Map.class);
        EasyMock.expect(renderer.render(EasyMock.anyObject(URL.class), anyObject)).andReturn(htmlResult);
        EasyMock.expect(context.getContextPath()).andReturn("/").anyTimes();
        EasyMock.expect(request.getLocale()).andReturn(Locale.getDefault()).once();
        
        EasyMock.replay(renderer, translator, context, request);
        Response resp = restService.getFormPreview(dto, "lang", context, request);
        EasyMock.verify(renderer, translator, context, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("contentType shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.TEXT_PLAIN);
        String html = entity.toString();
        assertTrue("html should contain data", html.length() > 0);
    }
    
    //test response to a FormEncodingException for RESTFormService.getFormPreview(...)
    public void testGetFormPreviewEncodingProblem() throws Exception {
        final ServletContext context = EasyMock.createMock(ServletContext.class);
        final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        FormRepresentationDecoder decoder = EasyMock.createMock(FormRepresentationDecoder.class);
        RESTFormService restService = new RESTFormService();
        restService.setFormService(new MockFormDefinitionService());
        FormEncodingFactory.register(FormEncodingFactory.getEncoder(), decoder);
        
        FormPreviewDTO dto = new FormPreviewDTO();
        FormRepresentation form = createMockForm("myForm", "key1", "key2");
        String jsonBody = FormEncodingFactory.getEncoder().encode(form);
        dto.setRepresentation(jsonBody);
        
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        EasyMock.expect(decoder.decode(EasyMock.eq(jsonBody))).andThrow(exception).once();
        
        EasyMock.replay(context, request);
        Response resp = restService.getFormPreview(dto, "lang", context, request);
        EasyMock.verify(context, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to a non existing Translator for RESTFormService.getFormPreview(...)
    public void testGetFormPreviewTranslatorNotFound() throws Exception {
        final ServletContext context = EasyMock.createMock(ServletContext.class);
        final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        RESTFormService restService = emulateRESTFormService(null, new TranslatorException("Not finding translator"), null, null);
        restService.setFormService(new MockFormDefinitionService());
        
        FormPreviewDTO dto = new FormPreviewDTO();
        FormRepresentation form = createMockForm("myForm", "key1", "key2");
        String jsonBody = FormEncodingFactory.getEncoder().encode(form);
        dto.setRepresentation(jsonBody);
        
        EasyMock.replay(context, request);
        Response resp = restService.getFormPreview(dto, "lang", context, request);
        EasyMock.verify(context, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to a non existing Renderer for RESTFormService.getFormPreview(...)
    public void testGetFormPreviewRendererNotFound() throws Exception {
        final Translator translator = EasyMock.createMock(Translator.class);
        final ServletContext context = EasyMock.createMock(ServletContext.class);
        final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        RESTFormService restService = emulateRESTFormService(translator, null, null, new RendererException("not finding a renderer"));
        restService.setFormService(new MockFormDefinitionService());
        
        FormPreviewDTO dto = new FormPreviewDTO();
        FormRepresentation form = createMockForm("myForm", "key1", "key2");
        String jsonBody = FormEncodingFactory.getEncoder().encode(form);
        dto.setRepresentation(jsonBody);
        
        URL url = new URL("http://www.redhat.com");
        EasyMock.expect(translator.translateForm(EasyMock.eq(form))).andReturn(url).once();
        EasyMock.expect(context.getContextPath()).andReturn("/").anyTimes();
        
        EasyMock.replay(translator, context, request);
        Response resp = restService.getFormPreview(dto, "lang", context, request);
        EasyMock.verify(translator, context, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to a LanguageException for RESTFormService.getFormPreview(...)
    public void testGetFormPreviewTranslatorProblem() throws Exception {
        final Renderer renderer = EasyMock.createMock(Renderer.class);
        final Translator translator = EasyMock.createMock(Translator.class);
        final ServletContext context = EasyMock.createMock(ServletContext.class);
        final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        RESTFormService restService = emulateRESTFormService(translator, null, renderer, null);
        restService.setFormService(new MockFormDefinitionService());
        
        FormRepresentation form = createMockForm("myForm", "key1", "key2");
        FormPreviewDTO dto = createFormPreviewDTO(form);
        
        TranslatorException exception = new TranslatorException("Something going wrong");
        EasyMock.expect(translator.translateForm(EasyMock.eq(form))).andThrow(exception).once();
        EasyMock.expect(context.getContextPath()).andReturn("/").anyTimes();
        
        EasyMock.replay(renderer, translator, context, request);
        Response resp = restService.getFormPreview(dto, "lang", context, request);
        EasyMock.verify(renderer, translator, context, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    //test response to a RendererException for RESTFormService.getFormPreview(...)
    public void testGetFormPreviewRendererProblem() throws Exception {
        final Renderer renderer = EasyMock.createMock(Renderer.class);
        final Translator translator = EasyMock.createMock(Translator.class);
        final ServletContext context = EasyMock.createMock(ServletContext.class);
        final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        RESTFormService restService = emulateRESTFormService(translator, null, renderer, null);
        restService.setFormService(new MockFormDefinitionService());
        
        FormRepresentation form = createMockForm("myForm", "key1", "key2");
        FormPreviewDTO dto = createFormPreviewDTO(form);
        
        EasyMock.expect(translator.translateForm(EasyMock.eq(form))).andReturn(new URL("http://www.redhat.com")).once();
        RendererException exception = new RendererException("Something going wrong");
        @SuppressWarnings("unchecked")
        Map<String, Object> anyObject = EasyMock.anyObject(Map.class);
        EasyMock.expect(renderer.render(EasyMock.anyObject(URL.class), anyObject)).andThrow(exception).once();
        EasyMock.expect(context.getContextPath()).andReturn("/").anyTimes();
        EasyMock.expect(request.getLocale()).andReturn(Locale.getDefault()).once();
        
        EasyMock.replay(renderer, translator, context, request);
        Response resp = restService.getFormPreview(dto, "lang", context, request);
        EasyMock.verify(renderer, translator, context, request);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path for RESTFormService.getFormTemplate(...)
    public void testGetFormTemplateOK() throws Exception {
        Translator translator = EasyMock.createMock(Translator.class);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        RESTFormService restService = emulateRESTFormService(translator, null, null, null);
        restService.setFormService(new MockFormDefinitionService());
        
        FormRepresentation form = createMockForm("myForm", "key1", "key2");
        FormPreviewDTO dto = createFormPreviewDTO(form);
        
        EasyMock.expect(translator.translateForm(EasyMock.eq(form))).andReturn(new URL("http://www.redhat.com")).once();
        
        EasyMock.replay(translator, context);
        Response resp = restService.getFormTemplate(dto, "lang", context);
        EasyMock.verify(translator, context);
        
        assertNotNull("resp shouldn't be null", resp);
        Object entity = assertXmlOkResponse(resp);
        String xml = entity.toString();
        assertTrue("xml should start with <fileName>", xml.startsWith("<fileName>"));
        assertTrue("xml should end with </fileName>", xml.endsWith("</fileName>"));
    }
    
    //test response to a FormEncodingException for RESTFormService.getFormTemplate(...)
    public void testGetFormTemplateEncodingProblem() throws Exception {
        ServletContext context = EasyMock.createMock(ServletContext.class);
        RESTFormService restService = new RESTFormService();
        restService.setFormService(new MockFormDefinitionService());
        FormRepresentationDecoder decoder = EasyMock.createMock(FormRepresentationDecoder.class);
        FormEncodingFactory.register(FormEncodingFactory.getEncoder(), decoder);
        FormRepresentation form = createMockForm("myForm", "key1", "key2");
        FormPreviewDTO dto = createFormPreviewDTO(form);
        
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        EasyMock.expect(decoder.decode(EasyMock.eq(dto.getRepresentation()))).andThrow(exception).once();
        
        EasyMock.replay(decoder, context);
        Response resp = restService.getFormTemplate(dto, "lang", context);
        EasyMock.verify(decoder, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test response to a LanguageException for RESTFormService.getFormTemplate(...)
    public void testGetFormTemplateTranslatorProblem() throws Exception {
        Translator translator = EasyMock.createMock(Translator.class);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        RESTFormService restService = emulateRESTFormService(translator, null, null, null);
        restService.setFormService(new MockFormDefinitionService());
        
        FormRepresentation form = createMockForm("myForm", "key1", "key2");
        FormPreviewDTO dto = createFormPreviewDTO(form);
        
        TranslatorException exception = new TranslatorException("Something going wrong");
        EasyMock.expect(translator.translateForm(EasyMock.eq(form))).andThrow(exception).once();
        
        EasyMock.replay(translator, context);
        Response resp = restService.getFormTemplate(dto, "lang", context);
        EasyMock.verify(translator, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
}
