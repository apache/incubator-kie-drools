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
package org.jbpm.task.service.base.async;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.MvelFilePath;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskClientHandler.TaskSummaryResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.utils.CollectionUtils;

public abstract class TaskServiceBaseAsyncTest extends BaseTest {

    protected TaskServer server;
    protected AsyncTaskService client;

    @SuppressWarnings("unchecked")
    public void testTasksOwnedQueryWithI18N() throws Exception {
        Map<String, Object> vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);

        //Reader reader;
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.TasksOwned));
        List<Task> tasks = (List<Task>) eval(reader,
                vars);
        for (Task task : tasks) {
            BlockingAddTaskResponseHandler responseHandler = new BlockingAddTaskResponseHandler();
            client.addTask(task, null, responseHandler);
        }

        // Test UK I18N  
        reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.TasksOwnedInEnglish));
        Map<String, List<TaskSummary>> expected = (Map<String, List<TaskSummary>>) eval(reader,
                vars);

        BlockingAllOpenTasksForUseResponseHandler responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned(users.get("peter").getId(),
                "en-UK",
                responseHandler);
        List<TaskSummary> actual = responseHandler.getResults();
        assertEquals(3,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("peter"),
                actual));

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned(users.get("steve").getId(),
                "en-UK",
                responseHandler);
        actual = responseHandler.getResults();
        assertEquals(2,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("steve"),
                actual));

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned(users.get("darth").getId(),
                "en-UK",
                responseHandler);
        actual = responseHandler.getResults();
        assertEquals(1,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("darth"),
                actual));

        // Test DK I18N 
        reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.TasksOwnedInGerman));
        expected = (Map<String, List<TaskSummary>>) eval(reader,
                vars);

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned(users.get("peter").getId(),
                "en-DK",
                responseHandler);
        actual = responseHandler.getResults();
        assertEquals(3,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("peter"),
                actual));

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned(users.get("steve").getId(),
                "en-DK",
                responseHandler);
        actual = responseHandler.getResults();
        assertEquals(2,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("steve"),
                actual));

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned(users.get("darth").getId(),
                "en-DK",
                responseHandler);
        actual = responseHandler.getResults();
        assertEquals(1,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("darth"),
                actual));
    }

    public void testPotentialOwnerQueries() {
        Map<String, Object> vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);

        //Reader reader;
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.TasksPotentialOwner));
        List<Task> tasks = (List<Task>) eval(reader,
                vars);
        for (Task task : tasks) {
            BlockingAddTaskResponseHandler responseHandler = new BlockingAddTaskResponseHandler();
            client.addTask(task, null, responseHandler);
        }

        // Test UK I18N  
        BlockingAllOpenTasksForUseResponseHandler responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(),
                "en-UK",
                responseHandler);
        List<TaskSummary> actual = responseHandler.getResults();
        assertEquals(2,
                actual.size());
    }

    public void testPeopleAssignmentQueries() {
        Map vars = new HashMap();
        vars.put("users",
                users);
        vars.put("groups",
                groups);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.TasksOwned));
        List<Task> tasks = (List<Task>) eval(reader,
                vars);
        for (Task task : tasks) {
            taskSession.addTask(task, null);
        }

        reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.PeopleAssignmentQuerries));
        Map<String, List<TaskSummary>> expected = (Map<String, List<TaskSummary>>) eval(reader,
                vars);

        BlockingAllOpenTasksForUseResponseHandler responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsTaskInitiator(users.get("darth").getId(),
                "en-UK",
                responseHandler);
        List<TaskSummary> actual = responseHandler.getResults();
        assertEquals(1,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("darth"),
                actual));

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsBusinessAdministrator(users.get("steve").getId(),
                "en-UK",
                responseHandler);
        actual = responseHandler.getResults();
        assertTrue(CollectionUtils.equals(expected.get("steve"),
                actual));

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsExcludedOwner(users.get("liz").getId(),
                "en-UK",
                responseHandler);
        actual = responseHandler.getResults();
        assertEquals(2,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("liz"),
                actual));

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(),
                "en-UK",
                responseHandler);
        actual = responseHandler.getResults();
        assertEquals(3,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("bobba"),
                actual));

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsRecipient(users.get("sly").getId(),
                "en-UK",
                responseHandler);
        actual = responseHandler.getResults();
        assertEquals(1,
                actual.size());
        assertTrue(CollectionUtils.equals(expected.get("sly"),
                actual));
    }

    public static class BlockingAllOpenTasksForUseResponseHandler
            implements
            TaskSummaryResponseHandler {

        private volatile List<TaskSummary> results;
        private volatile RuntimeException error;

        public synchronized void execute(List<TaskSummary> results) {
            this.results = results;
            notifyAll();
        }

        public synchronized List<TaskSummary> getResults() {
            if (results == null) {
                try {
                    wait(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (results == null) {
                throw new RuntimeException("Timeout : unable to retrieve results");
            }

            return results;

        }

        public boolean isDone() {
            synchronized (results) {
                return results != null;
            }
        }

        public void setError(RuntimeException error) {
            this.error = error;
        }

        public RuntimeException getError() {
            return error;
        }
    }
}
