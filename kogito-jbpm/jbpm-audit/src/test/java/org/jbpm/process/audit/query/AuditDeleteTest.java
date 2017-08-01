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

package org.jbpm.process.audit.query;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertEquals;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.AuditLogServiceTest;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.junit.After;
//import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogDeleteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditDeleteTest extends JPAAuditLogService {
    
    private static HashMap<String, Object> context;
    private static EntityManagerFactory emf;

    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceTest.class);
   
    private ProcessInstanceLog [] pilTestData;
    private VariableInstanceLog [] vilTestData;
    private NodeInstanceLog [] nilTestData;

    private boolean firstRun = true;
    
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
    	context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        this.persistenceStrategy = new StandaloneJtaStrategy(emf);

        if (firstRun) {
            clearTables(ProcessInstanceLog.class, VariableInstanceLog.class, NodeInstanceLog.class);
            firstRun = false;
        }
        if (pilTestData == null) {
            pilTestData = createTestProcessInstanceLogData();
            vilTestData = createTestVariableInstanceLogData();
            nilTestData = createTestNodeInstanceLogData();
        }
    }
    
    @After
    public void cleanup() {
        clearTables(ProcessInstanceLog.class, VariableInstanceLog.class, NodeInstanceLog.class);
        cleanUp(context);
    }
   
    private static Random random = new Random();

    private long randomLong() { 
        long result = (long) Math.abs(random.nextInt());
        while( result == 23l ) { 
           result = (long) Math.abs(random.nextInt());
        }
        return result;
    }

    private String randomString() { 
        return UUID.randomUUID().toString();
    }

    private Calendar randomCal() { 
        Calendar cal = GregorianCalendar.getInstance();
        cal.roll(Calendar.DAY_OF_YEAR, -1*random.nextInt(10*365));
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        return cal;
    }
    
    private ProcessInstanceLog [] createTestProcessInstanceLogData() { 
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();
   
        int numEntities = 11;
        ProcessInstanceLog [] testData = new ProcessInstanceLog[numEntities];
        
        Calendar cal = randomCal();
        
        for( int i = 0; i < numEntities; ++i ) { 
            ProcessInstanceLog pil = new ProcessInstanceLog(randomLong(), randomString());
            pil.setDuration(randomLong());
            pil.setExternalId(randomString());
            pil.setIdentity(randomString());
            pil.setOutcome(randomString());
            pil.setParentProcessInstanceId(randomLong());
            pil.setProcessId(randomString());
            pil.setProcessName(randomString());
            pil.setProcessVersion(randomString());
            pil.setStatus(random.nextInt());
            
            cal.add(Calendar.MINUTE, 1);
            pil.setStart(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
            pil.setEnd(cal.getTime());
            
            testData[i] = pil; 
        }
    
        for( int i = 0; i < numEntities; ++i ) { 
           switch(i) { 
           case 1:
               testData[i-1].setDuration(testData[i].getDuration());
               break;
           case 2:
               testData[i-1].setEnd(testData[i].getEnd());
               break;
           case 3:
               testData[i-1].setIdentity(testData[i].getIdentity());
               break;
           case 4:
               testData[i-1].setProcessId(testData[i].getProcessId());
               break;
           case 5:
               testData[i-1].setProcessInstanceId(testData[i].getProcessInstanceId());
               break;
           case 6:
               testData[i-1].setProcessName(testData[i].getProcessName());
               break;
           case 7:
               testData[i-1].setProcessVersion(testData[i].getProcessVersion());
               break;
           case 8:
               testData[i-1].setStart(testData[i].getStart());
               break;
           case 9:
               testData[i-1].setStatus(testData[i].getStatus());
               break;
           case 10:
               testData[i-1].setOutcome(testData[i].getOutcome());
               break;
           }
        }
        Object tx = jtaHelper.joinTransaction(em);
        for( int i = 0; i < numEntities; ++i ) {
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        
        return testData;
    }
    
    private NodeInstanceLog [] createTestNodeInstanceLogData() { 
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();
    
        int numEntities = 9;
        NodeInstanceLog [] testData = new NodeInstanceLog[numEntities];
        ProcessInstanceLog [] testDataPI = new ProcessInstanceLog[numEntities];
        
        Calendar cal = randomCal();
    
        for( int i = 0; i < numEntities; ++i ) { 
            NodeInstanceLog nil = new NodeInstanceLog();
            nil.setProcessInstanceId(randomLong());
            nil.setProcessId(randomString());
            cal.add(Calendar.SECOND, 1);
            nil.setDate(cal.getTime());
            nil.setType(Math.abs(random.nextInt()));
            nil.setNodeInstanceId(randomString());
            nil.setNodeId(randomString());
            nil.setNodeName(randomString());
            nil.setNodeType(randomString());
            nil.setWorkItemId(randomLong());
            nil.setConnection(randomString());
            nil.setExternalId(randomString());
            
            testData[i] = nil; 
            
            ProcessInstanceLog pLog = buildCompletedProcessInstance(nil.getProcessInstanceId());
            testDataPI[i] = pLog;
        }
    
        for( int i = 0; i < numEntities; ++i ) { 
           switch(i) { 
           case 1:
               testData[i-1].setDate(testData[i].getDate());
               break;
           case 2:
               testData[i-1].setNodeId(testData[i].getNodeId());
               break;
           case 3:
               testData[i-1].setNodeInstanceId(testData[i].getNodeInstanceId());
               break;
           case 4:
               testData[i-1].setNodeName(testData[i].getNodeName());
               break;
           case 5:
               testData[i-1].setNodeType(testData[i].getNodeType());
               break;
           case 6:
               testData[i-1].setProcessId(testData[i].getProcessId());
               break;
           case 7:
               testData[i-1].setProcessInstanceId(testData[i].getProcessInstanceId());
               break;
           case 8:
               testData[i-1].setWorkItemId(testData[i].getWorkItemId());
               break;
           }
        }
        Object tx = jtaHelper.joinTransaction(em);
        for( int i = 0; i < numEntities; ++i ) {
            em.persist(testDataPI[i]);
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        
        return testData;
    }
    
    private VariableInstanceLog [] createTestVariableInstanceLogData() { 
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();
    
        int numEntities = 8;
        VariableInstanceLog [] testData = new VariableInstanceLog[numEntities];
        ProcessInstanceLog [] testDataPI = new ProcessInstanceLog[numEntities];
       
        Calendar cal = randomCal();
        
        for( int i = 0; i < numEntities; ++i ) { 
            VariableInstanceLog vil = new VariableInstanceLog();
            vil.setProcessInstanceId(randomLong());
            vil.setProcessId(randomString());
            cal.add(Calendar.MINUTE, 1);
            vil.setDate(cal.getTime());
            vil.setVariableInstanceId(randomString());
            vil.setVariableId(randomString());
            vil.setValue(randomString());
            vil.setOldValue(randomString());
            vil.setExternalId(randomString());
            
            testData[i] = vil; 
            
            ProcessInstanceLog pLog = buildCompletedProcessInstance(vil.getProcessInstanceId());
            testDataPI[i] = pLog;
        }
    
        for( int i = 0; i < numEntities; ++i ) { 
           switch(i) { 
           case 1:
               testData[i-1].setDate(testData[i].getDate());
               break;
           case 2:
               testData[i-1].setOldValue(testData[i].getOldValue());
               break;
           case 3:
               testData[i-1].setProcessId(testData[i].getProcessId());
               break;
           case 4:
               testData[i-1].setProcessInstanceId(testData[i].getProcessInstanceId());
               break;
           case 5:
               testData[i-1].setValue(testData[i].getValue());
               break;
           case 6:
               testData[i-1].setVariableId(testData[i].getVariableId());
               break;
           case 7:
               testData[i-1].setVariableInstanceId(testData[i].getVariableInstanceId());
               break;
           }
        }
        Object tx = jtaHelper.joinTransaction(em);
        for( int i = 0; i < numEntities; ++i ) {
            em.persist(testDataPI[i]);
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        
        return testData;
    }
    
    private ProcessInstanceLog buildCompletedProcessInstance(long processInstanceId) {
        ProcessInstanceLog pil = new ProcessInstanceLog(processInstanceId, randomString());
        pil.setDuration(randomLong());
        pil.setExternalId(randomString());
        pil.setIdentity(randomString());
        pil.setOutcome(randomString());
        pil.setParentProcessInstanceId(randomLong());
        pil.setProcessId(randomString());
        pil.setProcessName(randomString());
        pil.setProcessVersion(randomString());
        pil.setStatus(2);
                
        pil.setStart(null);        
        pil.setEnd(null);
        
        return pil;
    }

    @Test
    public void testDeleteProcessInstanceInfoLogByProcessId() { 
        int p = 0;
        String processId = pilTestData[p++].getProcessId();
        String processId2 = pilTestData[p++].getProcessId();
        
        ProcessInstanceLogDeleteBuilder updateBuilder = this.processInstanceLogDelete().processId(processId, processId2);
        int result = updateBuilder.build().execute();
        assertEquals(2, result);
    }
    
    @Test
    public void testDeleteProcessInstanceInfoLogByDate() { 
        int p = 0;        
        Date endDate = pilTestData[p++].getEnd();
        
        ProcessInstanceLogDeleteBuilder updateBuilder = this.processInstanceLogDelete().endDate(endDate);
        int result = updateBuilder.build().execute();
        assertEquals(1, result);
    }
    
    @Test
    public void testDeleteProcessInstanceInfoLogByTimestamp() { 
        int p = 0;        
        Date endDate = pilTestData[p++].getEnd();
        
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> logs = this.processInstanceLogQuery().endDate(endDate).build().getResultList();
        assertEquals(1, logs.size());
        
        ProcessInstanceLogDeleteBuilder updateBuilder = this.processInstanceLogDelete().endDate(logs.get(0).getEnd());
        int result = updateBuilder.build().execute();
        assertEquals(1, result);
    }
    
    @Test
    public void testDeleteProcessInstanceInfoLogByProcessIdAndDate() { 
        int p = 0;     
        String processId = pilTestData[p].getProcessId();
        Date endDate = pilTestData[p].getEnd();
        
        ProcessInstanceLogDeleteBuilder updateBuilder = this.processInstanceLogDelete().endDate(endDate).processId(processId);
        int result = updateBuilder.build().execute();
        assertEquals(1, result);
    }
    
    @Test
    public void testDeleteProcessInstanceInfoLogByProcessIdAndNotMatchingDate() { 
        int p = 0;     
        String processId = pilTestData[p++].getProcessId();
        Date endDate = pilTestData[p++].getEnd();
        
        ProcessInstanceLogDeleteBuilder updateBuilder = this.processInstanceLogDelete().endDate(endDate).processId(processId);
        int result = updateBuilder.build().execute();
        assertEquals(0, result);
    }
    
    @Test
    public void testDeleteProcessInstanceInfoLogByStatus() { 
        int status = pilTestData[5].getStatus();
        ProcessInstanceLogDeleteBuilder updateBuilder = this.processInstanceLogDelete().status(status);
        int result = updateBuilder.build().execute();
        assertEquals(1, result);
    }
    
    @Test
    public void testDeleteProcessInstanceInfoLogByDateRangeEnd() { 
        
        Date endDate = pilTestData[4].getEnd();
        
        ProcessInstanceLogDeleteBuilder updateBuilder = this.processInstanceLogDelete().endDateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        assertEquals(5, result);
    }
    
    @Test
    public void testDeleteProcessInstanceInfoLogByDateRangeStart() { 
        
        Date endDate = pilTestData[8].getEnd();
        ProcessInstanceLogDeleteBuilder updateBuilder = this.processInstanceLogDelete().endDateRangeStart(endDate);
        int result = updateBuilder.build().execute();
        assertEquals(3, result);
    }
    
    @Test
    public void testDeleteNodeInstanceInfoLogByProcessId() { 
        int p = 0;
        String processId = nilTestData[p++].getProcessId();
        
        NodeInstanceLogDeleteBuilder updateBuilder = this.nodeInstanceLogDelete().processId(processId);
        int result = updateBuilder.build().execute();
        assertEquals(1, result);
    }
    
    @Test
    public void testDeleteNodeInstanceInfoLogByDate() { 
        int p = 0;
        Date date = nilTestData[p++].getDate();       
        
        NodeInstanceLogDeleteBuilder updateBuilder = this.nodeInstanceLogDelete().date(date);
        int result = updateBuilder.build().execute();
        assertEquals(2, result);
    }
    
    @Test
    public void testDeleteNodeInstanceInfoLogByDateRangeEnd() { 
        
        Date endDate = nilTestData[4].getDate();
        
        NodeInstanceLogDeleteBuilder updateBuilder = this.nodeInstanceLogDelete().dateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        assertEquals(5, result);
    }
    
    @Test
    public void testDeleteNodeInstanceInfoLogByTimestamp() { 
        int p = 0;
        Date date = nilTestData[p++].getDate();   
        
        List<org.kie.api.runtime.manager.audit.NodeInstanceLog> logs = this.nodeInstanceLogQuery().date(date).build().getResultList();
        assertEquals(2, logs.size());
        
        
        NodeInstanceLogDeleteBuilder updateBuilder = this.nodeInstanceLogDelete().date(logs.get(0).getDate());
        int result = updateBuilder.build().execute();
        assertEquals(2, result);
    }
    
    @Test
    public void testDeleteVarInstanceInfoLogByProcessId() { 
        int p = 0;
        String processId = vilTestData[p++].getProcessId();     
        
        VariableInstanceLogDeleteBuilder updateBuilder = this.variableInstanceLogDelete().processId(processId);
        int result = updateBuilder.build().execute();
        assertEquals(1, result);
    }
    
    @Test
    public void testDeleteVarInstanceInfoLogByDate() { 
        int p = 0;
        Date date = vilTestData[p++].getDate();       
        
        VariableInstanceLogDeleteBuilder updateBuilder = this.variableInstanceLogDelete().date(date);
        int result = updateBuilder.build().execute();
        assertEquals(2, result);
    }
    
    @Test
    public void testDeleteVarInstanceInfoLogByDateRangeEnd() { 
        
        Date endDate = vilTestData[4].getDate();
        
        VariableInstanceLogDeleteBuilder updateBuilder = this.variableInstanceLogDelete().dateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        assertEquals(5, result);
    }
    
    @Test
    public void testDeleteVarInstanceInfoLogByTimestamp() { 
        int p = 0;
        Date date = vilTestData[p++].getDate();     
        
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> vars = this.variableInstanceLogQuery().date(date).build().getResultList();
        assertEquals(2, vars.size());
        
        VariableInstanceLogDeleteBuilder updateBuilder = this.variableInstanceLogDelete().date(vars.get(0).getDate());
        int result = updateBuilder.build().execute();
        assertEquals(2, result);
    }

    private void clearTables(Class<? extends Serializable>... entities) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        try {
            for (Class<? extends Serializable> entity : entities) {
                em.createQuery("DELETE FROM " + entity.getSimpleName()).executeUpdate();
            }
        } finally {
            closeEntityManager(em, newTx);
        }
    }

}