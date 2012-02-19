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

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;
import org.jbpm.formbuilder.shared.form.FormDefinitionService;
import org.jbpm.formbuilder.shared.form.FormServiceException;
import org.jbpm.formbuilder.shared.task.TaskDefinitionService;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.jbpm.formbuilder.shared.task.TaskServiceException;

public class EmbedingServletTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
    }
    
    public void testInitOK() throws Exception {
        EmbedingServlet servlet = new EmbedingServlet();
        ServletConfig config = EasyMock.createMock(ServletConfig.class);
        
        EasyMock.replay(config);
        servlet.init(config);
        EasyMock.verify(config);
    }

    public void testDoGetOK() throws Exception {
        String uuid = UUID.randomUUID().toString();
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("guvnor").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("uuid"))).andReturn(uuid).once();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        EasyMock.expect(taskService.getContainingPackage(EasyMock.eq(uuid))).andReturn("somePackage").once();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "my1Param", "my2Param");
        EasyMock.expect(formService.getFormByUUID(EasyMock.eq("somePackage"), EasyMock.eq(uuid))).andReturn(form).once();
        request.setAttribute(EasyMock.eq("jsonData"), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        RequestDispatcher dispatcher = EasyMock.createMock(RequestDispatcher.class);
        dispatcher.forward(request, response);
        EasyMock.expectLastCall().once();
        EasyMock.expect(request.getRequestDispatcher(EasyMock.eq("/FormBuilder.jsp"))).andReturn(dispatcher);
        EmbedingServlet servlet = createServlet(formService, taskService);
        
        EasyMock.replay(request, response, formService, taskService, dispatcher);
        servlet.doGet(request, response);
        EasyMock.verify(request, response, formService, taskService, dispatcher);
    }

    public void testDoGetWrongProfile() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("somethingdifferentfromguvnor").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        EmbedingServlet servlet = createServlet(null, null);
        
        EasyMock.replay(request, response);
        servlet.doGet(request, response);
        EasyMock.verify(request, response);
    }
    
    public void testDoGetTaskServiceProblem() throws Exception {
        String uuid = UUID.randomUUID().toString();
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("guvnor").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("uuid"))).andReturn(uuid).once();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        EasyMock.expect(taskService.getContainingPackage(EasyMock.eq(uuid))).andThrow(new TaskServiceException()).once();
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        
        EasyMock.replay(request, response, formService, taskService);
        servlet.doGet(request, response);
        EasyMock.verify(request, response, formService, taskService);
    }
    
    public void testDoGetFormServiceProblem() throws Exception {
        String uuid = UUID.randomUUID().toString();
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("guvnor").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("uuid"))).andReturn(uuid).once();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        EasyMock.expect(taskService.getContainingPackage(EasyMock.eq(uuid))).andReturn("somePackage").once();
        EasyMock.expect(formService.getFormByUUID(EasyMock.eq("somePackage"), EasyMock.eq(uuid))).andThrow(new FormServiceException()).once();
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        
        EasyMock.replay(request, response, formService, taskService);
        servlet.doGet(request, response);
        EasyMock.verify(request, response, formService, taskService);
    }
    
    public void testDoGetEncodingProblem() throws Exception {
        String uuid = UUID.randomUUID().toString();
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("guvnor").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("uuid"))).andReturn(uuid).once();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        EasyMock.expect(taskService.getContainingPackage(EasyMock.eq(uuid))).andReturn("somePackage").once();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "my1Param", "my2Param");
        EasyMock.expect(formService.getFormByUUID(EasyMock.eq("somePackage"), EasyMock.eq(uuid))).andReturn(form).once();
        FormRepresentationEncoder encoder = EasyMock.createMock(FormRepresentationEncoder.class);
        EasyMock.expect(encoder.encode(EasyMock.eq(form))).andThrow(new FormEncodingException()).once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        FormEncodingFactory.register(encoder, FormEncodingFactory.getDecoder());
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, formService, taskService, encoder);
        servlet.doGet(request, response);
        EasyMock.verify(request, response, formService, taskService, encoder);
    }
    
    public void testDoGetUnknownProblem() throws Exception {
        String uuid = UUID.randomUUID().toString();
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("guvnor").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("uuid"))).andReturn(uuid).once();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        EasyMock.expect(taskService.getContainingPackage(EasyMock.eq(uuid))).andReturn("somePackage").once();
        EasyMock.expect(formService.getFormByUUID(EasyMock.eq("somePackage"), EasyMock.eq(uuid))).andThrow(new NullPointerException()).once();
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        
        EasyMock.replay(request, response, formService, taskService);
        servlet.doGet(request, response);
        EasyMock.verify(request, response, formService, taskService);
    }

    public void testDoPostOK() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("designer").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("userTask"))).andReturn("USER_TASK").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("processName"))).andReturn("PROCESS_NAME").once();
        String xml = "<some><bpmn2><content/></bpmn2></some>";
        BufferedReader reader = new BufferedReader(new StringReader(xml));
        EasyMock.expect(request.getReader()).andReturn(reader);
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        TaskRef task = new TaskRef();
        task.setPackageName("somePackage");
        EasyMock.expect(taskService.getBPMN2Task(EasyMock.eq(xml), EasyMock.eq("PROCESS_NAME"), EasyMock.eq("USER_TASK"))).andReturn(task).once();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam");
        EasyMock.expect(formService.getAssociatedForm(EasyMock.eq("somePackage"), EasyMock.eq(task))).andReturn(form).once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        request.setAttribute(EasyMock.eq("jsonData"), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        RequestDispatcher dispatcher = EasyMock.createMock(RequestDispatcher.class);
        EasyMock.expect(request.getRequestDispatcher(EasyMock.eq("/FormBuilder.jsp"))).andReturn(dispatcher).once();
        dispatcher.forward(request, response);
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, formService, taskService, dispatcher);
        servlet.doPost(request, response);
        EasyMock.verify(request, response, formService, taskService, dispatcher);
    }
    
    public void testDoPostWrongProfile() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("somethingdifferentfromdesigner").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        EmbedingServlet servlet = createServlet(null, null);
        
        EasyMock.replay(request, response);
        servlet.doPost(request, response);
        EasyMock.verify(request, response);
    }
    
    public void testDoPostNoTask() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("designer").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("userTask"))).andReturn("USER_TASK").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("processName"))).andReturn("PROCESS_NAME").once();
        String xml = "<some><bpmn2><content/></bpmn2></some>";
        BufferedReader reader = new BufferedReader(new StringReader(xml));
        EasyMock.expect(request.getReader()).andReturn(reader);
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        EasyMock.expect(taskService.getBPMN2Task(EasyMock.eq(xml), EasyMock.eq("PROCESS_NAME"), EasyMock.eq("USER_TASK"))).andReturn(null).once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        request.setAttribute(EasyMock.eq("jsonData"), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        RequestDispatcher dispatcher = EasyMock.createMock(RequestDispatcher.class);
        EasyMock.expect(request.getRequestDispatcher(EasyMock.eq("/FormBuilder.jsp"))).andReturn(dispatcher).once();
        dispatcher.forward(request, response);
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, formService, taskService, dispatcher);
        servlet.doPost(request, response);
        EasyMock.verify(request, response, formService, taskService, dispatcher);
    }
    
    public void testDoPostNoForm() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("designer").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("userTask"))).andReturn("USER_TASK").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("processName"))).andReturn("PROCESS_NAME").once();
        String xml = "<some><bpmn2><content/></bpmn2></some>";
        BufferedReader reader = new BufferedReader(new StringReader(xml));
        EasyMock.expect(request.getReader()).andReturn(reader);
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        TaskRef task = new TaskRef();
        task.setPackageName("somePackage");
        EasyMock.expect(taskService.getBPMN2Task(EasyMock.eq(xml), EasyMock.eq("PROCESS_NAME"), EasyMock.eq("USER_TASK"))).andReturn(task).once();
        EasyMock.expect(formService.getAssociatedForm(EasyMock.eq("somePackage"), EasyMock.eq(task))).andReturn(null).once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        request.setAttribute(EasyMock.eq("jsonData"), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        RequestDispatcher dispatcher = EasyMock.createMock(RequestDispatcher.class);
        EasyMock.expect(request.getRequestDispatcher(EasyMock.eq("/FormBuilder.jsp"))).andReturn(dispatcher).once();
        dispatcher.forward(request, response);
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, formService, taskService, dispatcher);
        servlet.doPost(request, response);
        EasyMock.verify(request, response, formService, taskService, dispatcher);
    }
    
    public void testDoPostTaskServiceProblem() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("designer").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("userTask"))).andReturn("USER_TASK").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("processName"))).andReturn("PROCESS_NAME").once();
        String xml = "<some><bpmn2><content/></bpmn2></some>";
        BufferedReader reader = new BufferedReader(new StringReader(xml));
        EasyMock.expect(request.getReader()).andReturn(reader);
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        TaskRef task = new TaskRef();
        task.setPackageName("somePackage");
        EasyMock.expect(taskService.getBPMN2Task(EasyMock.eq(xml), EasyMock.eq("PROCESS_NAME"), EasyMock.eq("USER_TASK"))).
            andThrow(new TaskServiceException("problem")).once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, formService, taskService);
        servlet.doPost(request, response);
        EasyMock.verify(request, response, formService, taskService);
    }
    
    public void testDoPostFormServiceProblem() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("designer").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("userTask"))).andReturn("USER_TASK").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("processName"))).andReturn("PROCESS_NAME").once();
        String xml = "<some><bpmn2><content/></bpmn2></some>";
        BufferedReader reader = new BufferedReader(new StringReader(xml));
        EasyMock.expect(request.getReader()).andReturn(reader);
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        TaskRef task = new TaskRef();
        task.setPackageName("somePackage");
        EasyMock.expect(taskService.getBPMN2Task(EasyMock.eq(xml), EasyMock.eq("PROCESS_NAME"), EasyMock.eq("USER_TASK"))).andReturn(task).once();
        EasyMock.expect(formService.getAssociatedForm(EasyMock.eq("somePackage"), EasyMock.eq(task))).
            andThrow(new FormServiceException("problem")).once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, formService, taskService);
        servlet.doPost(request, response);
        EasyMock.verify(request, response, formService, taskService);
    }
    
    public void testDoPostEncodingProblem() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("designer").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("userTask"))).andReturn("USER_TASK").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("processName"))).andReturn("PROCESS_NAME").once();
        String xml = "<some><bpmn2><content/></bpmn2></some>";
        BufferedReader reader = new BufferedReader(new StringReader(xml));
        EasyMock.expect(request.getReader()).andReturn(reader);
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        TaskRef task = new TaskRef();
        task.setPackageName("somePackage");
        EasyMock.expect(taskService.getBPMN2Task(EasyMock.eq(xml), EasyMock.eq("PROCESS_NAME"), EasyMock.eq("USER_TASK"))).andReturn(task).once();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam");
        EasyMock.expect(formService.getAssociatedForm(EasyMock.eq("somePackage"), EasyMock.eq(task))).andReturn(form).once();
        FormRepresentationEncoder encoder = EasyMock.createMock(FormRepresentationEncoder.class);
        EasyMock.expect(encoder.encode(EasyMock.eq(form))).andThrow(new FormEncodingException()).once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        FormEncodingFactory.register(encoder, FormEncodingFactory.getDecoder());
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, formService, taskService, encoder);
        servlet.doPost(request, response);
        EasyMock.verify(request, response, formService, taskService, encoder);
    }
    
    public void testDoPostUnknownProblem() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("designer").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("usr"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("pwd"))).andReturn(null).once();
        EasyMock.expect(request.getParameter(EasyMock.eq("userTask"))).andReturn("USER_TASK").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("processName"))).andReturn("PROCESS_NAME").once();
        String xml = "<some><bpmn2><content/></bpmn2></some>";
        BufferedReader reader = new BufferedReader(new StringReader(xml));
        EasyMock.expect(request.getReader()).andReturn(reader);
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        TaskRef task = new TaskRef();
        task.setPackageName("somePackage");
        EasyMock.expect(taskService.getBPMN2Task(EasyMock.eq(xml), EasyMock.eq("PROCESS_NAME"), EasyMock.eq("USER_TASK"))).
            andThrow(new NullPointerException()).once();
        EmbedingServlet servlet = createServlet(formService, taskService);
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, formService, taskService);
        servlet.doPost(request, response);
        EasyMock.verify(request, response, formService, taskService);
    }

    private EmbedingServlet createServlet(final FormDefinitionService formService, final TaskDefinitionService taskService) {
         return new EmbedingServlet() {
            private static final long serialVersionUID = 1L;
            @Override
            protected FormDefinitionService createFormService(HttpServletRequest request, String usr, String pwd) {
                return formService;
            }
            @Override
            protected TaskDefinitionService createTaskService(HttpServletRequest request, String usr, String pwd) {
                return taskService;
            }
        };
    }
}
