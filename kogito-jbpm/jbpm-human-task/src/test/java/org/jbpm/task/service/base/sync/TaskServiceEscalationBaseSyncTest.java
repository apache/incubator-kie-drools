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
package org.jbpm.task.service.base.sync;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.MockEscalatedDeadlineHandler;
import org.jbpm.task.service.MvelFilePath;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.MockEscalatedDeadlineHandler.Item;


public abstract class TaskServiceEscalationBaseSyncTest extends BaseTest {

    protected TaskServer server;
    protected TaskService client;

    public void testDummy() {
        assertTrue(true);
    }

    public void testUnescalatedDeadlines() throws Exception {
        Map vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);

        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
        taskService.setEscalatedDeadlineHandler(handler);

        //Reader reader;
        Reader reader = new InputStreamReader(TaskServiceEscalationBaseSyncTest.class.getResourceAsStream(MvelFilePath.UnescalatedDeadlines));
        List<Task> tasks = (List<Task>) eval(reader,
                vars);
        long now = ((Date) vars.get("now")).getTime();

        for (Task task : tasks) {
            client.addTask(task, null);
        }

        int sleep = 8000;
        handler.wait(3, sleep);
        
        assertEquals(3, handler.getList().size());

        boolean firstDeadlineMet = false;
        boolean secondDeadlineMet = false;
        boolean thirdDeadlineMet = false;
        for( Item item : handler.getList() ) { 
            long deadlineTime = item.getDeadline().getDate().getTime();
            if( deadlineTime == now + 2000 ) { 
                firstDeadlineMet = true;
            }
            else if( deadlineTime == now + 4000 ) { 
                secondDeadlineMet = true;
            }
            else if( deadlineTime == now + 6000 ) { 
                thirdDeadlineMet = true;
            }
            else { 
                fail( deadlineTime + " is not an expected deadline time." );
            }
        }
        
        assertTrue( "First deadline was not met." , firstDeadlineMet );
        assertTrue( "Second deadline was not met." , secondDeadlineMet );
        assertTrue( "Third deadline was not met." , thirdDeadlineMet );     
    }

    public void testUnescalatedDeadlinesOnStartup() throws Exception {
        Map vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);

        //Reader reader;
        Reader reader = new InputStreamReader(TaskServiceEscalationBaseSyncTest.class.getResourceAsStream(MvelFilePath.UnescalatedDeadlines));
        List<Task> tasks = (List<Task>) eval(reader,
                vars);
        long now = ((Date) vars.get("now")).getTime();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        for (Task task : tasks) {
            // for this one we put the task in directly;
            em.persist(task);
        }
        em.getTransaction().commit();

        // now create a new service, to see if it initiates from the DB correctly
        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
        org.jbpm.task.service.TaskService local = new org.jbpm.task.service.TaskService(emf, SystemEventListenerFactory.getSystemEventListener(), handler);

        int sleep = 8000;
        handler.wait(3, sleep);

        assertEquals(3, handler.getList().size());

        boolean firstDeadlineMet = false;
        boolean secondDeadlineMet = false;
        boolean thirdDeadlineMet = false;
        for( Item item : handler.getList() ) { 
            long deadlineTime = item.getDeadline().getDate().getTime();
            if( deadlineTime == now + 2000 ) { 
                firstDeadlineMet = true;
            }
            else if( deadlineTime == now + 4000 ) { 
                secondDeadlineMet = true;
            }
            else if( deadlineTime == now + 6000 ) { 
                thirdDeadlineMet = true;
            }
            else { 
                fail( deadlineTime + " is not an expected deadline time." );
            }
        }
        
        assertTrue( "First deadline was not met." , firstDeadlineMet );
        assertTrue( "Second deadline was not met." , secondDeadlineMet );
        assertTrue( "Third deadline was not met." , thirdDeadlineMet );     
 
    }

}
