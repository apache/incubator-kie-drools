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
package org.jbpm.services.task.audit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.MvelFilePath;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.services.task.impl.TaskDeadlinesServiceImpl;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.jbpm.test.listener.task.CountDownTaskEventListener;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.model.InternalTask;

public class LocalTaskAuditWithDeadlineTest extends HumanTaskServicesBaseTest {

	private PoolingDataSource pds;
	private EntityManagerFactory emf;
	
	protected TaskAuditService taskAuditService;
	
	@Before
	public void setup() {
	    TaskDeadlinesServiceImpl.reset();
	    pds = setupPoolingDataSource();
		emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );

		this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
												.entityManagerFactory(emf)
												.listener(new JPATaskLifeCycleEventListener(true))
												.listener(new BAMTaskEventListener(true))
												.getTaskService();
                
        this.taskAuditService = TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService();
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
	  
    @Test(timeout=10000)
    public void testDelayedReassignmentOnDeadline() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, true, false);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
    
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithReassignment));
        Task task = (InternalTask) TaskFactory.evalTask(reader, vars);

        Map<String, Object> inputVars = new HashMap<String, Object>();
        inputVars.put("NotCompletedReassign", "[users:Tony Stark,Bobba Fet,Jabba Hutt|groups:]@[500ms]");
        taskService.addTask(task, inputVars);
        long taskId = task.getId();

        taskService.claim(taskId, "Tony Stark");
    
        task = taskService.getTaskById(taskId);
        List<OrganizationalEntity> potentialOwners = (List<OrganizationalEntity>) task.getPeopleAssignments().getPotentialOwners();
        List<String> ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains("Tony Stark"));
        assertTrue(ids.contains("Luke Cage"));
        
        List<AuditTask> tasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        assertEquals(1, tasks.size());
        
        AuditTask auditTask = tasks.get(0);
        assertEquals(Status.Reserved.toString(), auditTask.getStatus());
        assertEquals("Tony Stark", auditTask.getActualOwner());
    
        // should have re-assigned by now
        countDownListener.waitTillCompleted();
        
        task = taskService.getTaskById(taskId);
        assertNull(task.getTaskData().getActualOwner());
        assertEquals(Status.Ready, task.getTaskData().getStatus());
        potentialOwners = (List<OrganizationalEntity>) task.getPeopleAssignments().getPotentialOwners();
    
        ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains("Bobba Fet"));
        assertTrue(ids.contains("Jabba Hutt"));
        
        tasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        assertEquals(1, tasks.size());
        
        auditTask = tasks.get(0);
        assertEquals(Status.Ready.toString(), auditTask.getStatus());
        assertEquals("", auditTask.getActualOwner());
    
    }
}
