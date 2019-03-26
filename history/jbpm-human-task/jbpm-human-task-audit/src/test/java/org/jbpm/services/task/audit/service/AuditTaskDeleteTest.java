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

package org.jbpm.services.task.audit.service;

import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertEquals;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.jbpm.services.task.utils.TaskFluent;
import org.junit.After;
//import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.query.AuditTaskDeleteBuilder;
import org.kie.internal.task.query.AuditTaskQueryBuilder;

public class AuditTaskDeleteTest extends TaskJPAAuditService {
    
    private static HashMap<String, Object> context;
    private static EntityManagerFactory emf;

    private Task [] taskTestData;
    
    
    @BeforeClass
    public static void configure() { 
        LoggingPrintStream.interceptSysOutSysErr();
    }
    
    @AfterClass
    public static void reset() { 
        LoggingPrintStream.resetInterceptSysOutSysErr();
        
    }

    @Before
    public void setUp() throws Exception {
    	context = setupWithPoolingDataSource("org.jbpm.services.task", "jdbc/jbpm-ds");
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        this.persistenceStrategy = new StandaloneJtaStrategy(emf);
        
        produceTaskInstances();
    }
    
    @After
    public void cleanup() {
    	cleanUp(context);
    }
   
    private static Random random = new Random();

    private Calendar randomCal() { 
        Calendar cal = GregorianCalendar.getInstance();
        cal.roll(Calendar.DAY_OF_YEAR, -1*random.nextInt(10*365));
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }
    
    private void produceTaskInstances() {
    	InternalTaskService taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
				.entityManagerFactory(emf)
				.listener(new JPATaskLifeCycleEventListener(true))
				.listener(new BAMTaskEventListener(true))
				.getTaskService();
    	
    	Calendar cal = randomCal();
    	String processId = "process";
    	taskTestData = new Task[10];
    	
    	List<ProcessInstanceLog> pLogs = new ArrayList<>();
    	
    	for (int i = 0; i < 10; i++) {
    		cal.add(Calendar.HOUR_OF_DAY, 1);
    		Task task = new TaskFluent().setName("This is my task name")
                 .addPotentialGroup("Knights Templer")
                 .setAdminUser("Administrator")
                 .setProcessId(processId + i)
                 .setProcessInstanceId(i)
                 .setCreatedOn(cal.getTime())
                 .getTask();

			taskService.addTask(task, new HashMap<String, Object>());	
			taskTestData[i] = task;
			
			ProcessInstanceLog plog = buildCompletedProcessInstance(i);
			pLogs.add(plog);
    	}
    	
    	StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();
        Object tx = jtaHelper.joinTransaction(em);
        pLogs.forEach(pl -> {
            em.persist(pl);
        });
        jtaHelper.leaveTransaction(em, tx);
    }

    @Test
    public void testDeleteAuditTaskInfoLogByProcessId() { 
        int p = 0;
        String processId = taskTestData[p++].getTaskData().getProcessId();
        String processId2 = taskTestData[p++].getTaskData().getProcessId();
        
        AuditTaskDeleteBuilder updateBuilder = this.auditTaskDelete().processId(processId, processId2);
        int result = updateBuilder.build().execute();
        assertEquals(2, result);
    }
    
    @Test
    public void testDeleteAuditTaskInfoLogByDate() { 
        int p = 0;        
        Date endDate = taskTestData[p++].getTaskData().getCreatedOn();
        
        AuditTaskDeleteBuilder updateBuilder = this.auditTaskDelete().date(endDate);
        int result = updateBuilder.build().execute();
        assertEquals(1, result);
    }
    
    @Test
    public void testDeleteAuditTaskInfoLogByProcessIdAndDate() { 
        int p = 0;     
        String processId = taskTestData[p].getTaskData().getProcessId();
        Date endDate = taskTestData[p].getTaskData().getCreatedOn();
        
        AuditTaskDeleteBuilder updateBuilder = this.auditTaskDelete().date(endDate).processId(processId);
        int result = updateBuilder.build().execute();
        assertEquals(1, result);
    }
    
    @Test
    public void testDeleteAuditTaskInfoLogByProcessIdAndNotMatchingDate() { 
        int p = 0;     
        String processId = taskTestData[p++].getTaskData().getProcessId();
        Date endDate = taskTestData[p++].getTaskData().getCreatedOn();
        
        AuditTaskDeleteBuilder updateBuilder = this.auditTaskDelete().date(endDate).processId(processId);
        int result = updateBuilder.build().execute();
        assertEquals(0, result);
    }
    
    @Test
    public void testDeleteAuditTaskInfoLogByDateRangeEnd() { 
        
        Date endDate = taskTestData[4].getTaskData().getCreatedOn();
        
        AuditTaskDeleteBuilder updateBuilder = this.auditTaskDelete().dateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        assertEquals(5, result);
    }
    
    @Test
    public void testDeleteAuditTaskInfoLogByDateRangeStart() { 
        
        Date endDate = taskTestData[8].getTaskData().getCreatedOn();
        AuditTaskDeleteBuilder updateBuilder = this.auditTaskDelete().dateRangeStart(endDate);
        int result = updateBuilder.build().execute();
        assertEquals(2, result);
    }
    
    @Test
    public void testDeleteAuditTaskInfoLogByDateRange() { 
    	Date startDate = taskTestData[4].getTaskData().getCreatedOn();
        Date endDate = taskTestData[8].getTaskData().getCreatedOn();
        AuditTaskDeleteBuilder updateBuilder = this.auditTaskDelete().dateRangeStart(startDate).dateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        assertEquals(5, result);
    }
    
    @Test
    public void testTaskAuditServiceClear() { 
        AuditTaskQueryBuilder queryBuilder = this.auditTaskQuery();
        List<AuditTask> tasks = queryBuilder.taskId(taskTestData[4].getId()).build().getResultList();
        assertEquals(1, tasks.size());

        queryBuilder.clear();
        
        List<AuditTask> data = this.auditTaskQuery().build().getResultList(); 
        assertEquals(10, data.size());
        
        this.clear();
        
        data = this.auditTaskQuery().build().getResultList();       
        assertEquals(0, data.size());
    }
    
    @Test
    public void testDeleteAuditTaskInfoLogByTimestamp() { 
        List<AuditTask> tasks = this.auditTaskQuery().taskId(taskTestData[4].getId()).build().getResultList();
        assertEquals(1, tasks.size());
        
        AuditTaskDeleteBuilder updateBuilder = this.auditTaskDelete().date(tasks.get(0).getCreatedOn());
        int result = updateBuilder.build().execute();
        assertEquals(1, result);
    }
      
    private ProcessInstanceLog buildCompletedProcessInstance(long processInstanceId) {
        ProcessInstanceLog pil = new ProcessInstanceLog(processInstanceId, "test");
        pil.setDuration(0L);
        pil.setExternalId("none");
        pil.setIdentity("none");
        pil.setOutcome("");
        pil.setParentProcessInstanceId(-1L);
        pil.setProcessId("test");
        pil.setProcessName("test process");
        pil.setProcessVersion("1");
        pil.setStatus(2);
                
        pil.setStart(null);        
        pil.setEnd(null);
        
        return pil;
    }
}