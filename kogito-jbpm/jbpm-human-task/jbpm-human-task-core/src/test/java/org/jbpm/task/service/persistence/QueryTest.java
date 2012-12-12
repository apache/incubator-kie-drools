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

package org.jbpm.task.service.persistence;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jbpm.task.BaseTest;
import org.jbpm.task.MvelFilePath;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.query.DeadlineSummary;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.MockEscalatedDeadlineHandler;

public class QueryTest extends BaseTest {

    public void testUnescalatedDeadlines() throws Exception {
        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
        taskService.setEscalatedDeadlineHandler( handler );       
        Map vars = new HashMap();
        vars.put( "users",
                  users );
        vars.put( "groups",
                  groups );


        //Reader reader;
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( MvelFilePath.UnescalatedDeadlines ) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );
        for ( Task task : tasks ) {
            taskSession.addTask( task, null );
        }
        long now = ((Date)vars.get( "now" )).getTime();
        
        // should be three, one is marked as escalated
        TaskPersistenceManager tpm = new TaskPersistenceManager(emf.createEntityManager());
        List<DeadlineSummary> list = tpm.getUnescalatedDeadlines();
        
        assertEquals( 3,
                      list.size() );

        boolean firstDeadlineMet = false;
        boolean secondDeadlineMet = false;
        boolean thirdDeadlineMet = false;
        for( DeadlineSummary summary : list ) { 
            long deadlineTime = summary.getDate().getTime();
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
    
    String queryString = 
              "select new org.jbpm.task.query.TaskSummary("
            + "     t.id,"
            + "     t.taskData.processInstanceId,"
            + "     name.text,"
            + "     subject.text,"
            + "     description.text,"
            + "     t.taskData.status,"
            + "     t.priority,"
            + "     t.taskData.skipable,"
            + "     actualOwner,"
            + "     createdBy,"
            + "     t.taskData.createdOn,"
            + "     t.taskData.activationTime,"
            + "     t.taskData.expirationTime,"
            + "     t.taskData.processId,"
            + "     t.taskData.processSessionId)"
            + " from"
            + "    Task t"
            + "    left join t.taskData.createdBy as createdBy"
            + "    left join t.taskData.actualOwner as actualOwner"
            + "    left join t.subjects as subject"
            + "    left join t.descriptions as description"
            + "    left join t.names as name,"
            + "    OrganizationalEntity potOwn"
            + " where"
            + "    potOwn.id = :userId and"
            + "    potOwn in elements ( t.peopleAssignments.potentialOwners  ) and"
            + "    t.taskData.status in ('Created', 'Ready', 'Reserved', 'InProgress', 'Suspended') and"
            + "    t.taskData.expirationTime is null";

    /**
     * This test works with Hibernate 3, but not with Hibernate 4
     * It has something to do with potential owners.. 
     * 
     * @throws Exception
     */
    public void testPotentialOwnerHibernate4QueryTest() throws Exception { 
        TaskPersistenceManager tpm = new TaskPersistenceManager(emf.createEntityManager());
        String name = "Bobba Fet";
        Task task = new Task();
        User bobba = new User();
        bobba.setId(name);
        
        task.setPriority(55);
        TaskData taskData = new TaskData();
        taskData.setActivationTime(new Date());
        taskData.setCreatedOn(new Date());
        taskData.setDocumentContentId(-1);
        taskData.setFault(-1, new FaultData());
        taskData.setOutput(-1, new ContentData());
        taskData.setParentId(-1);
        taskData.setPreviousStatus(Status.Created);
        taskData.setProcessInstanceId(-1);
        taskData.setProcessSessionId(0);
        taskData.setSkipable(false);
        taskData.setStatus(Status.Reserved);
        taskData.setWorkItemId(1);
        
        task.setTaskData(taskData);
        task.setPeopleAssignments(new PeopleAssignments());
        task.getPeopleAssignments().setPotentialOwners((List) new ArrayList<OrganizationalEntity>());
        task.getPeopleAssignments().getPotentialOwners().add(bobba);
        
        tpm.beginTransaction();
        tpm.saveEntity(task);
        task = (Task) tpm.findEntity(Task.class, task.getId());
        tpm.endTransaction(true);
        
        tpm.beginTransaction();
        List<TaskSummary> list = null;
//        list = tpm.queryTasksWithUserIdAndLanguage("TasksAssignedAsPotentialOwner", name, "en-UK"); 
        Query query = tpm.createNewQuery(queryString);
        query.setParameter("userId", name);
        
        list = query.getResultList();
        tpm.endTransaction(true);
        
        assertTrue( "Query did not succeed.", list.size() > 0 );
    }

}
