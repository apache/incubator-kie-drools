/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.task.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.utils.TaskFluent;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;

import bitronix.tm.resource.jdbc.PoolingDataSource;


public abstract class HTPerformanceBaseTest extends HumanTaskServicesBaseTest {

    protected PoolingDataSource pds;
    protected EntityManagerFactory emf;

    
    @Test
    public void testBasicUserTaskAddingAndQuering() throws Exception {
        System.out.println("Starting testBasicUserTaskAddingAndQuering ...");
        long beforeStartTime = System.currentTimeMillis();
        int times = 10;
        double totalAdding = 0;
        double totalQuerying = 0;
        for(int j = 1; j < times + 1 ; j ++){
            long beforeAddTime = System.currentTimeMillis();
            int amount = 1000;
            System.out.println("Adding " + amount + " tasks....");
            
            for (int i = 0; i < amount; i++) {
                
                 Task task = new TaskFluent()
                                            .setName("Task #" + i)
                                            .addI18NName("en-UK","First Language Task #"+i)
                                            .addI18NName("en-US", "Second Language Task #"+i)
                                            .addI18NName("es-AR", "Third Language Task #"+i)
                                            .addPotentialUser("salaboy")
                                            .setAdminUser("Administrator")
                                            .getTask();

                taskService.addTask(task, new HashMap<String, Object>());
                
            }
            
            double thisRoundAdding = ((double)(System.currentTimeMillis() - beforeAddTime)/1000);
            totalAdding += thisRoundAdding;
            System.out.println("Finish Adding tasks...." + thisRoundAdding);
            
           
        }
        
        for(int i = 0; i < times; i++){
            long beforeQueryTime = System.currentTimeMillis();
            System.out.println("Querying tasks....");
            List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy",null, null, new QueryFilter(i * 1000,1000));
            double thisRoundQuerying = ((double)(System.currentTimeMillis() - beforeQueryTime)/1000);
            totalQuerying += thisRoundQuerying;
            System.out.println("Finishing query tasks (Page "+i * 1000+")...." + thisRoundQuerying);
            //Assert.assertEquals(amount * j, tasksAssignedAsPotentialOwner.size());
            Assert.assertEquals(1000, tasksAssignedAsPotentialOwner.size());
        }

       
        System.out.println("Finishing testBasicUserTaskAddingAndQuering ... "+((double)(System.currentTimeMillis()-beforeStartTime)/1000));
        System.out.println(" AVG Addition... "+ (totalAdding / times));
        System.out.println(" AVG Querying... "+ (totalQuerying / times));
    }
    
    @Test
    public void testBasicGroupTaskAddingAndQueringAndClaimingSimple() throws Exception {
        System.out.println("Starting testBasicGroupTaskAddingAndQueringAndClaimingSimple ...");
        
        long beforeStartTime = System.currentTimeMillis();
        int times = 10;
        double totalAdding = 0;
        double totalQuerying = 0;
        for(int j = 1; j < times + 1 ; j ++){
            int amount = 1000;
            long beforeAddTime = System.currentTimeMillis();
            System.out.println("Adding " + amount + " tasks....");

            for (int i = 0; i < amount; i++) {
                 Task task = new TaskFluent()
                                                .setName("Task #" + i)
                                                .addI18NName("en-UK","First Language Task #"+i)
                                                .addI18NName("en-US", "Second Language Task #"+i)
                                                .addI18NName("es-AR", "Third Language Task #"+i)
                                                .addPotentialGroup("Knights Templer")
                                                .setAdminUser("Administrator")
                                                .getTask();
                taskService.addTask(task, new HashMap<String, Object>());
            }

            double thisRoundAdding = ((double)(System.currentTimeMillis() - beforeAddTime)/1000);
            totalAdding += thisRoundAdding;
            System.out.println("Finish Adding tasks...." + thisRoundAdding);

            long beforeQueryTime = System.currentTimeMillis();
            System.out.println("Querying tasks potOwner (Ready)....");
            List<Status> statuses = new ArrayList<Status>();
            
            //Getting only the group tasks
            statuses.add(Status.Ready);
            List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy",null, statuses, new QueryFilter(0,0));
            double thisRoundQuerying = ((double)(System.currentTimeMillis() - beforeQueryTime)/1000);
            totalQuerying += thisRoundQuerying;
            System.out.println("Finishing query tasks (Ready)...." + thisRoundQuerying);
            Assert.assertEquals(amount, tasksAssignedAsPotentialOwner.size());

            //We need to check that this are all group tasks!
            for (TaskSummary ts : tasksAssignedAsPotentialOwner) {
                Assert.assertTrue(ts.getStatus().name().equals("Ready"));
            }

            
            
            long beforeClaimingTime = System.currentTimeMillis();
            for (TaskSummary ts : tasksAssignedAsPotentialOwner) {
                taskService.claim(ts.getId(), "salaboy");
            }
            System.out.println("After claiming  tasks...." + ((double)(System.currentTimeMillis() - beforeClaimingTime)/1000));
            
            
            beforeQueryTime = System.currentTimeMillis();
            System.out.println("Querying tasks potOwner (Reserved)....");
            statuses = new ArrayList<Status>();
            //Getting only my tasks
            statuses.add(Status.Reserved);
            tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy",null, statuses, new QueryFilter(0,0));
            thisRoundQuerying = ((double)(System.currentTimeMillis() - beforeQueryTime)/1000);
            totalQuerying += thisRoundQuerying;
            System.out.println("Finishing query tasks (Reserved)...." + thisRoundQuerying);
            Assert.assertEquals(amount, tasksAssignedAsPotentialOwner.size());
            

            long beforeStartingTime = System.currentTimeMillis();
            System.out.println("Before starting  tasks...." );
            for (TaskSummary ts : tasksAssignedAsPotentialOwner) {
                taskService.start(ts.getId(), "salaboy");
            }
            System.out.println("After starting  tasks...." + ((double)(System.currentTimeMillis() - beforeStartingTime)/1000));

            
            beforeQueryTime = System.currentTimeMillis();
            System.out.println("Querying tasks potOwner (InProgress)....");
            statuses = new ArrayList<Status>();
            //Getting only my tasks in progress
            statuses.add(Status.InProgress);
            tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy",null, statuses, new QueryFilter(0,0));
            thisRoundQuerying = ((double)(System.currentTimeMillis() - beforeQueryTime)/1000);
            totalQuerying += thisRoundQuerying;
            System.out.println("Finishing query tasks (InProgress)...." + thisRoundQuerying);
            Assert.assertEquals(amount, tasksAssignedAsPotentialOwner.size());
            
            long beforeCompletingTime = System.currentTimeMillis();
            System.out.println("Before completing  tasks...." );
            for (TaskSummary ts : tasksAssignedAsPotentialOwner) {
                taskService.complete(ts.getId(), "salaboy", null);
            }
            System.out.println("After completing  tasks...." + ((double)(System.currentTimeMillis() - beforeCompletingTime)/1000));

            beforeQueryTime = System.currentTimeMillis();
            System.out.println("Querying tasks potOwner (Completed)....");
            statuses = new ArrayList<Status>();
            //Getting only my tasks completed
            statuses.add(Status.Completed);
            tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy",null, statuses, new QueryFilter(0,0));
            System.out.println("Finishing query potOwner (Completed )tasks...." + ((double)(System.currentTimeMillis() - beforeQueryTime)/1000));
            Assert.assertEquals(amount * j, tasksAssignedAsPotentialOwner.size());
        
        }
        
        
        System.out.println("Finishing testBasicGroupTaskAddingAndQueringAndClaimingSimple ..." + 
                ((double)(System.currentTimeMillis() - beforeStartTime)/1000));
        System.out.println(" AVG Addition... "+ (totalAdding / times));
        System.out.println(" AVG Querying... "+ (totalQuerying / times / 4));
    }
    

}
