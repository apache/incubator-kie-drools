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

package org.jbpm.services.ejb.test;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.services.ejb.api.UserTaskServiceEJBLocal;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.services.task.commands.GetTaskCommand;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.UpdateStringCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

@RunWith(Arquillian.class)
public class UserTaskServiceEJBIntegrationTest extends AbstractTestSupport {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, new File("target/sample-war-ejb-app.war"));
		war.addPackage("org.jbpm.services.ejb.test"); // test cases

		// deploy test kjar
		deployKjar();
		
		return war;
	}
	
	private Long processInstanceId = null;
    private KModuleDeploymentUnit deploymentUnit = null;
    
    @Before
    public void prepare() {
    	assertNotNull(deploymentService);
        
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);
    }
	
	protected static void deployKjar() {
		KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("processes/EmptyHumanTask.bpmn");
        processes.add("processes/humanTask.bpmn");
        
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
        repository.installArtifact(releaseId, kJar1, pom);
	}
	
	private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
	
    @After
    public void cleanup() {
    	if (processInstanceId != null) {
	    	// let's abort process instance to leave the system in clear state
	    	processService.abortProcessInstance(processInstanceId);
	    	
	    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);    	
	    	assertNull(pi);
    	}
    	int deleted = 0;
        deleted += commandService.execute(new UpdateStringCommand("delete from  NodeInstanceLog nid"));
        deleted += commandService.execute(new UpdateStringCommand("delete from  ProcessInstanceLog pid"));        
        deleted += commandService.execute(new UpdateStringCommand("delete from  VariableInstanceLog vsd"));
        deleted += commandService.execute(new UpdateStringCommand("delete from  AuditTaskImpl vsd"));
        System.out.println("Deleted " + deleted);
    	cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }
	
    @EJB
    private UserTaskServiceEJBLocal userTaskService;
    
	@EJB
	private DeploymentServiceEJBLocal deploymentService;
	
	@EJB
	private ProcessServiceEJBLocal processService;
	
	@EJB
	private RuntimeDataServiceEJBLocal runtimeDataService;
	
	@EJB(beanInterface=TransactionalCommandServiceEJBImpl.class)
	private TransactionalCommandService commandService;
	
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
    	assertEquals(0, (int)task.getPriority());
    	    	
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
    	assertEquals(5, input.size());
    	assertTrue(input.containsKey("ActorId"));
    	assertTrue(input.containsKey("Comment"));
    	assertTrue(input.containsKey("TaskName"));
    	assertTrue(input.containsKey("NodeName"));
    	assertTrue(input.containsKey("Priority"));
    	
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
}
