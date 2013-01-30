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

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.task.TaskService;
import org.jbpm.task.service.ClaimTaskTwiceTest;
import org.jbpm.task.service.local.LocalTaskService;

/**
 * Thanks to jbride for development of the test.
 * 
 */
public class ClaimTaskTwiceLocalSyncTest extends ClaimTaskTwiceTest {
    protected EntityManagerFactory createEntityManagerFactory() { 
        return Persistence.createEntityManagerFactory("org.jbpm.task.local");
    }
    
    @Override
    protected void setUp() throws Exception {
       setupJTADataSource();
       super.setUp();
    }

    protected TaskService createClient(String clientName) throws Exception { 
        TaskService client = new LocalTaskService(taskService);
        return client;
    }
    
    
}
