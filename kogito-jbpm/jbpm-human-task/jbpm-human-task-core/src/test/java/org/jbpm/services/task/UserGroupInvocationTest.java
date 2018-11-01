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

package org.jbpm.services.task;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class UserGroupInvocationTest extends HumanTaskServicesBaseTest {
	
	private static final Logger logger = LoggerFactory.getLogger(UserGroupInvocationTest.class);

	private PoolingDataSource pds;
	private EntityManagerFactory emf;
	protected CountInvokeUserGroupCallback callback;
	
	@Before
	public void setup() {
		pds = setupPoolingDataSource();
		emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );
		callback = new CountInvokeUserGroupCallback();
		
		this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
												.entityManagerFactory(emf)
												.userGroupCallback(callback)
												.getTaskService();
	}
	
	@After
	public void clean() {
		if (emf != null) {
			emf.close();
		}
		if (pds != null) {
			pds.close();
		}
	}
	
    @Test
    public void testAddStartCompleteUserAssignment() {

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";

        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        logger.debug("-------------------------");
        assertEquals(3, callback.getExistsUserCounter());
        assertEquals(0, callback.getExistsGroupCounter());
        assertEquals(0, callback.getGetGroupCounter());
        
        callback.reset();
        
        long taskId = task.getId();

        taskService.start(taskId, "Darth Vader");
        logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        logger.debug("-------------------------");
        assertEquals(1, callback.getExistsUserCounter());
        assertEquals(0, callback.getExistsGroupCounter());
        assertEquals(1, callback.getGetGroupCounter());
        
        callback.reset();
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        taskService.complete(taskId, "Darth Vader", null);
        logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        logger.debug("-------------------------");
        assertEquals(1, callback.getExistsUserCounter());
        assertEquals(0, callback.getExistsGroupCounter());
        assertEquals(1, callback.getGetGroupCounter());
        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }
    
    @Test
    public void testAddStartCompleteGroupAssignment() {

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Knights Templer'), new Group('Crusaders') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";

        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        logger.debug("-------------------------");
        assertEquals(1, callback.getExistsUserCounter());
        assertEquals(2, callback.getExistsGroupCounter());
        assertEquals(0, callback.getGetGroupCounter());
        
        callback.reset();
        
        long taskId = task.getId();
        
        taskService.claim(taskId, "Darth Vader");
        logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        logger.debug("-------------------------");
        assertEquals(1, callback.getExistsUserCounter());
        assertEquals(0, callback.getExistsGroupCounter());
        assertEquals(1, callback.getGetGroupCounter());
        
        callback.reset();

        taskService.start(taskId, "Darth Vader");
        logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        logger.debug("-------------------------");
        assertEquals(1, callback.getExistsUserCounter());
        assertEquals(0, callback.getExistsGroupCounter());
        assertEquals(1, callback.getGetGroupCounter());
        
        callback.reset();
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        taskService.complete(taskId, "Darth Vader", null);
        logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        logger.debug("-------------------------");
        assertEquals(1, callback.getExistsUserCounter());
        assertEquals(0, callback.getExistsGroupCounter());
        assertEquals(1, callback.getGetGroupCounter());
        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }
    
    protected class CountInvokeUserGroupCallback implements UserGroupCallback {

    	private int existsUserCounter = 0;
    	private int existsGroupCounter = 0;
    	private int getGroupCounter = 0;
    	
		@Override
		public boolean existsUser(String userId) {
			existsUserCounter++;
			return true;
		}

		@Override
		public boolean existsGroup(String groupId) {
			existsGroupCounter++;
			return true;
		}

		@Override
		public List<String> getGroupsForUser(String userId) {
			getGroupCounter++;
			List<String> groups = new ArrayList<String>();
			groups.add("Knights Templer");
			groups.add("Crusaders");
			return groups;
		}

		public int getExistsUserCounter() {
			return existsUserCounter;
		}

		public int getExistsGroupCounter() {
			return existsGroupCounter;
		}

		public int getGetGroupCounter() {
			return getGroupCounter;
		}
		
		public void reset() {
			this.existsUserCounter = 0;
			this.existsGroupCounter = 0;
			this.getGroupCounter = 0;
		}
    	
    }
}
