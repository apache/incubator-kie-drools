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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Deadline;
import org.jbpm.task.Task;
import org.jbpm.task.service.EscalatedDeadlineHandler;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.base.sync.TaskServiceEscalationBaseSyncTest.MockEscalatedDeadlineHandler.Item;


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
        Reader reader = new InputStreamReader(TaskServiceEscalationBaseSyncTest.class.getResourceAsStream("../../../QueryData_UnescalatedDeadlines.mvel"));
        List<Task> tasks = (List<Task>) eval(reader,
                vars);
        long now = ((Date) vars.get("now")).getTime();

        for (Task task : tasks) {
            
            client.addTask(task, null);
            
        }

        handler.wait(3, 30000);

        assertEquals(3, handler.list.size());

        Item item0 = handler.list.get(0);
        assertEquals(now + 20000,
                item0.getDeadline().getDate().getTime());

        Item item1 = handler.list.get(1);
        assertEquals(now + 22000,
                item1.getDeadline().getDate().getTime());

        Item item2 = handler.list.get(2);
        assertEquals(now + 24000,
                item2.getDeadline().getDate().getTime());
    }

    public void testUnescalatedDeadlinesOnStartup() throws Exception {
        Map vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);

        //Reader reader;
        Reader reader = new InputStreamReader(TaskServiceEscalationBaseSyncTest.class.getResourceAsStream("../../../QueryData_UnescalatedDeadlines.mvel"));
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

        handler.wait(3, 30000);

        assertEquals(3, handler.list.size());

        Item item0 = handler.list.get(0);
        assertEquals(item0.getDeadline().getDate().getTime(),
                now + 20000);

        Item item1 = handler.list.get(1);
        assertEquals(item1.getDeadline().getDate().getTime(),
                now + 22000);

        Item item2 = handler.list.get(2);
        assertEquals(item2.getDeadline().getDate().getTime(),
                now + 24000);
    }

    public static class MockEscalatedDeadlineHandler
            implements
            EscalatedDeadlineHandler {

        List<Item> list = new ArrayList<Item>();
        org.jbpm.task.service.TaskService taskService;

        public void executeEscalatedDeadline(Task task,
                Deadline deadline,
                EntityManager em,
                org.jbpm.task.service.TaskService taskService) {
            list.add(new Item(task,
                    deadline,
                    em,
                    taskService));
        }

        public List<Item> getList() {
            return this.list;
        }

       
        public static class Item {

            Task task;
            Deadline deadline;
            EntityManager em;

            public Item(Task task,
                    Deadline deadline,
                    EntityManager em,
                    org.jbpm.task.service.TaskService taskService) {
                this.deadline = deadline;
                this.em = em;
                this.task = task;
            }

            public Task getTask() {
                return task;
            }

            public void setTask(Task task) {
                this.task = task;
            }

            public Deadline getDeadline() {
                return deadline;
            }

            public void setDeadline(Deadline deadline) {
                this.deadline = deadline;
            }

            public EntityManager getEntityManager() {
                return em;
            }

            public void setEntityManager(EntityManager em) {
                this.em = em;
            }

            public EntityManager getEm() {
                return em;
            }

            public void setEm(EntityManager em) {
                this.em = em;
            }
        }

        public synchronized void wait(int totalSize, int totalWait) {
            int wait = 0;
            int size = 0;

            while (true) {
                synchronized (list) {
                    size = list.size();
                }

                if (size >= totalSize || wait >= totalWait) {
                    break;
                }

                try {
                    Thread.sleep(250);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to sleep", e);
                }
                wait += 250;
            }
        }
    }
}
