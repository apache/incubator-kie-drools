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

package org.jbpm.services.task.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionImpl;
import org.hibernate.persister.collection.CollectionPersister;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.OrganizationalEntityImpl;
import org.jbpm.services.task.impl.model.OrganizationalEntityImpl_;
import org.jbpm.services.task.impl.model.PeopleAssignmentsImpl;
import org.jbpm.services.task.impl.model.PeopleAssignmentsImpl_;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import org.jbpm.services.task.impl.model.TaskDataImpl_;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.impl.model.TaskImpl_;
import org.jbpm.services.task.impl.model.UserImpl_;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 * 
 * Verifying that the new JPA Criteria API approach is more performant (primarilly because of better use of joins!)
 *
 */
@Ignore
public class DistincVsJoinPerformanceTest extends HumanTaskServicesBaseTest {

    private PoolingDataSource pds;
    private EntityManagerFactory emf;
    
    private static final String stakeHolder = "vampire";
    
    @Before
    public void setup() {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );

        this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
                                                .entityManagerFactory(emf)
                                                .getTaskService();
    }
    
    @After
    public void clean() {
        super.tearDown();
        if (emf != null) {
            emf.close();
        }
        if (pds != null) {
            pds.close();
        }
    }
    
        
    @Test
    public void performanceTest() {
        long workItemId = 59;
        long procInstId = 12111;
        String busAdmin = "Wintermute";
        String potOwner = "Maelcum";
        String deploymentId = "Dixie Flatline";
        String name = "Complete Mission";
        
        int total = 100;
       for( int i = 0; i < total; ++i ) { 
               // Add two more tasks, in order to have a quorum
                ++workItemId;
                ++procInstId;
                busAdmin = "Wintermute";
                potOwner = "Maelcum";
                deploymentId = "Dixie Flatline";
                name = "Complete Mission";        
               addTask(workItemId, procInstId, busAdmin, potOwner, name, deploymentId);
               
               // Add two more tasks, in order to have a quorum
               ++workItemId;
               ++procInstId;
               busAdmin = "Neuromancer";
               potOwner = "Hideo";
               deploymentId = "Linda Lee";
               name = "Resurrect";
               addTask(workItemId, procInstId, busAdmin, potOwner, name, deploymentId);
       }
        
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Ninja");
        String userId = "Hideo";
        
        EntityManager em  = emf.createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
      
        long joinTotal = 0;
        long selectTotal = 0;
       
        for( int i = 0; i < 500; ++i ) { 

            // Add two more tasks, in order to have a quorum
            ++workItemId;
            ++procInstId;
            busAdmin = "Neuromancer";
            potOwner = i % 2 == 0 ? "Ninja" : "Hideo";
            deploymentId = "Linda Lee";
            name = "Resurrect";
            addTask(workItemId, procInstId, busAdmin, potOwner, name, deploymentId);
            ++total;

            long selectDur, joinDur;
            if( i % 2 == 0 ) { 
                selectDur = doSelectQuery(em, userId, groupIds, total);
                joinDur = doJoinQuery(em, userId, groupIds, total);
            } else { 
                joinDur = doJoinQuery(em, userId, groupIds, total);
                selectDur = doSelectQuery(em, userId, groupIds, total);
            }
            
            System.out.println( "API: " + joinDur + " JPQL: " + selectDur);
            joinTotal += joinDur;
            selectTotal += selectDur;
        }
        joinTotal /= 20;
        selectTotal /= 20;
        assertTrue( "Join [" + joinTotal + "ms] took longer than Select [" + selectTotal + "ms]!",
                joinTotal < selectTotal );
        
    }

    private long doJoinQuery(EntityManager em, String userId, List<String> groupIds, int total) { 
        CriteriaBuilder builder = em.getCriteriaBuilder();
        
        CriteriaQuery<TaskImpl> joinQuery = builder.createQuery(TaskImpl.class);
        Root<TaskImpl> taskRoot = joinQuery.from(TaskImpl.class);
        Join<TaskImpl, TaskDataImpl> join = taskRoot.join(TaskImpl_.taskData);
      
        Selection select = getTaskSummarySelect(builder, taskRoot);
        joinQuery.select(select);
        Join<TaskImpl, PeopleAssignmentsImpl> peopleAssign = taskRoot.join(TaskImpl_.peopleAssignments);
        ListJoin<PeopleAssignmentsImpl,OrganizationalEntityImpl> busAdmins =  peopleAssign.join(PeopleAssignmentsImpl_.businessAdministrators, JoinType.LEFT);
        ListJoin<PeopleAssignmentsImpl,OrganizationalEntityImpl> potOwners =  peopleAssign.join(PeopleAssignmentsImpl_.potentialOwners, JoinType.LEFT);
        ListJoin<PeopleAssignmentsImpl,OrganizationalEntityImpl> stakeHols =  peopleAssign.join(PeopleAssignmentsImpl_.taskStakeholders, JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add( builder.equal(taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.actualOwner).get(UserImpl_.id), userId) );
        predicates.add( builder.equal(taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.createdBy).get(UserImpl_.id), userId) );
        
        predicates.add( builder.or( 
                builder.equal( busAdmins.get(OrganizationalEntityImpl_.id), userId ), 
                busAdmins.get(OrganizationalEntityImpl_.id).in(groupIds) ) );
        predicates.add( builder.or( 
                builder.equal( potOwners.get(OrganizationalEntityImpl_.id), userId ), 
                potOwners.get(OrganizationalEntityImpl_.id).in(groupIds) ) );
        predicates.add( builder.or( 
                builder.equal( stakeHols.get(OrganizationalEntityImpl_.id), userId ), 
                stakeHols.get(OrganizationalEntityImpl_.id).in(groupIds) ) );

        if( ! predicates.isEmpty() ) { 
            joinQuery.where(builder.or(predicates.toArray(new Predicate[predicates.size()])));
        }

        return timeQueryExecution(em, joinQuery, null, total); 
    }
    
    private long doSelectQuery(EntityManager em, String userId, List<String> groupIds, int total) { 
        String selectFrom = 
                "SELECT distinct new org.jbpm.services.task.query.TaskSummaryImpl(\n" +
                "       t.id,\n" +
                "       t.name,\n" +
                "       t.description,\n" +
                "       t.taskData.status,\n" +
                "       t.priority,\n" +
                "       t.taskData.actualOwner.id,\n" +
                "       t.taskData.createdBy.id,\n" +
                "       t.taskData.createdOn,\n" +
                "       t.taskData.activationTime,\n" +
                "       t.taskData.expirationTime,\n" +
                "       t.taskData.processId,\n" +
                "       t.taskData.processInstanceId,\n" +
                "       t.taskData.parentId,\n" +
                "       t.taskData.deploymentId,\n" +
                "       t.taskData.skipable )\n" +
                "FROM TaskImpl t,\n"
              + "     OrganizationalEntityImpl stakeHolders,\n"
              + "     OrganizationalEntityImpl potentialOwners,\n"
              + "     OrganizationalEntityImpl businessAdministrators\n"
              + "WHERE ";
        StringBuffer queryStr = new StringBuffer(selectFrom);
       
        String userIdParam = "U";
        String groupIdsParam = "G";
        
        queryStr
        .append("t.taskData.createdBy.id = :").append(userIdParam).append("\n OR ")
        .append("( stakeHolders.id in :").append(groupIdsParam).append(" and\n")
        .append("  stakeHolders in elements ( t.peopleAssignments.taskStakeholders ) )").append("\n OR " )
        .append("( potentialOwners.id in :").append(groupIdsParam).append(" and\n")
        .append("  potentialOwners in elements ( t.peopleAssignments.potentialOwners ) )").append("\n OR " )
        .append("t.taskData.actualOwner.id = :").append(userIdParam).append("\n OR ")
        .append("( businessAdministrators.id in :").append(groupIdsParam).append(" and\n")
        .append("  businessAdministrators in elements ( t.peopleAssignments.businessAdministrators ) )")
        .append(" )\n");
       
        Query realQuery = em.createQuery(queryStr.toString());
        realQuery.setParameter(userIdParam, userId);
        realQuery.setParameter(groupIdsParam, groupIds);
        
        return  timeQueryExecution(em, null, realQuery, total); 
    }
    
    private String [] createOriginalAndExpectedKeys(Attribute embeddedAttr, PluralAttribute listAttr) { 
        String originalKey = embeddedAttr.getDeclaringType().getJavaType().getName() 
                + "." + embeddedAttr.getName()
                + "." + listAttr.getName();
        
        String copyKey = listAttr.getDeclaringType().getJavaType().getName()
                + "." + listAttr.getName(); 
        String [] keys = { originalKey, copyKey };
        return keys;
    }
    
    private void copyCollectionPersisterKeys(Attribute embeddedAttr, PluralAttribute listAttr, EntityManager em) {
        String [] keys = createOriginalAndExpectedKeys(embeddedAttr, listAttr);
        try {
            SessionImpl session = (SessionImpl) em.getDelegate();
            SessionFactoryImplementor sessionFactory = session.getSessionFactory();
            CollectionPersister persister =   sessionFactory.getCollectionPersister(keys[0]);
            sessionFactory.getCollectionPersisters().put(keys[1], persister);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Selection<TaskSummaryImpl> getTaskSummarySelect(CriteriaBuilder builder, Root<TaskImpl> taskRoot) { 
        Selection<TaskSummaryImpl> select = builder.construct(TaskSummaryImpl.class, 
                taskRoot.get(TaskImpl_.id), 
                taskRoot.get(TaskImpl_.name), 
                taskRoot.get(TaskImpl_.subject), 
                taskRoot.get(TaskImpl_.description), 

                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.status),
                taskRoot.get(TaskImpl_.priority), 

                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.skipable), 

                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.actualOwner).get(UserImpl_.id),
                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.createdBy).get(UserImpl_.id), 

                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.createdOn), 

                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.activationTime), 
                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.expirationTime), 

                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.processId), 
                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.processSessionId), 
                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.processInstanceId), 
                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.deploymentId), 

                taskRoot.get(TaskImpl_.subTaskStrategy),
                taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.parentId)
                );
        return select;
    }
    
    private long timeQueryExecution(EntityManager em, CriteriaQuery query, Query realQuery, int total) { 
        
        if( realQuery == null ) { 
            realQuery = em.createQuery(query);
            realQuery.setMaxResults(2000);
        }
        
        long start = System.nanoTime(); 
        List<TaskSummary> results = realQuery.getResultList();
        long end = System.nanoTime(); 
       
        assertEquals( "query results", total, results.size() );
        
        return (end - start)/1000000; 
    }
   
    private TaskImpl addTask( long workItemId, long procInstId, String busAdmin, String potOwner, String name, String deploymentId) { 
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        
        String potOwnerType = potOwner.equals("Hideo") ? "User" : "Group";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { "
                + "taskStakeholders = [new User('" + stakeHolder + "')],"
                + "businessAdministrators = [new User('" + busAdmin + "')],"
                + "potentialOwners = [new " + potOwnerType + "('" + potOwner + "')]"
                + " }),";
        str += "name =  '" + name + "' })";
        Task taskImpl = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) taskImpl.getTaskData()).setWorkItemId(workItemId);
        ((InternalTaskData) taskImpl.getTaskData()).setProcessInstanceId(procInstId);
        ((InternalTaskData) taskImpl.getTaskData()).setDeploymentId(deploymentId);
        taskService.addTask(taskImpl, new HashMap<String, Object>());
        assertNotNull( "Null task id", taskImpl.getId());
        return (TaskImpl) taskImpl;
    }
}
