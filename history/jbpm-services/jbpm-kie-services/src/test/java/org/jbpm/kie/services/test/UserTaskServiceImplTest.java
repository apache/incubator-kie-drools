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

package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.StringUtils;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.objects.Image;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.TaskNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.task.commands.GetTaskCommand;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTaskServiceImplTest extends AbstractKieServicesBaseTest {

private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);   
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private Long processInstanceId = null;
    private KModuleDeploymentUnit deploymentUnit = null;
       
    @Before
    public void prepare() {
    	configureServices();
    	logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/EmptyHumanTask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/NoFormNameHumanTask.bpmn");
        processes.add("repo/processes/general/BPMN2-UserTaskImageVar.bpmn2");
        
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
            
        }
		KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
        
        assertNotNull(deploymentService);
        
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);

    }
    
    @After
    public void cleanup() {
    	if (processInstanceId != null) {
    		try {
		    	// let's abort process instance to leave the system in clear state
		    	processService.abortProcessInstance(processInstanceId);
		    	
		    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);    	
		    	assertNull(pi);
    		} catch (ProcessInstanceNotFoundException e) {
    			// ignore it as it might already be completed/aborted
    		}
    	}
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
            	try {
                deploymentService.undeploy(unit);
            	} catch (Exception e) {
            		// do nothing in case of some failed tests to avoid next test to fail as well
            	}
            }
            units.clear();
        }
        close();
    }
    
    @Test
    public void testActivate() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument.empty");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	userTaskService.activate(taskId, "Administrator");
    	
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Ready.toString(), task.getStatus());
    }
    
    @Test
    public void testReleaseAndClaim() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	userTaskService.release(taskId, "salaboy");
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Ready.toString(), task.getStatus());
    	
    	userTaskService.claim(taskId, "salaboy");
    	task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    }
    
    @Test
    public void testStartAndComplete() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
        
        Long taskId = taskIds.get(0);
        
        userTaskService.start(taskId, "salaboy");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals(Status.InProgress.toString(), task.getStatus());
        
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "some document data");
        userTaskService.complete(taskId, "salaboy", results);
        task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals(Status.Completed.toString(), task.getStatus());
    }
    
    @Test
    public void testCompleteAutoProgress() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
        
        Long taskId = taskIds.get(0);
        userTaskService.release(taskId, "salaboy");
        
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "some document data");
        // claim, start, and complete the task
        userTaskService.completeAutoProgress(taskId, "salaboy", results);
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals(Status.Completed.toString(), task.getStatus());
    }
    
    @Test
    public void testDelegate() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	userTaskService.delegate(taskId, "Administrator", "john");
    	
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    	assertEquals("john", task.getActualOwner());
    }
    
    @Test
    public void testExit() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	userTaskService.exit(taskId, "Administrator");
    	
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Exited.toString(), task.getStatus());
    }
    
    @Test
    public void testStartAndFail() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	userTaskService.start(taskId, "salaboy");
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.InProgress.toString(), task.getStatus());
    	
    	userTaskService.fail(taskId, "Administrator", new HashMap<String, Object>());
    	
    	task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Failed.toString(), task.getStatus());
    }
    
    @Test
    public void testStartAndForward() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	userTaskService.start(taskId, "salaboy");
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.InProgress.toString(), task.getStatus());
    	
    	userTaskService.forward(taskId, "salaboy", "john");
    	
    	task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Ready.toString(), task.getStatus());
    	assertEquals("", task.getActualOwner());
    }
    
    @Test
    public void testSuspendAndResume() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	userTaskService.suspend(taskId, "salaboy");
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Suspended.toString(), task.getStatus());
    	
    	userTaskService.resume(taskId, "salaboy");
    	task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    }
    
    @Test
    public void testStartAndStop() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	userTaskService.start(taskId, "salaboy");
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.InProgress.toString(), task.getStatus());
    	
    	userTaskService.stop(taskId, "salaboy");
    	task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    }
    
    @Test
    public void testSkip() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	userTaskService.skip(taskId, "Administrator");
    	
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Obsolete.toString(), task.getStatus());
    }
    
    @Test
    public void testNominate() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument.empty");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	List<OrganizationalEntity> owners = new ArrayList<OrganizationalEntity>();
    	User user = TaskModelProvider.getFactory().newUser("john");
    	owners.add(user);
    	user = TaskModelProvider.getFactory().newUser("salaboy");
    	owners.add(user);
    	Group group = TaskModelProvider.getFactory().newGroup("HR");
    	owners.add(group);
    	
    	userTaskService.nominate(taskId, "Administrator", owners);
    	
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Ready.toString(), task.getStatus());
    }
    
    @Test
    public void testSetPriority() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    	assertEquals(9, (int)task.getPriority());
    	    	
    	userTaskService.setPriority(taskId, 8);
    	
    	task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    	assertEquals(8, (int)task.getPriority());
    }
    
    @Test
    public void testSetExpirationDate() throws Exception {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    	Date origDueDate = task.getDueDate();
    	assertNull(origDueDate);
    	    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	
    	userTaskService.setExpirationDate(taskId, sdf.parse("2013-12-31"));
    	
    	task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    	assertEquals("2013-12-31", sdf.format(task.getDueDate()));
    }
    
    @Test
    public void testSetSkippable() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	Task taskInstance = userTaskService.getTask(taskId);
    	assertNotNull(taskInstance);
    	assertEquals(Status.Reserved, taskInstance.getTaskData().getStatus());
    	assertTrue(taskInstance.getTaskData().isSkipable());
    	    	
    	userTaskService.setSkipable(taskId, false);
    	
    	taskInstance = userTaskService.getTask(taskId);
    	assertNotNull(taskInstance);
    	assertEquals(Status.Reserved, taskInstance.getTaskData().getStatus());
    	assertFalse(taskInstance.getTaskData().isSkipable());
    }
    
    @Test
    public void testSetName() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    	assertEquals("Write a Document", task.getName());   	
    	userTaskService.setName(taskId, "updated");
    	
    	task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    	assertEquals("updated", task.getName());
    }
    
    @Test
    public void testSetDescription() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    	assertEquals("Write a Document", task.getDescription());   	
    	userTaskService.setDescription(taskId, "updated");
    	
    	task = runtimeDataService.getTaskById(taskId);
    	assertNotNull(task);
    	assertEquals(Status.Reserved.toString(), task.getStatus());
    	assertEquals("updated", task.getDescription());
    }
    
    @Test
    public void testContentRelatedOperations() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	Map<String, Object> input = userTaskService.getTaskInputContentByTaskId(taskId);
    	assertNotNull(input);
    	assertEquals(6, input.size());
    	assertTrue(input.containsKey("ActorId"));
    	assertTrue(input.containsKey("Comment"));
    	assertTrue(input.containsKey("TaskName"));
    	assertTrue(input.containsKey("NodeName"));
    	assertTrue(input.containsKey("Priority"));
    	assertTrue(input.containsKey("Skippable"));
    	
    	// now let's add some output data
    	Map<String, Object> values = new HashMap<String, Object>();
    	values.put("Content", "testing save");
    	values.put("Author", "john");
    	Long contentId = userTaskService.saveContent(taskId, values);
    	assertNotNull(contentId);
    	
    	// let's now validate it
    	Map<String, Object> output = userTaskService.getTaskOutputContentByTaskId(taskId);
    	assertNotNull(output);
    	assertEquals(2, output.size());
    	assertTrue(output.containsKey("Content"));
    	assertTrue(output.containsKey("Author"));
    	
    	// now we delete it
    	userTaskService.deleteContent(taskId, contentId);
    	// and confirm it was deleted
    	output = userTaskService.getTaskOutputContentByTaskId(taskId);
    	assertNotNull(output);
    	assertEquals(0, output.size());
    }
    
    @Test
    public void testCommentOperations() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	List<Comment> comments = userTaskService.getCommentsByTaskId(taskId);
    	assertNotNull(comments);
    	assertEquals(0, comments.size());
    	
    	Long commentId = userTaskService.addComment(taskId, "Simple comment", "john", new Date());
    	assertNotNull(commentId);
    	
    	Long commentId2 = userTaskService.addComment(taskId, "Another comment", "john", new Date());
    	assertNotNull(commentId2);
    	
    	comments = userTaskService.getCommentsByTaskId(taskId);
    	assertNotNull(comments);
    	assertEquals(2, comments.size());
    	
    	Comment cm = userTaskService.getCommentById(taskId, commentId2);
    	assertNotNull(cm);
    	assertEquals("john", cm.getAddedBy().getId());
    	assertEquals("Another comment", cm.getText());
    	
    	userTaskService.deleteComment(taskId, commentId2);
    	comments = userTaskService.getCommentsByTaskId(taskId);
    	assertNotNull(comments);
    	assertEquals(1, comments.size());
    	
    }
    
    @Test
    public void testAttachmentOperations() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	List<Attachment> attachments = userTaskService.getAttachmentsByTaskId(taskId);
    	assertNotNull(attachments);
    	assertEquals(0, attachments.size());
    	
    	Long attId = userTaskService.addAttachment(taskId, "john", "my attachment", "String attachment");
    	assertNotNull(attId);
    	
    	attachments = userTaskService.getAttachmentsByTaskId(taskId);
    	assertNotNull(attachments);
    	assertEquals(1, attachments.size());
    	
    	String content = (String) userTaskService.getAttachmentContentById(taskId, attId);
    	assertNotNull(content);
    	assertEquals("String attachment", content);
    	
    	Attachment attachment = userTaskService.getAttachmentById(taskId, attId);
    	assertNotNull(attachment);
    	assertEquals("john", attachment.getAttachedBy().getId());
    	assertEquals("my attachment", attachment.getName());
    	assertNotNull(attachment.getAttachmentContentId());
    	assertEquals("java.lang.String", attachment.getContentType());
    	
    	userTaskService.deleteAttachment(taskId, attId);
    	
    	attachments = userTaskService.getAttachmentsByTaskId(taskId);
    	assertNotNull(attachments);
    	assertEquals(0, attachments.size());
    }
    
    @Test
    public void testExecute() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	Task task = userTaskService.execute(deploymentUnit.getIdentifier(), new GetTaskCommand(taskId));
    	assertNotNull(task);
    	assertEquals(taskId, task.getId());
    	assertEquals("Write a Document", task.getName());
    }
    
    @Test
    public void testGetTask() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument.noform");
    	assertNotNull(processInstanceId);
    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());
    	
    	Long taskId = taskIds.get(0);
    	
    	Task taskInstance = userTaskService.getTask(taskId);
    	assertNotNull(taskInstance);
    	assertEquals(Status.Reserved, taskInstance.getTaskData().getStatus());
    	assertEquals("Write a Document", taskInstance.getName());
    	assertTrue(StringUtils.isEmpty(((InternalTask)taskInstance).getFormName()));
    }
    
    @Test
    public void testGetTaskOfAbortedProcess() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument.noform");
        assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
        
        Long taskId = taskIds.get(0);
        
        Task taskInstance = userTaskService.getTask(taskId);
        assertNotNull(taskInstance);
        assertEquals(Status.Reserved, taskInstance.getTaskData().getStatus());
        assertEquals("Write a Document", taskInstance.getName());
        assertTrue(StringUtils.isEmpty(((InternalTask)taskInstance).getFormName()));
        
        processService.abortProcessInstance(processInstanceId);
        
        taskInstance = userTaskService.getTask(taskId);
        assertNotNull(taskInstance);
        assertEquals(Status.Exited, taskInstance.getTaskData().getStatus());
        assertEquals("Write a Document", taskInstance.getName());
        assertTrue(StringUtils.isEmpty(((InternalTask)taskInstance).getFormName()));
      
    }
    
    @Test
    public void testUpdateTask() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
        
        Long taskId = taskIds.get(0);
        
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals(Status.Reserved.toString(), task.getStatus());
        assertEquals("Write a Document", task.getName());
        assertEquals("Write a Document", task.getDescription());
        assertEquals(9, task.getPriority().intValue()); 
        assertNull(task.getDueDate());
        
        
        ((org.jbpm.kie.services.impl.model.UserTaskInstanceDesc)task).setName("updated");
        ((org.jbpm.kie.services.impl.model.UserTaskInstanceDesc)task).setPriority(5);
        ((org.jbpm.kie.services.impl.model.UserTaskInstanceDesc)task).setDescription("my description");
        ((org.jbpm.kie.services.impl.model.UserTaskInstanceDesc)task).setFormName("BasicForm");
        ((org.jbpm.kie.services.impl.model.UserTaskInstanceDesc)task).setDueDate(new Date());
        
        userTaskService.updateTask(taskId, "salaboy", task, null, null);
        
        task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals(Status.Reserved.toString(), task.getStatus());
        assertEquals("updated", task.getName());
        assertEquals("my description", task.getDescription());
        assertEquals(5, task.getPriority().intValue()); 
        assertNotNull(task.getDueDate());
        
        Task updatedTask = userTaskService.getTask(taskId);
        assertEquals("BasicForm", updatedTask.getFormName());
    }
    
    @Test
    public void testUpdateTaskWithData() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
        
        Long taskId = taskIds.get(0);
        
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals(Status.Reserved.toString(), task.getStatus());
        assertEquals("Write a Document", task.getName());
        assertEquals(9, task.getPriority().intValue()); 
        
        Map<String, Object> inputs = userTaskService.getTaskInputContentByTaskId(taskId);
        assertEquals(6, inputs.size());
        Map<String, Object> outputs = userTaskService.getTaskOutputContentByTaskId(taskId);
        assertEquals(0, outputs.size());
        
        ((org.jbpm.kie.services.impl.model.UserTaskInstanceDesc)task).setName("updated");
        ((org.jbpm.kie.services.impl.model.UserTaskInstanceDesc)task).setPriority(5);
        
        Map<String, Object> updatedInputs = new HashMap<>();
        updatedInputs.put("new task input", "test");
        Map<String, Object> updatedOutputs = new HashMap<>();
        updatedOutputs.put("new task output", "reviewed");
        
        userTaskService.updateTask(taskId, "salaboy", task, updatedInputs, updatedOutputs);
        
        task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals(Status.Reserved.toString(), task.getStatus());
        assertEquals("updated", task.getName());
        assertEquals(5, task.getPriority().intValue()); 
        
        inputs = userTaskService.getTaskInputContentByTaskId(taskId);
        
        assertEquals("test", inputs.get("new task input"));
        outputs = userTaskService.getTaskOutputContentByTaskId(taskId);
        assertEquals("reviewed", outputs.get("new task output"));
    }
    
    @Test
    public void testUpdateTaskPermissionDenied() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
        
        Long taskId = taskIds.get(0);
        
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals("Write a Document", task.getName());
        
        try {
            ((org.jbpm.kie.services.impl.model.UserTaskInstanceDesc)task).setName("updated");
            
            userTaskService.updateTask(taskId, "john", task, null, null);
            fail("John is not admin nor potential owner");
        } catch (PermissionDeniedException e) {
            //expected
        }
        
        task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals(Status.Reserved.toString(), task.getStatus());
        assertEquals("Write a Document", task.getName());
        assertEquals(9, task.getPriority().intValue()); 
        
    }
    
    @Test
    public void testProcessWithLazyLoadedVariable() {
        
        Image newImage = new Image("test");
        Map<String, Object> params = new HashMap<>();
        params.put("image", newImage);
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "UserTaskImageVar", params);
        assertNotNull(processInstanceId);
        
        Image processVar = (Image) processService.getProcessInstanceVariable(processInstanceId, "image");
        assertImageVariable(processVar, "test");
        // make sure there is no NPE when accessing not exesting variable
        processVar = (Image) processService.getProcessInstanceVariable(processInstanceId, "imageNotExisting");
        assertNull(processVar);
        
        Map<String, Object> variables = processService.getProcessInstanceVariables(processInstanceId);
        assertEquals(1, variables.size());
        assertTrue(variables.containsKey("image"));
        assertImageVariable((Image) variables.get("image"), "test");
        
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
                
        Long taskId = taskIds.get(0);
        
        Map<String, Object> input = userTaskService.getTaskInputContentByTaskId(taskId);
        assertNotNull(input);
        assertEquals(3, input.size());
        assertTrue(input.containsKey("Image"));
        
        Image image = (Image) input.get("Image");
        assertImageVariable(image, "test");
        
        // now let's add some output data
        Map<String, Object> values = new HashMap<String, Object>();
        Image updatedImage = new Image("updated test");
        values.put("UpdatedImage", updatedImage);
        Long contentId = userTaskService.saveContent(taskId, values);
        assertNotNull(contentId);
        
        // let's now validate it
        Map<String, Object> output = userTaskService.getTaskOutputContentByTaskId(taskId);
        assertNotNull(output);
        assertEquals(1, output.size());
        assertTrue(output.containsKey("UpdatedImage"));
        
        image = (Image) output.get("UpdatedImage");
        assertImageVariable(image, "updated test");
       
    }

    @Test
    public void testTaskOperationWithUserOrWithIdentityProvider() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
    
        ProcessInstance instance = processService.getProcessInstance(processInstanceId);
        assertNotNull(instance);

        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
        
        Long taskId = taskIds.get(0);
    
        userTaskService.start(taskId, "salaboy");
    
        List<TaskEvent> auditTasks = runtimeDataService.getTaskEvents(taskId, new QueryFilter());
        assertNotNull(auditTasks);
        assertEquals(2, auditTasks.size());
        assertEquals(TaskEvent.TaskEventType.ADDED, auditTasks.get(0).getType());
        assertEquals("org.jbpm.writedocument", auditTasks.get(0).getUserId());
        assertEquals(TaskEvent.TaskEventType.STARTED, auditTasks.get(1).getType());
        assertEquals("salaboy", auditTasks.get(1).getUserId());
        
        identityProvider.setName("salaboy");
        userTaskService.stop(taskId, null);
        
        auditTasks = runtimeDataService.getTaskEvents(taskId, new QueryFilter());
        assertNotNull(auditTasks);
        assertEquals(3, auditTasks.size());
        assertEquals(TaskEvent.TaskEventType.ADDED, auditTasks.get(0).getType());
        assertEquals("org.jbpm.writedocument", auditTasks.get(0).getUserId());
        assertEquals(TaskEvent.TaskEventType.STARTED, auditTasks.get(1).getType());
        assertEquals("salaboy", auditTasks.get(1).getUserId());
        assertEquals(TaskEvent.TaskEventType.STOPPED, auditTasks.get(2).getType());
        assertEquals("salaboy", auditTasks.get(2).getUserId());
        
        Map<String, Object> params = new HashMap<>();
        params.put("test", "value");
        userTaskService.saveContent(taskId, params);
        
        auditTasks = runtimeDataService.getTaskEvents(taskId, new QueryFilter());
        assertNotNull(auditTasks);
        assertEquals(4, auditTasks.size());
        assertEquals(TaskEvent.TaskEventType.ADDED, auditTasks.get(0).getType());
        assertEquals("org.jbpm.writedocument", auditTasks.get(0).getUserId());
        assertEquals(TaskEvent.TaskEventType.STARTED, auditTasks.get(1).getType());
        assertEquals("salaboy", auditTasks.get(1).getUserId());
        assertEquals(TaskEvent.TaskEventType.STOPPED, auditTasks.get(2).getType());
        assertEquals("salaboy", auditTasks.get(2).getUserId());
        assertEquals(TaskEvent.TaskEventType.UPDATED, auditTasks.get(3).getType());
        assertEquals("salaboy", auditTasks.get(3).getUserId());
    
        identityProvider.setName("testUser");
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    
    }
    
    protected void assertImageVariable(Image processVar, String name) {
        assertNotNull(processVar);
        assertNotNull(processVar.getName());
        assertEquals(name, processVar.getName());
        assertNotNull(processVar.getContent());
        assertEquals(1, processVar.getContent().length);
    }

    @Test
    public void testCompleteAutoProgressWrongDeploymentId() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
        
        Long taskId = taskIds.get(0);
        userTaskService.release(taskId, "salaboy");
        
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "some document data");
        assertThatExceptionOfType(TaskNotFoundException.class).isThrownBy(() -> { 
            userTaskService.completeAutoProgress("wrong-one", taskId, "salaboy", results); })
        .withMessageContaining("Task with id " + taskId + " is not associated with wrong-one");
               
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        assertNotNull(task);
        assertEquals(Status.Ready.toString(), task.getStatus());
    }
}
