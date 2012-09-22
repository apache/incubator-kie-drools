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

import org.jbpm.task.MvelFilePath;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.task.*;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClientHandler.TaskSummaryResponseHandler;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.utils.CollectionUtils;

public abstract class TaskServiceBaseUserGroupCallbackAsyncTest extends BaseTestNoUserGroupSetup {
    
    protected TaskServer server;
    protected AsyncTaskService client;

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }

    
    public void testTasksOwnedQueryWithI18N() throws Exception {
        runTestTasksOwnedQueryWithI18N(client, users, groups);
    }
    
    @SuppressWarnings("unchecked")
    public static void runTestTasksOwnedQueryWithI18N(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) { 
        Map<String, Object>  vars = fillVariables(users, groups);
        
        //Reader reader;
        Reader reader = new InputStreamReader( TaskServiceBaseUserGroupCallbackAsyncTest.class.getResourceAsStream( MvelFilePath.TasksOwned ) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );
        for ( Task task : tasks ) {
            BlockingAddTaskResponseHandler responseHandler = new BlockingAddTaskResponseHandler();
            client.addTask( task, null, responseHandler );
        }

        // Test UK I18N  
        reader = new InputStreamReader( TaskServiceBaseUserGroupCallbackAsyncTest.class.getResourceAsStream( MvelFilePath.TasksOwnedInEnglish ) );
        Map<String, List<TaskSummary>> expected = (Map<String, List<TaskSummary>>) eval( reader,
                                                                                         vars );

        BlockingAllOpenTasksForUseResponseHandler responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned( users.get( "peter" ).getId(),
                              "en-UK",
                              responseHandler );
        List<TaskSummary> actual = responseHandler.getResults();
        assertEquals( 3,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "peter" ),
                                            actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned( users.get( "steve" ).getId(),
                              "en-UK",
                              responseHandler );
        actual = responseHandler.getResults();
        assertEquals( 2,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "steve" ),
                                            actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned( users.get( "darth" ).getId(),
                              "en-UK",
                              responseHandler );
        actual = responseHandler.getResults();
        assertEquals( 1,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "darth" ),
                                            actual ) );

        // Test DK I18N 
        reader = new InputStreamReader( TaskServiceBaseUserGroupCallbackAsyncTest.class.getResourceAsStream( MvelFilePath.TasksOwnedInGerman ) );
        expected = (Map<String, List<TaskSummary>>) eval( reader,
                                                          vars );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned( users.get( "peter" ).getId(),
                              "en-DK",
                              responseHandler );
        actual = responseHandler.getResults();
        assertEquals( 3,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "peter" ),
                                            actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned( users.get( "steve" ).getId(),
                              "en-DK",
                              responseHandler );
        actual = responseHandler.getResults();
        assertEquals( 2,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "steve" ),
                                            actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksOwned( users.get( "darth" ).getId(),
                              "en-DK",
                              responseHandler );
        actual = responseHandler.getResults();
        assertEquals( 1,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "darth" ),
                                            actual ) );
    }
    
    public void testPotentialOwnerQueries() {
        runTestPotentialOwnerQueries(client, users, groups);
    }
    
    @SuppressWarnings({ "unchecked" })
    public static void runTestPotentialOwnerQueries(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) { 
        Map<String, Object>  vars = fillVariables(users, groups);
        
        BlockingAllOpenTasksForUseResponseHandler getTasksHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get( "bobba" ).getId(), "en-UK", getTasksHandler );
        int bobbaSize = getTasksHandler.getResults().size();
        
        //Reader reader;
        Reader reader = new InputStreamReader( TaskServiceBaseUserGroupCallbackAsyncTest.class.getResourceAsStream( MvelFilePath.TasksPotentialOwner ) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );
        for ( Task task : tasks ) {
            BlockingAddTaskResponseHandler responseHandler = new BlockingAddTaskResponseHandler();
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
            client.addTask( task, null, responseHandler );
        }

        // Test UK I18N  
        getTasksHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get( "bobba" ).getId(),
                              "en-UK",
                              getTasksHandler );
        List<TaskSummary> actual = getTasksHandler.getResults();
        assertEquals( 2,
                      actual.size() );
    }
    
    public void testPeopleAssignmentQueries() {
        runTestPeopleAssignmentQueries(client, users, groups, taskSession);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void runTestPeopleAssignmentQueries(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups, TaskServiceSession taskSession) { 
        Map vars = fillVariables(users, groups);

        Reader reader = new InputStreamReader( TaskServiceBaseUserGroupCallbackAsyncTest.class.getResourceAsStream(MvelFilePath.TasksOwned) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );

        for ( Task task : tasks ) {
            BlockingAddTaskResponseHandler addTaskHandler = new BlockingAddTaskResponseHandler();
            client.addTask(task, null, addTaskHandler);
        }

        reader = new InputStreamReader( TaskServiceBaseUserGroupCallbackAsyncTest.class.getResourceAsStream( MvelFilePath.PeopleAssignmentQuerries ) );
        Map<String, List<TaskSummary>> expected = (Map<String, List<TaskSummary>>) eval( reader,
                                                                                         vars );

        BlockingAllOpenTasksForUseResponseHandler responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsTaskInitiator( users.get( "darth" ).getId(),
                                                "en-UK",
                                                responseHandler );
        List<TaskSummary> actual = responseHandler.getResults();
        assertEquals( 1,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "darth" ),
                                            actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsBusinessAdministrator( users.get( "steve" ).getId(),
                                                        "en-UK",
                                                        responseHandler );
        actual = responseHandler.getResults();
        assertTrue( CollectionUtils.equals( expected.get( "steve" ),
                                            actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsExcludedOwner( users.get( "liz" ).getId(),
                                                "en-UK",
                                                responseHandler );
        actual = responseHandler.getResults();
        assertEquals( 2,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "liz" ),
                                            actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsPotentialOwner( users.get( "bobba" ).getId(),
                                                 "en-UK",
                                                 responseHandler );
        actual = responseHandler.getResults();
        assertEquals( 3,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "bobba" ),
                                            actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();
        client.getTasksAssignedAsRecipient( users.get( "sly" ).getId(),
                                            "en-UK",
                                            responseHandler );
        actual = responseHandler.getResults();
        assertEquals( 1,
                      actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "sly" ),
                                            actual ) );
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
            if ( results == null ) {
                try {
                    wait( 3000 );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }

            if ( results == null ) {
                throw new RuntimeException( "Timeout : unable to retrieve results" );
            }

            return results;

        }

        public boolean isDone() {
            synchronized ( results ) {
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
