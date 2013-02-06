package org.jbpm.task;



import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jbpm.task.impl.factories.TaskFactory;
import org.jbpm.task.query.TaskSummary;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class TaskQueryServiceBaseTest extends BaseTest {
    
    // getTasksAssignedAsPotentialOwner(String userId, String language)
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(tasks.size(), 0);
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(tasks.size(), 1);
        assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
    }

    // getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language)
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithNoTaskNoGroupIds() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, "en-UK");
        assertEquals(tasks.size(), 0);
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithOneTaskNoGroupIds() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, "en-UK");
        assertEquals(tasks.size(), 1);
        assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
    }
    
    // TODO requires fix
    /* 
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithOneTaskOneGroupId() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Darth Vader'), new Group('Crusaders'), businessAdministrators = [ new User('Administrator') ],   ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", groupIds, "en-UK");
        assertEquals(tasks.size(), 1);
        assertEquals("Darth Vader", tasks.get(0).getActualOwner().getId());
    }
    */
    
    // getTasksAssignedByGroup(String groupId, String language)
    
    @Test
    public void testGetTasksAssignedByGroupWithNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroup("Crusaders", "en-UK");
        assertEquals(tasks.size(), 0);
    }
    
    @Test
    public void testGetTasksAssignedByGroupWithOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders')  ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroup("Crusaders", "en-UK");
        assertEquals(tasks.size(), 1);
    }
}
