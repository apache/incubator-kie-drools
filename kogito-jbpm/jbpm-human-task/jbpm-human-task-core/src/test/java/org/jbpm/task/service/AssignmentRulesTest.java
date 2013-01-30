package org.jbpm.task.service;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.task.BaseTest;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.service.test.impl.AssignmentService;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.junit.Test;
import org.kie.KieBase;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;

public class AssignmentRulesTest extends BaseTest {
    
    protected TaskServer server;
    protected TaskService client;
    
    protected EntityManagerFactory createEntityManagerFactory() { 
        return Persistence.createEntityManagerFactory("org.jbpm.task.local");
    }
    
    @Override
    protected void setUp() throws Exception {
        setupJTADataSource(); 
        super.setUp();
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder.add(ResourceFactory.newClassPathResource("task-assignment-rules.drl"), ResourceType.DRL);
        Map<String, KnowledgeBase> kbases = new HashMap<String, KnowledgeBase>();
        kbases.put("addTask", kbuilder.newKnowledgeBase());
        
        taskService.setKieBases(kbases);
        
        Map<String, Object> globals = new HashMap<String, Object>();
        globals.put("assignmentService", new AssignmentService());
        Map<String, Map<String, Object>> addTaskGlobals = new HashMap<String, Map<String,Object>>();
        addTaskGlobals.put("addTask",  globals);
        taskService.setGlobals(addTaskGlobals);
        client = new LocalTaskService(taskService);
    }

    protected void tearDown() throws Exception {
        client.disconnect();
        if( server != null ) { 
            server.stop();
        }
        super.tearDown();
    }
    
    @Test
    public void testCreateTaskWithExcludedActorByRule() {
        Map<String, Object> vars = fillVariables();

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['john']], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null);
        long taskId = task.getId();
        
        assertEquals(1, taskId);
        List<TaskSummary> tasks = client.getTasksOwned("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testCreateTaskWithAutoAssignActorByRule() {
        Map<String, Object> vars = fillVariables();

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['john'], users['mary'],users['krisv']], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null);
        long taskId = task.getId();
        
        assertEquals(1, taskId);
        
        List<TaskSummary> tasks = client.getTasksOwned("mary", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        assertEquals("mary", tasks.get(0).getActualOwner().getId());
        assertEquals(Status.Reserved, tasks.get(0).getStatus());
    }

    @Test
    public void testCreateTaskWithDisallowedCreationByRule() {
        Map<String, Object> vars = fillVariables();

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['peter']], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) eval(new StringReader(str), vars);
        try {
            client.addTask(task, null);
            fail("Task should not be created due to rule violation");
        } catch (CannotAddTaskException e) {
            assertTrue(e.getMessage().indexOf("peter does not work here any more") != -1);
        }
        
    }
    
    @Test
    public void testCreateTaskWithDisallowedCreationBasedOnContentByRule() {
        Map<String, Object> vars = fillVariables();

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['peter']], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) eval(new StringReader(str), vars);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("manager", "John");
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);

        try {
            client.addTask(task, marshalledObject);
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
        
        UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(userGroups));
        
        Map<String, Object> vars = fillVariables();

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [], businessAdministrators = [ new User('Administrator') ], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null);
        long taskId = task.getId();
        
        assertEquals(1, taskId);
        
        List<TaskSummary> tasks = client.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        
        assertEquals(Status.Ready, tasks.get(0).getStatus());
        UserGroupCallbackManager.resetCallback();
    }
}
