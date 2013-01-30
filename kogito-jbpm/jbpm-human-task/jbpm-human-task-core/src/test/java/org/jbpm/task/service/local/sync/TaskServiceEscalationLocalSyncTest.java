/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.task.service.local.sync;

import java.util.List;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.jbpm.task.Task;
import org.jbpm.task.service.base.sync.TaskServiceEscalationBaseSyncTest;
import org.jbpm.task.service.local.LocalTaskService;

public class TaskServiceEscalationLocalSyncTest extends TaskServiceEscalationBaseSyncTest {

    protected EntityManagerFactory createEntityManagerFactory() { 
        return Persistence.createEntityManagerFactory("org.jbpm.task.local");
    }
    
    @Override
    protected void setUp() throws Exception {
        setupJTADataSource();
		super.setUp();
		
		client = new LocalTaskService(taskService);
	}

    @Override
    protected void persist(List<Task> tasks, EntityManager em) throws Exception {
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        em.joinTransaction();

        for (Task task : tasks) {
            // for this one we put the task in directly;
            em.persist(task);
        }
        ut.commit();
    }
	
	

}
