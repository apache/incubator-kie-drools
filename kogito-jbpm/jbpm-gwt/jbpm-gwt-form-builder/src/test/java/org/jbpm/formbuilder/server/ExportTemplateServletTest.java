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

import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formbuilder.shared.form.FormDefinitionService;
import org.jbpm.formbuilder.shared.task.TaskDefinitionService;
import org.jbpm.formbuilder.shared.task.TaskServiceException;

public class ExportTemplateServletTest extends TestCase {


    public void testInitOK() throws Exception {
        ExportTemplateServlet servlet = new ExportTemplateServlet();
        ServletConfig config = EasyMock.createMock(ServletConfig.class);
        
        EasyMock.replay(config);
        servlet.init(config);
        EasyMock.verify(config);
    }
    
    public void testDoGetOK() throws Exception {
        String uuid = UUID.randomUUID().toString();
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("jbpm").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("uuid"))).andReturn(uuid).once();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        ExportTemplateServlet servlet = createServlet(formService, taskService);
        EasyMock.expect(taskService.getContainingPackage(EasyMock.eq(uuid))).andReturn("somePackage").once();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam");
        form.setProcessName("MY_PROCESS");
        form.setTaskId("MY_TASK");
        EasyMock.expect(formService.getFormByUUID(EasyMock.eq("somePackage"), EasyMock.eq(uuid))).andReturn(form).once();
        formService.saveTemplate(EasyMock.eq("somePackage"), EasyMock.eq("MY_TASK-taskform.ftl"), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, taskService, formService);
        servlet.doGet(request, response);
        EasyMock.verify(request, response, taskService, formService);
    }    
    
    public void testDoGetEmptyName() throws Exception {
        String uuid = UUID.randomUUID().toString();
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("jbpm").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("uuid"))).andReturn(uuid).once();
        FormDefinitionService formService = EasyMock.createMock(FormDefinitionService.class);
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        ExportTemplateServlet servlet = createServlet(formService, taskService);
        EasyMock.expect(taskService.getContainingPackage(EasyMock.eq(uuid))).andReturn("somePackage").once();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam");
        form.setProcessName("");
        form.setTaskId("");
        EasyMock.expect(formService.getFormByUUID(EasyMock.eq("somePackage"), EasyMock.eq(uuid))).andReturn(form).once();
        
        EasyMock.replay(request, response, taskService, formService);
        servlet.doGet(request, response);
        EasyMock.verify(request, response, taskService, formService);
    }

    public void testDoGetEmptyProfile() throws Exception {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn(null).once();
        ExportTemplateServlet servlet = createServlet(null, null);
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response);
        servlet.doGet(request, response);
        EasyMock.verify(request, response);
    }

    public void testDoGetAnyProblem() throws Exception {
        String uuid = UUID.randomUUID().toString();
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter(EasyMock.eq("profile"))).andReturn("jbpm").once();
        EasyMock.expect(request.getParameter(EasyMock.eq("uuid"))).andReturn(uuid).once();
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        ExportTemplateServlet servlet = createServlet(null, taskService);
        EasyMock.expect(taskService.getContainingPackage(EasyMock.eq(uuid))).andThrow(new TaskServiceException()).once();
        response.sendError(EasyMock.eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), EasyMock.anyObject(String.class));
        EasyMock.expectLastCall().once();
        
        EasyMock.replay(request, response, taskService);
        servlet.doGet(request, response);
        EasyMock.verify(request, response, taskService);
    }
    
    private ExportTemplateServlet createServlet(final FormDefinitionService formService, final TaskDefinitionService taskService) {
        return new ExportTemplateServlet() {
            private static final long serialVersionUID = 1L;
            @Override
            protected FormDefinitionService createFormService(HttpServletRequest request) {
                return formService;
            }
            @Override
            protected TaskDefinitionService createTaskService(HttpServletRequest request) {
                return taskService;
            }
        };
    }
}
