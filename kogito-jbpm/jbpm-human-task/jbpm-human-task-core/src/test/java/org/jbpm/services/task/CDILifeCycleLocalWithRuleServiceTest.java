/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.services.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.services.task.exception.CannotAddTaskException;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.internals.rule.AssignmentService;
import org.jbpm.services.task.rule.RuleContextProvider;
import org.jbpm.services.task.rule.TaskRuleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

@RunWith(Arquillian.class)
public class CDILifeCycleLocalWithRuleServiceTest extends HumanTaskServicesBaseTest {

    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "jbpm-human-task-cdi.jar")
                .addPackage("org.jboss.seam.persistence") //seam-persistence
                .addPackage("org.jboss.seam.transaction") //seam-persistence
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.jbpm.services.task")
                .addPackage("org.jbpm.services.task.annotations")
                .addPackage("org.jbpm.services.task.api")
                .addPackage("org.jbpm.services.task.impl")
                .addPackage("org.jbpm.services.task.events")
                .addPackage("org.jbpm.services.task.exception")
                .addPackage("org.jbpm.services.task.identity")
                .addPackage("org.jbpm.services.task.factories")
                .addPackage("org.jbpm.services.task.internals")
                .addPackage("org.jbpm.services.task.internals.lifecycle")
                .addPackage("org.jbpm.services.task.lifecycle.listeners")
                .addPackage("org.jbpm.services.task.query")
                .addPackage("org.jbpm.services.task.util")
                .addPackage("org.jbpm.services.task.deadlines") // deadlines
                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.services.task.subtask")
                .addPackage("org.jbpm.services.task.rule")
                .addPackage("org.jbpm.services.task.rule.impl")
                //.addPackage("org.jbpm.services.task.commands") // This should not be required here 
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
                .addAsResource("simple-add-task-rules.drl", "default-add-task.drl")
                .addAsResource("simple-complete-task-rules.drl", "default-complete-task.drl");

    }

    @Inject
    private RuleContextProvider ruleContextProvider;
    
    @Before
    public void configure() {
        Map<String, Object> addTaskGlobals = ruleContextProvider.getGlobals(TaskRuleService.ADD_TASK_SCOPE);
        if (addTaskGlobals == null) {
            addTaskGlobals = new HashMap<String, Object>();
            addTaskGlobals.put("assignmentService", new AssignmentService());
            ruleContextProvider.addGlobals(TaskRuleService.ADD_TASK_SCOPE, addTaskGlobals);
        }
        
    }
    
    @Test
    public void testCreateTaskWithExcludedActorByRule() {
     

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('john')], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        List<TaskSummary> tasks = taskService.getTasksOwned("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testCreateTaskWithAutoAssignActorByRule() {
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('john'), new User('mary'),new User('krisv')], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        List<TaskSummary> tasks = taskService.getTasksOwned("mary", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        assertEquals("mary", tasks.get(0).getActualOwner().getId());
        assertEquals(Status.Reserved, tasks.get(0).getStatus());
    }

    @Test
    public void testCreateTaskWithDisallowedCreationByRule() {
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('peter')], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        try {
            taskService.addTask(task, new HashMap<String, Object>());
        
            fail("Task should not be created due to rule violation");
        } catch (CannotAddTaskException e) {
            assertTrue(e.getMessage().indexOf("peter does not work here any more") != -1);
        }
        
    }
    
    @Test
    public void testCreateTaskWithDisallowedCreationBasedOnContentByRule() {
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('john')], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager", "John");
        

        try {
            taskService.addTask(task, params);
            
            fail("Task should not be created due to rule violation");
        } catch (CannotAddTaskException e) {
            assertTrue(e.getMessage().indexOf("John (manager) does not work here any more") != -1);
        }
        
    }
    
    @Test
    public void testCreateTaskWithAssignByServiceByRule() {
        Properties userGroups = new Properties();
        userGroups.setProperty("john", "Crusaders");
        userGroups.setProperty("Administrator", "BA");
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [], businessAdministrators = [ new User('Administrator') ], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());        
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        
        assertEquals(Status.Ready, tasks.get(0).getStatus());
    }

    @Test
    public void testCompleteTaskWithCheckByRule() {
     

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('mary')], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        List<TaskSummary> tasks = taskService.getTasksOwned("mary", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "mary");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("approved", "false");
        
        try {
            taskService.complete(taskId, "mary", data);
            
            fail("Task should not be created due to rule violation");
        } catch (PermissionDeniedException e) {
            assertTrue(e.getMessage().indexOf("Mary is not allowed to complete task with approved false") != -1);
        }
    }
}
