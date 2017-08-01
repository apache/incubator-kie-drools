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

package org.jbpm.services.task.assignment;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.assignment.impl.strategy.PotentialOwnerBusynessAssignmentStrategy;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.InternalTaskService;



public class PotentialOwnerBusynessAssignmentTest extends AbstractAssignmentTest {

	private PoolingDataSource pds;
	private EntityManagerFactory emf;
	
	
	
	@Before
	public void setup() {
	    System.setProperty("org.jbpm.task.assignment.enabled", "true");
		pds = setupPoolingDataSource();
		emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );
		
		AssignmentServiceProvider.override(new PotentialOwnerBusynessAssignmentStrategy());
		
		this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
												.entityManagerFactory(emf)
												.getTaskService();
	
	}
	
	@After
	public void clean() {
	    System.clearProperty("org.jbpm.task.assignment.enabled");
	    AssignmentServiceProvider.clear();
		if (emf != null) {
			emf.close();
		}
		if (pds != null) {
			pds.close();
		}		
	}
	
    @Test
    public void testAssignmentBasedOnBussynessManyPotentialOwners() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";
        
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");        
        // another task
        createAndAssertTask(str, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
        // yet another task
        createAndAssertTask(str, "Luke Cage", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
        // and we are back...
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");
    }
    
    @Test
    public void testAssignmentBasedWithEventListenerUpdates() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";
        
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");        
        // another task
        createAndAssertTask(str, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
        // yet another task
        createAndAssertTask(str, "Luke Cage", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
        
        // now Darth Vader completes his task
        List<TaskSummary> tasks = taskService.getTasksOwned("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "Darth Vader");
        taskService.complete(taskId, "Darth Vader", null);        
        
        // since Darth Vader has no more currently assigned tasks he should get next one
        createAndAssertTask(str, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage");
        
        // then Bobba Fet decides to release his task
        tasks = taskService.getTasksOwned("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        
        taskId = tasks.get(0).getId();
        taskService.release(taskId, "Bobba Fet");
        
        // that means that next task created will be directly assigned to Bobba Fet
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
    }
    
    @Test
    public void testAssignmentBasedOnBussynessNoPotentialOwners() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        assertPotentialOwners(task, 0);
       
        taskService.addTask(task, new HashMap<String, Object>());
        assertPotentialOwners(task, 0);
        assertNoActualOwner(task);
    }

    
    @Test
    public void testAssignmentBasedOnBussynessDifferentTasks() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";
        
        String str2 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str2 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Luke Cage'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ],}),";
        str2 += "name =  'This is my task name' })";
        
        createAndAssertTask(str, "Bobba Fet", 2, "Bobba Fet", "Darth Vader");        
        // another task
        createAndAssertTask(str, "Darth Vader", 2, "Bobba Fet", "Darth Vader");         
        // and we are back...
        createAndAssertTask(str, "Bobba Fet", 2, "Bobba Fet", "Darth Vader");
        
        // now created another task - different definition (and different users)
        createAndAssertTask(str2, "Luke Cage", 2, "Luke Cage", "Darth Vader");
        createAndAssertTask(str2, "Darth Vader", 2, "Luke Cage", "Darth Vader");
        
        // and now again task def one ...
        createAndAssertTask(str, "Bobba Fet", 2, "Bobba Fet", "Darth Vader");
        createAndAssertTask(str, "Darth Vader", 2, "Bobba Fet", "Darth Vader");
        
        createAndAssertTask(str2, "Luke Cage", 2, "Luke Cage", "Darth Vader");
        createAndAssertTask(str2, "Luke Cage", 2, "Luke Cage", "Darth Vader");
    }
    
    @Test
    public void testAssignmentBasedOnBussynessGroupPotentialOwners() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders') ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";
        
        createAndAssertTask(str, "Bobba Fet", 1, "Crusaders");        
        // yet another task
        createAndAssertTask(str, "Luke Cage", 1, "Crusaders");
        // and one more...
        createAndAssertTask(str, "Tony Stark", 1, "Crusaders");
        // and we are back...
        createAndAssertTask(str, "Bobba Fet", 1, "Crusaders");
    }
    
    @Test
    public void testAssignmentBasedOnBussynessUserAndGroupPotentialOwners() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Luke Cage'), new User('Darth Vader'), new Group('Crusaders') ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";
        
        createAndAssertTask(str, "Bobba Fet", 3, "Darth Vader", "Luke Cage", "Crusaders");  
        createAndAssertTask(str, "Darth Vader", 3, "Darth Vader", "Luke Cage", "Crusaders");
        createAndAssertTask(str, "Luke Cage", 3, "Darth Vader", "Luke Cage", "Crusaders"); 

        // and one for Tony since he is also a crusader
        createAndAssertTask(str, "Tony Stark", 3, "Darth Vader", "Luke Cage", "Crusaders");  

        createAndAssertTask(str, "Bobba Fet", 3, "Darth Vader", "Luke Cage", "Crusaders");  
        createAndAssertTask(str, "Darth Vader", 3, "Darth Vader", "Luke Cage", "Crusaders");
        createAndAssertTask(str, "Luke Cage", 3, "Darth Vader", "Luke Cage", "Crusaders");
              
    }
    
    @Test
    public void testAssignmentAfterRelease() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";
        
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");        
        // another task
        createAndAssertTask(str, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
        
        // now Darth Vader completes his task
        List<TaskSummary> tasks = taskService.getTasksOwned("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        long taskId = tasks.get(0).getId();
        
        // Luke should not have any tasks
        tasks = taskService.getTasksOwned("Luke Cage", "en-UK");
        assertEquals(0, tasks.size());        
        // Darth Vader releases the task so Luke should get it
        taskService.release(taskId, "Darth Vader");
        
        tasks = taskService.getTasksOwned("Luke Cage", "en-UK");
        assertEquals(1, tasks.size());  
         
    }

    @Test
    public void testAssignmentBasedOnBussynessWithExcludedOwners() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')  ],excludedOwners = [new User('Luke Cage')  ],businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";
        
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");        
        // another task
        createAndAssertTask(str, "Darth Vader", 3, "Bobba Fet", "Darth Vader", "Luke Cage"); 
        // and we are back...
        createAndAssertTask(str, "Bobba Fet", 3, "Bobba Fet", "Darth Vader", "Luke Cage");
    }
}
