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

package org.jbpm.task.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbpm.task.BaseTest;
import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.TaskService;
import org.jbpm.task.User;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;

/**
 * Thanks to jbride for development of the test.
 * 
 */
public abstract class ClaimTaskTwiceTest extends BaseTest {

    protected static org.jbpm.task.service.TaskService taskSessionFactory;
    
    // Test
    protected long taskId;
    protected final static String GROUP_NAME = "Crusaders";

    // Thread
    protected ScheduledExecutorService execObj = null;
    protected int clientCount = 2;
    protected int delay = 0;

    // Check
    protected static Set<String> claimersSet = new CopyOnWriteArraySet<String>();
    protected boolean taskClaimedTwice = false;

    protected void setUp() throws Exception {
        super.setUp();
        taskSessionFactory = taskService;
        logger.info("setUp() clientCount = " + clientCount + " : delay = " + delay);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private Task createTask() { 
        // Create task
        Task task = new Task();
        TaskData tDataObj = new TaskData();
        task.setTaskData(tDataObj);
    
        Group groupObj = new Group(GROUP_NAME);
        List<OrganizationalEntity> groups = new ArrayList<OrganizationalEntity>();
        groups.add(groupObj);
        PeopleAssignments peopleAssignmentObj = new PeopleAssignments();
        peopleAssignmentObj.setPotentialOwners(groups);
        task.setPeopleAssignments(peopleAssignmentObj);
        
        return task;
    }
    
    public void testDummy() {
    	
    }

    // test is not using datasource linked to transaction manager
    // so transactions are on autocommit in this case
    // need to fix this first, to check if problem still exists then
    public void fixmetestMultipleClientsOneTask() throws Exception {
        // Create and insert task
        Task task = createTask();
        TaskService client = createClient("setup");
        client.addTask(task, new ContentData());
        cleanupClient(client);
        
        taskId = task.getId();
        logger.info("setUp() taskId = " + taskId);

        Properties userGroups = new Properties();
        // Setup user/group stuff
        User[] userArray = new User[clientCount];
        String[] userNames = { "krisv", "john", "mary" };
        for (int t = 0; t < clientCount; t++) {
            User user = new User(userNames[t]);
            userArray[t] = user;
            userGroups.setProperty(user.getId(), GROUP_NAME);
        }
        // set callback with configured user groups
        UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(userGroups));

        try {
            execObj = Executors.newScheduledThreadPool(clientCount);

            for (int t = 0; t < clientCount; t++) {
                delay = delay + (t * delay);
                Runnable siClient = new TaskOperationThread(userArray[t].getId());
                execObj.schedule(siClient, delay, TimeUnit.MILLISECONDS);
            }

            execObj.shutdown();
            execObj.awaitTermination(60, TimeUnit.SECONDS);
            logger.info("main() all tasks completed on ExecutorService ...");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        Status status = taskSession.getTask(taskId).getTaskData().getStatus();
        assertTrue(status == Status.Reserved);
        assertTrue("Task with task id " + taskId + " has been claimed twice!", !taskClaimedTwice);
    }

    private static AtomicInteger threadIdGenerator = new AtomicInteger(0);
    
    class TaskOperationThread implements Runnable {
        TaskService threadClient = null;
        final String threadName;
        String userId;
    
        public TaskOperationThread(String userId) {
            threadName = "thread-" + threadIdGenerator.incrementAndGet();
            try { 
                threadClient = createClient(threadName);
            }
            catch(Exception e) { 
                logger.error("Could not initialize thread client: " + e.getClass().getSimpleName() + " [" + e.getMessage() + "]");
            }
            this.userId = userId;
        }
    
        public void run() {
            try {
                for(int i = 0; i < 2 && ! taskClaimedTwice; ++i ) { 
                    
                    try { 
                        threadClient.claim(taskId, userId);
                    } catch (PermissionDeniedException pde) {
                        logger.debug("run() userId = " + userId + " : taskId = " + taskId + " : claimed by other user already!");
                        continue;
                    }
                    
                    int numClaimers = 0;
                    synchronized(claimersSet) { 
                        claimersSet.add(threadName);
                        numClaimers = claimersSet.size();
                    }
                    if (numClaimers > 1) { 
                        taskClaimedTwice = true;
                    } else {
                        logger.info("run() just claimed task with userId = " + userId);
                    }
    
                    if( ! taskClaimedTwice ) { 
                        synchronized(claimersSet) { 
                            threadClient.release(taskId, userId);
                            claimersSet.remove(threadName);
                        }
                        Thread.sleep(500);
                    }
                }
            } catch (javax.persistence.RollbackException re) {
                Throwable secondCause = re.getCause().getCause();
                logger.error("run() userId = " + userId + " : taskId = " + taskId + " :  exception cause(s) = \n\t" + re.getCause()
                        + "\n\t" + secondCause);
            } catch (PermissionDeniedException pde) {
                logger.error("run() userId = " + userId + " : taskId = " + taskId + " : claimed by other user already!");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                try {
                    cleanupClient(threadClient);
                } catch (Exception e) {
                    // do nothing..
                }
            }
        }
    }

    protected TaskService createClient(String clientName) throws Exception { 
        throw new UnsupportedOperationException("This must be implemented in the implementation test class!");
    }

    protected void cleanupClient(TaskService client) throws Exception { 
        throw new UnsupportedOperationException("This must be implemented in the implementation test class!");
    }

}
