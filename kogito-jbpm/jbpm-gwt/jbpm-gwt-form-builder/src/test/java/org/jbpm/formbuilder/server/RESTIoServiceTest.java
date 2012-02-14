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
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.easymock.EasyMock;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jbpm.formbuilder.server.xml.ListTasksDTO;
import org.jbpm.formbuilder.server.xml.TaskRefDTO;
import org.jbpm.formbuilder.shared.task.TaskDefinitionService;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.jbpm.formbuilder.shared.task.TaskServiceException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class RESTIoServiceTest extends RESTAbstractTest {

    public void testSetContextOK() throws Exception {
        RESTIoService restService = new RESTIoService();
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

        TaskDefinitionService service = restService.getTaskService();
        assertNotNull("service shouldn't be null", service);
    }
    
    //test happy path for RESTIoService.getIoAssociations(...)
    public void testGetIoAssociationsOK() throws Exception {
        RESTIoService restService = new RESTIoService();
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        List<TaskRef> tasks = new ArrayList<TaskRef>();
        tasks.add(new TaskRef());
        tasks.add(new TaskRef());
        EasyMock.expect(taskService.query(EasyMock.eq("somePackage"), EasyMock.eq("someFilter"))).andReturn(tasks).once();
        restService.setTaskService(taskService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        EasyMock.replay(taskService, context);
        Response resp = restService.getIoAssociations("someFilter", "somePackage", context);
        EasyMock.verify(taskService, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        assertTrue("entity should be of type ListTasksDTO", entity instanceof ListTasksDTO);
        ListTasksDTO dto = (ListTasksDTO) entity;
        List<TaskRefDTO> dtoTasks = dto.getTask();
        assertNotNull("dtoTasks shouldn't be null", dtoTasks);
        assertEquals("dtoTasks should have " + tasks.size() + " elements but it has " + dtoTasks.size(), tasks.size(), dtoTasks.size());
    }
    
    //test happy path without filtering param for RESTIoService.getIoAssociations(...)
    public void testGetIoAssociationsNoQParam() throws Exception {
        RESTIoService restService = new RESTIoService();
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        List<TaskRef> tasks = new ArrayList<TaskRef>();
        tasks.add(new TaskRef());
        tasks.add(new TaskRef());
        EasyMock.expect(taskService.query(EasyMock.eq("somePackage"), EasyMock.eq(""))).andReturn(tasks).once();
        restService.setTaskService(taskService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        EasyMock.replay(taskService, context);
        Response resp = restService.getIoAssociations(null, "somePackage", context);
        EasyMock.verify(taskService, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        assertTrue("entity should be of type ListTasksDTO", entity instanceof ListTasksDTO);
        ListTasksDTO dto = (ListTasksDTO) entity;
        List<TaskRefDTO> dtoTasks = dto.getTask();
        assertNotNull("dtoTasks shouldn't be null", dtoTasks);
        assertEquals("dtoTasks should have " + tasks.size() + " elements but it has " + dtoTasks.size(), tasks.size(), dtoTasks.size());
    }
    
    //test response to a TaskServiceException for RESTIoService.getIoAssociations(...)
    public void testGetIoAssociationsServiceProblem() throws Exception {
        RESTIoService restService = new RESTIoService();
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        TaskServiceException exception = new TaskServiceException("Something going wrong");
        EasyMock.expect(taskService.query(EasyMock.eq("somePackage"), EasyMock.eq(""))).andThrow(exception).once();
        restService.setTaskService(taskService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        EasyMock.replay(taskService, context);
        Response resp = restService.getIoAssociations(null, "somePackage", context);
        EasyMock.verify(taskService, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

    //test happy path for RESTIoService.getIoAssociations(...)
    public void testGetIoAssociationOK() throws Exception {
        RESTIoService restService = new RESTIoService();
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        List<TaskRef> tasks = new ArrayList<TaskRef>();
        tasks.add(new TaskRef());
        EasyMock.expect(taskService.getTasksByName(
                EasyMock.eq("somePackage"), EasyMock.eq("someProcess"), EasyMock.eq("someTask"))).andReturn(tasks).once();
        restService.setTaskService(taskService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        EasyMock.replay(taskService, context);
        Response resp = restService.getIoAssociation("somePackage", "someProcess", "someTask", context);
        EasyMock.verify(taskService, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        assertTrue("entity should be of type ListTasksDTO", entity instanceof ListTasksDTO);
        ListTasksDTO dto = (ListTasksDTO) entity;
        List<TaskRefDTO> dtoTasks = dto.getTask();
        assertNotNull("dtoTasks shouldn't be null", dtoTasks);
        assertEquals("dtoTasks should have " + tasks.size() + " elements but it has " + dtoTasks.size(), tasks.size(), dtoTasks.size());
    }
    
    //test response to a TaskServiceException for RESTIoService.getIoAssociation(...)
    public void testGetIoAssociationServiceProblem() throws Exception {
        RESTIoService restService = new RESTIoService();
        TaskDefinitionService taskService = EasyMock.createMock(TaskDefinitionService.class);
        TaskServiceException exception = new TaskServiceException("Something going wrong");
        EasyMock.expect(taskService.getTasksByName(
                EasyMock.eq("somePackage"), EasyMock.eq("someProcess"), EasyMock.eq("someTask"))).andThrow(exception).once();
        restService.setTaskService(taskService);
        ServletContext context = EasyMock.createMock(ServletContext.class);
        
        EasyMock.replay(taskService, context);
        Response resp = restService.getIoAssociation("somePackage", "someProcess", "someTask", context);
        EasyMock.verify(taskService, context);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
}
