/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jbpm.test.persistence;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.services.task.admin.listener.TaskCleanUpProcessEventListener;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;


@RunWith(Parameterized.class)
public class CallActivitiesWithUserTasksProcessTest extends JbpmJUnitBaseTestCase {
  
    @Parameters(name="{1} | user managed={0}")
    public static Collection<Object[]> parameters() {
        Object[][] locking = new Object[][] { 
                { true, Strategy.SINGLETON }, 
                { true, Strategy.PROCESS_INSTANCE }, 
                { false, Strategy.SINGLETON }, 
                { false, Strategy.PROCESS_INSTANCE },
                };
        return Arrays.asList(locking);
    };
  
    private final boolean userManagedTx;
    private final Strategy strategy;
    
    public CallActivitiesWithUserTasksProcessTest(boolean txManagedType, Strategy runtimeStrategy) {
        super(true, true);
        this.userManagedTx = txManagedType;
        this.strategy = runtimeStrategy;
    }
  
    @Test
    public void testCallActivitiesWithUserTasks() throws Exception {
        InitialContext context = new InitialContext();
        UserTransaction ut =  (UserTransaction) context.lookup( JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME ); 
        
        createRuntimeManager(strategy, (String) null, "BPMN2-CallActivityWithTask-Main.bpmn2", "BPMN2-CallActivityWithTask-Sub.bpmn2");
       
        RuntimeEngine runtimeEngine;
        if( Strategy.SINGLETON.equals(strategy) ) { 
            runtimeEngine = getRuntimeEngine();
        } else if( Strategy.PROCESS_INSTANCE.equals(strategy) ) { 
            runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
        } else { 
            throw new IllegalStateException("Not possible!");
        }
        
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        ksession.addEventListener(new TaskCleanUpProcessEventListener(taskService));        
        ksession.getWorkItemManager().registerWorkItemHandler("Sysout", new SystemOutWorkItemHandler());
                 
        if( userManagedTx ) { 
            ut.begin();
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pActorId", "john");
        ProcessInstance processInstance = ksession.startProcess("PolicyValueAnalysis", params);
        
        if( userManagedTx ) { 
            ut.commit();
            ut.begin();
        }
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        Long taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);
        
        if( userManagedTx ) { 
            ut.commit();
            ut.begin();
        }
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        taskId = tasks.get(0).getId();

        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);
        
        if( userManagedTx ) { 
            ut.commit();
            ut.begin();
        }
        
        assertProcessInstanceCompleted(processInstance.getId(), ksession);

        if( userManagedTx ) { 
            ut.commit();
        }
    }
    
    @Test
    public void testCallActivitiesWith2ndUserTaskInSub() throws Exception {
        InitialContext context = new InitialContext();
        UserTransaction ut =  (UserTransaction) context.lookup( JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME ); 
        
        createRuntimeManager(strategy, (String) null, "BPMN2-CallActivityWithTaskInSub-Main.bpmn2", "BPMN2-CallActivityWithTaskInSub-Sub.bpmn2");
       
        RuntimeEngine runtimeEngine;
        if( Strategy.SINGLETON.equals(strategy) ) { 
            runtimeEngine = getRuntimeEngine();
        } else if( Strategy.PROCESS_INSTANCE.equals(strategy) ) { 
            runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
        } else { 
            throw new IllegalStateException("Not possible!");
        }
        
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        ksession.addEventListener(new TaskCleanUpProcessEventListener(taskService));        
        ksession.getWorkItemManager().registerWorkItemHandler("Sysout", new SystemOutWorkItemHandler());
                 
        if( userManagedTx ) { 
            ut.begin();
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pActorId", "john");
        ProcessInstance processInstance = ksession.startProcess("PolicyValueAnalysis", params);
        
        if( userManagedTx ) { 
            ut.commit();
            ut.begin();
        }
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        Long taskId = tasks.get(0).getId();
        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);
      
        if( userManagedTx ) { 
            ut.commit();
            ut.begin();
        }
        
        // sub process task 
        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
      
        taskId = tasks.get(0).getId();
        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);
        
        if( userManagedTx ) { 
            ut.commit();
            ut.begin();
        }
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        taskId = tasks.get(0).getId();

        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);
        
        if( userManagedTx ) { 
            ut.commit();
            ut.begin();
        }
        
        // sub process task 
        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
      
        taskId = tasks.get(0).getId();
        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);
        
        assertProcessInstanceCompleted(processInstance.getId(), ksession);

        if( userManagedTx ) { 
            ut.commit();
        }
    }
}
