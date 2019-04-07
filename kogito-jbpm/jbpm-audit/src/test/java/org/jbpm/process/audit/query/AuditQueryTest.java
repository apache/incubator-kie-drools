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
import static org.jbpm.process.audit.query.AuditQueryDataUtil.BOTH;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.MAX;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.MIN;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.cleanDB;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.createTestNodeInstanceLogData;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.createTestProcessInstanceLogData;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.createTestVariableInstanceLogData;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.randomLong;
import static org.jbpm.process.audit.query.AuditQueryDataUtil.verifyMaxMinDuration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.AuditLogServiceTest;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.runtime.manager.audit.query.AuditLogQueryBuilder.OrderBy;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditQueryTest extends JPAAuditLogService {
    
    private static HashMap<String, Object> context;
    private static EntityManagerFactory emf;

    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceTest.class);
   
    private ProcessInstanceLog [] pilTestData;
    private VariableInstanceLog [] vilTestData;
    private NodeInstanceLog [] nilTestData;
   
    @AfterClass 
    public static void resetLogging() { 
        AbstractBaseTest.reset();
    }
    
    @BeforeClass
    public static void configure() { 
        AbstractBaseTest.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
        LoggingPrintStream.interceptSysOutSysErr();
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        
    }
    
    @AfterClass
    public static void reset() { 
        LoggingPrintStream.resetInterceptSysOutSysErr();
        cleanDB(emf);
        cleanUp(context);
    }

    @Before
    public void setUp() throws Exception {
        if( pilTestData == null ) { 
                pilTestData = createTestProcessInstanceLogData(emf);
                vilTestData = createTestVariableInstanceLogData(emf);
                nilTestData = createTestNodeInstanceLogData(emf);
        }
        this.persistenceStrategy = new StandaloneJtaStrategy(emf);
    }
   
    @Test
    public void simpleProcessInstanceLogQueryBuilderTest() { 
        int p = 0;
        long duration = pilTestData[p++].getDuration();
        ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery().duration(duration);
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList 
            = builder.build().getResultList();
        assertEquals( "duration query result", 2, resultList.size());
     
        {
        Date end = pilTestData[p++].getEnd();
        builder = this.processInstanceLogQuery().endDate(end);
        resultList = builder.build().getResultList();
        assertEquals( "end date query result", 2, resultList.size());
        }
       
        {
        String identity = pilTestData[p++].getIdentity();
        builder = this.processInstanceLogQuery().identity(identity);
        resultList = builder.build().getResultList();
        assertEquals( "identity query result", 2, resultList.size());
        }
       
        {
        String processId = pilTestData[p++].getProcessId();
        builder = this.processInstanceLogQuery().processId(processId);
        resultList = builder.build().getResultList();
        assertEquals( "process id query result", 2, resultList.size());
        }
       
        {
        long processInstanceId = pilTestData[p++].getProcessInstanceId();
        builder = this.processInstanceLogQuery().processInstanceId(processInstanceId);
        resultList = builder.build().getResultList();
        assertEquals( "process instance id query result", 2, resultList.size());
        }
       
        {
        String processName = pilTestData[p++].getProcessName();
        builder = this.processInstanceLogQuery().processName(processName);
        resultList = builder.build().getResultList();
        assertEquals( "process name query result", 2, resultList.size());
        }
        
        {
        String version = pilTestData[p++].getProcessVersion();
        builder = this.processInstanceLogQuery().processVersion(version);
        resultList = builder.build().getResultList();
        assertEquals( "process version query result", 2, resultList.size());
        }
        
        {
        Date start = pilTestData[p++].getStart();
        builder = this.processInstanceLogQuery().startDate(start);
        resultList = builder.build().getResultList();
        assertEquals( "start date query result", 2, resultList.size());
        }
        
        {
        int status = pilTestData[p++].getStatus();
        builder = this.processInstanceLogQuery().status(status);
        resultList = builder.build().getResultList();
        assertEquals( "status query result", 2, resultList.size());
        }
        
        {
        String outcome = pilTestData[p++].getOutcome();
        builder = this.processInstanceLogQuery().outcome(outcome);
        resultList = builder.build().getResultList();
        assertEquals( "outcome query result", 2, resultList.size());
        }
        
        {
        String correlationKey = pilTestData[p++].getCorrelationKey();
        CorrelationKey ck = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey(correlationKey);	
        	
        
        builder = this.processInstanceLogQuery().correlationKey(ck);
        resultList = builder.build().getResultList();
        assertEquals( "identity query result", 1, resultList.size());
        }
    }
    
    @Test
    public void simpleVariableInstanceLogQueryBuilderTest() { 
        int p = 0;
        Date date = vilTestData[p++].getDate();
        VariableInstanceLogQueryBuilder builder = this.variableInstanceLogQuery().date(date);
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> resultList = builder.build().getResultList();
        assertEquals( "date query result", 2, resultList.size());
       
        {
        String oldValue = vilTestData[p++].getOldValue();
        builder = this.variableInstanceLogQuery().oldValue(oldValue);
        resultList = builder.build().getResultList();
        assertEquals( "old value query result", 2, resultList.size());
        }
       
        {
        String processId = vilTestData[p++].getProcessId();
        builder = this.variableInstanceLogQuery().processId(processId);
        resultList = builder.build().getResultList();
        assertEquals( "process id query result", 2, resultList.size());
        }
       
        {
        long processInstanceId = vilTestData[p++].getProcessInstanceId();
        builder = this.variableInstanceLogQuery().processInstanceId(processInstanceId);
        resultList = builder.build().getResultList();
        assertEquals( "process instance id query result", 2, resultList.size());
        }
       
        {
        String value = vilTestData[p++].getValue();
        builder = this.variableInstanceLogQuery().value(value);
        resultList = builder.build().getResultList();
        assertEquals( "value query result", 2, resultList.size());
        }
        
        {
        String variableId = vilTestData[p++].getVariableId();
        builder = this.variableInstanceLogQuery().variableId(variableId);
        resultList = builder.build().getResultList();
        assertEquals( "variable id query result", 2, resultList.size());
        }
        
        {
        String varInstId = vilTestData[p++].getVariableInstanceId();
        builder = this.variableInstanceLogQuery().variableInstanceId(varInstId);
        resultList = builder.build().getResultList();
        assertEquals( "variable instance id query result", 2, resultList.size());
        }
    }
    
    @Test
    public void simpleNodeInstanceLogQueryBuilderTest() { 
        int p = 0;
        Date date = nilTestData[p++].getDate();
        NodeInstanceLogQueryBuilder builder = this.nodeInstanceLogQuery().date(date);
        List<org.kie.api.runtime.manager.audit.NodeInstanceLog> resultList = builder.build().getResultList();
        assertEquals( "date query result", 2, resultList.size());
       
        {
        String nodeId = nilTestData[p++].getNodeId();
        builder = this.nodeInstanceLogQuery().nodeId(nodeId);
        resultList = builder.build().getResultList();
        assertEquals( "node id query result", 2, resultList.size());
        }
        
        {
        String nodeInstId = nilTestData[p++].getNodeInstanceId();
        builder = this.nodeInstanceLogQuery().nodeInstanceId(nodeInstId);
        resultList = builder.build().getResultList();
        assertEquals( "node instance id query result", 2, resultList.size());
        }
        
        {
        String name = nilTestData[p++].getNodeName();
        builder = this.nodeInstanceLogQuery().nodeName(name);
        resultList = builder.build().getResultList();
        assertEquals( "node name query result", 2, resultList.size());
        }
       
        {
        String nodeType = nilTestData[p++].getNodeType();
        builder = this.nodeInstanceLogQuery().nodeType(nodeType);
        resultList = builder.build().getResultList();
        assertEquals( "node type query result", 2, resultList.size());
        }
       
        {
        String processId = nilTestData[p++].getProcessId();
        builder = this.nodeInstanceLogQuery().processId(processId);
        resultList = builder.build().getResultList();
        assertEquals( "process id query result", 2, resultList.size());
        }
       
        {
        long processInstanceId = nilTestData[p++].getProcessInstanceId();
        builder = this.nodeInstanceLogQuery().processInstanceId(processInstanceId);
        resultList = builder.build().getResultList();
        assertEquals( "process instance id query result", 2, resultList.size());
        }
       
        {
        long workItemId = nilTestData[p++].getWorkItemId();
        builder = this.nodeInstanceLogQuery().workItemId(workItemId);
        resultList = builder.build().getResultList();
        assertEquals( "work item id query result", 2, resultList.size());
        }
        
        // pagination
        int maxResults = 5;
        resultList = this.nodeInstanceLogQuery().build().getResultList();
        assertTrue( "Not enough to do pagination test", resultList.size() > maxResults );
        resultList = this.nodeInstanceLogQuery()
                .maxResults(maxResults)
                .ascending(org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder.OrderBy.processInstanceId)
                .build().getResultList();
        assertTrue( "Only expected "  + maxResults + " results, not " + resultList.size(), resultList.size() <= 5 );
        
        int offset = 3;
        List<org.kie.api.runtime.manager.audit.NodeInstanceLog> newResultList = this.nodeInstanceLogQuery()
                .maxResults(maxResults)
                .offset(offset)
                .ascending(org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder.OrderBy.processInstanceId)
                .build().getResultList();
        assertTrue( "Only expected"  + maxResults + " results, not " + newResultList.size(), newResultList.size() <= 5 );
        assertEquals( "Offset should have been " + offset + ": " + resultList.get(offset).getProcessInstanceId() + " != " + newResultList.get(0).getProcessInstanceId(),
                resultList.get(offset).getProcessInstanceId(), newResultList.get(0).getProcessInstanceId() );
    }
    
    @Test
    public void unionQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
       
       builder.duration(pilTestData[4].getDuration());
       
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
       assertEquals( "duration result", 1, resultList.size());
       
       builder.endDate(pilTestData[5].getEnd(), pilTestData[6].getEnd());
       resultList = builder.build().getResultList();
       assertEquals( "union: duration OR end result", 3, resultList.size());
       
       builder.identity(pilTestData[7].getIdentity(), pilTestData[8].getIdentity());
       resultList = builder.build().getResultList();
       assertEquals( "union: duration OR end OR identity result", 5, resultList.size());
    }
    
    @Test
    public void intersectQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
       
       builder.intersect();
       builder.duration(pilTestData[4].getDuration());
       
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
       assertEquals( "duration result", 1, resultList.size());
       
       builder.endDate(pilTestData[5].getEnd());
       resultList = builder.build().getResultList();
       assertEquals( "intersect: duration AND end result", 0, resultList.size());
       
       builder.identity(pilTestData[6].getIdentity());
       resultList = builder.build().getResultList();
       assertEquals( "intersect: duration AND end AND identity result", 0, resultList.size());
    } 
    
    @Test
    public void intersectUnionQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
       
       builder.duration(pilTestData[0].getDuration(), pilTestData[2].getDuration());
       
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
       assertEquals( "duration result", 3, resultList.size());
       
       builder.intersect().endDate(pilTestData[0].getEnd());
       resultList = builder.build().getResultList();
       assertEquals( "intersect: duration AND end result", 1, resultList.size());
       
       builder.union().processId(pilTestData[10].getProcessId());
       resultList = builder.build().getResultList();
       assertEquals( "intersect/union: duration AND end OR processId result", 1 + 1, resultList.size());
    } 
    
    @Test
    public void likeRegexQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
      
       builder.regex();
       boolean parameterFailed = false;
       try { 
           builder.duration(pilTestData[0].getDuration(), pilTestData[2].getDuration());
       } catch( Exception e ) { 
          parameterFailed = true; 
       }
       assertTrue( "adding critera should have failed because of like()", parameterFailed);
     
       String regex = pilTestData[0].getIdentity();
       builder.identity(regex);
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
       assertEquals( "literal regex identity result", 1, resultList.size());
       String externalId = resultList.get(0).getExternalId();
       
       builder = this.processInstanceLogQuery();
       regex = regex.substring(0, regex.length()-1) + ".";
       builder.regex().identity(regex);
       resultList = builder.build().getResultList(); 
       assertEquals( "literal regex identity result", 1, resultList.size());
       assertEquals( externalId, resultList.get(0).getExternalId() );
       
       builder = this.processInstanceLogQuery();
       regex = regex.substring(0, 10) + "*";
       builder.regex().identity(regex);
       resultList = builder.build().getResultList(); 
       assertEquals( "literal regex identity result", 1, resultList.size());
       assertEquals( externalId, resultList.get(0).getExternalId() );
       
       builder = this.processInstanceLogQuery();
       String regex2 = "*" + pilTestData[0].getIdentity().substring(10);
       builder.regex().intersect().identity(regex, regex2);
       resultList = builder.build().getResultList(); 
       assertEquals( "literal regex identity result", 1, resultList.size());
       assertEquals( externalId, resultList.get(0).getExternalId() );
      
       builder = this.processInstanceLogQuery();
       regex2 = "*" + pilTestData[5].getIdentity().substring(10);
       builder.regex().intersect().identity(regex, regex2);
       resultList = builder.build().getResultList(); 
       assertEquals( "literal regex identity result", 0, resultList.size());
      
       builder = this.processInstanceLogQuery();
       builder.regex().union().identity(regex, regex2);
       resultList = builder.build().getResultList(); 
       assertEquals( "literal regex identity result", 2, resultList.size());
       
       builder = this.processInstanceLogQuery();
       builder.regex().union().identity("*");
       resultList = builder.build().getResultList(); 
       assertEquals( "literal regex identity result", this.processInstanceLogQuery().build().getResultList().size(), resultList.size());
    }       

    
    @Test
    public void rangeQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
     
       long duration = pilTestData[5].getDuration();
       builder.intersect().durationMin(duration-1).durationMax(duration+1);
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
       assertEquals( "duration min + max result", 1, resultList.size());
       
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> durationOrderedProcInstLogList = this.processInstanceLogQuery().build().getResultList();
       Collections.sort(durationOrderedProcInstLogList, 
               new Comparator<org.kie.api.runtime.manager.audit.ProcessInstanceLog>() {
                   @Override
                   public int compare( 
                           org.kie.api.runtime.manager.audit.ProcessInstanceLog o1, 
                           org.kie.api.runtime.manager.audit.ProcessInstanceLog o2 ) {
                       return o1.getDuration().compareTo(o2.getDuration());
                   }
               } 
       );
 
       int lastElemIndex = durationOrderedProcInstLogList.size()-1;
       builder = this.processInstanceLogQuery();
       long max = durationOrderedProcInstLogList.get(0).getDuration();
       builder.durationMax(max);
       resultList = builder.build().getResultList();
       verifyMaxMinDuration( resultList, MAX, max );
           
       builder = this.processInstanceLogQuery();
       long min = durationOrderedProcInstLogList.get(lastElemIndex).getDuration();
       builder.durationMin(min);
       resultList = builder.build().getResultList();
       verifyMaxMinDuration(resultList, MIN, min);
           
       // union max and min
       builder = this.processInstanceLogQuery();
       min = durationOrderedProcInstLogList.get(lastElemIndex).getDuration();
       builder.durationMin(min);
       max = durationOrderedProcInstLogList.get(0).getDuration();
       builder.durationMax(max);
       resultList = builder.build().getResultList();
       for( org.kie.api.runtime.manager.audit.ProcessInstanceLog log : resultList ) { 
           long dur = log.getDuration();
           assertTrue( "Duration " + dur + " is neither larger than min + " + min + " nor smaller than max" + max, 
                   dur >= min || dur <= max );
       }
         
       // empty intersection (larger than large min, smaller than small max )
       builder = this.processInstanceLogQuery().intersect();
       min = durationOrderedProcInstLogList.get(lastElemIndex).getDuration();
       builder.durationMin(min);
       max = durationOrderedProcInstLogList.get(0).getDuration();
       builder.durationMax(max);
       resultList = builder.build().getResultList();
       verifyMaxMinDuration(resultList, BOTH, min, max);
       
       builder = this.processInstanceLogQuery().intersect();
       min = durationOrderedProcInstLogList.get(2).getDuration();
       max = durationOrderedProcInstLogList.get(3).getDuration();
       builder.durationMin(min);
       builder.durationMax(max);
       resultList = builder.build().getResultList();
       // there are 2 ProcessInstanceLog's with the same duration
       verifyMaxMinDuration(resultList, BOTH, min, max );
    }

    @Test
    public void orderByQueryBuilderTest() {
        ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();

        builder.ascending(OrderBy.processInstanceId);
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
        for (int i = 1; i < resultList.size(); ++i) {
            ProcessInstanceLog pilB = (ProcessInstanceLog) resultList.get(i);
            ProcessInstanceLog pilA = (ProcessInstanceLog) resultList.get(i - 1);
            assertTrue("order by asc process instance id failed: " + pilA.getProcessInstanceId() + " ? " + pilB.getProcessInstanceId(),
                    pilA.getProcessInstanceId() <= pilB.getProcessInstanceId());
        }

        builder.descending(OrderBy.processInstanceId);
        resultList = builder.build().getResultList();
        for (int i = 1; i < resultList.size(); ++i) {
            ProcessInstanceLog pilB = (ProcessInstanceLog) resultList.get(i);
            ProcessInstanceLog pilA = (ProcessInstanceLog) resultList.get(i - 1);
            assertTrue("order by desc process instance id failed: " + pilA.getProcessInstanceId() + " ? " + pilB.getProcessInstanceId(),
                    pilA.getProcessInstanceId() >= pilB.getProcessInstanceId());
        }

        builder.ascending(OrderBy.processId);
        resultList = builder.build().getResultList();
        for (int i = 1; i < resultList.size(); ++i) {
            ProcessInstanceLog pilA = (ProcessInstanceLog) resultList.get(i - 1);
            ProcessInstanceLog pilB = (ProcessInstanceLog) resultList.get(i);
            assertTrue("order by asc process id failed: " + pilA.getProcessId() + " ? " + pilB.getProcessId(),
                    pilA.getProcessId().compareTo(pilB.getProcessId()) <= 0);
        }

        builder.descending(OrderBy.processId);
        resultList = builder.build().getResultList();
        for (int i = 1; i < resultList.size(); ++i) {
            ProcessInstanceLog pilA = (ProcessInstanceLog) resultList.get(i - 1);
            ProcessInstanceLog pilB = (ProcessInstanceLog) resultList.get(i);
            assertTrue("order by desc process id failed: " + pilA.getProcessId() + " ? " + pilB.getProcessId(),
                    pilA.getProcessId().compareTo(pilB.getProcessId()) >= 0);
        }
    }
   
    @Test
    public void lastVariableTest() throws Exception {
        int numLogs = 10;
        VariableInstanceLog [] testData = new VariableInstanceLog[numLogs];
        Calendar cal = GregorianCalendar.getInstance(); 

        for( int i = 0; i < 5; ++i ) {
            cal.roll(Calendar.SECOND, 1);
            testData[i] = new VariableInstanceLog(23L, "org.lots.of.vars", "inst", "first-var", "val-a", "oldVal-" + i);
            testData[i+5] = new VariableInstanceLog(23L, "org.lots.of.vars", "inst", "second-var", "val-b", "oldVal-" + i);
            testData[i].setDate(cal.getTime());
            testData[i+5].setDate(cal.getTime());
        }

        persistEntities(testData);
       
        VariableInstanceLogQueryBuilder queryBuilder;
        ParametrizedQuery<org.kie.api.runtime.manager.audit.VariableInstanceLog> query ;
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> logs;
       
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder.last().intersect().processInstanceId(23L).build();
        logs = query.getResultList();
        assertEquals("2 logs", 2, logs.size());
        
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder.value("val-a").intersect().last().build();
        logs = query.getResultList();
        assertEquals("Only 1 log expected", 1, logs.size());
        assertEquals("Incorrect variable val", "val-a", logs.get(0).getValue());
        assertEquals("Incorrect variable old val", "oldVal-4", logs.get(0).getOldValue());

        removeEntities(testData);
    }

    @Test
    public void variableValueTest() throws Exception {
        int numLogs = 9;
        VariableInstanceLog [] testData = new VariableInstanceLog[numLogs];
        Calendar cal = GregorianCalendar.getInstance(); 

        String processId =  "org.variable.value";
        for( int i = 0; i < testData.length; ++i ) {
            cal.roll(Calendar.SECOND, 1);
            testData[i] = new VariableInstanceLog(randomLong(), processId, "varInstId", "var-" +i, "val-"+i, "oldVal-" + i);
        }

        persistEntities(testData);
       
        VariableInstanceLogQueryBuilder queryBuilder;
        ParametrizedQuery<org.kie.api.runtime.manager.audit.VariableInstanceLog> query ;
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> logs;
      
        // check
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder.processId(processId).build();
        logs = query.getResultList();
        assertEquals(numLogs + " logs expected", numLogs, logs.size());
        
        // control: don't find any
        queryBuilder = this.variableInstanceLogQuery()
                .intersect()
                .processId(processId);
        query = queryBuilder
                .variableValue("var-1", "val-2")
                .build();
        logs = query.getResultList();
        assertEquals("No logs expected", 0, logs.size());
        
        // control: don't find any
        queryBuilder = this.variableInstanceLogQuery()
                .intersect()
                .processId(processId);
        query = queryBuilder
                .variableValue("var-1", "val-1")
                .variableValue("var-2", "val-2")
                .build();
        logs = query.getResultList();
        assertEquals("No logs expected", 0, logs.size());
        
        // find 1
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder
                .union()
                .variableValue("var-1", "val-1")
                .build();
        logs = query.getResultList();
        assertEquals("1 log expected", 1, logs.size());
        assertEquals("Incorrect variable val", "val-1", logs.get(0).getValue());
        assertEquals("Incorrect variable id", "var-1", logs.get(0).getVariableId());
        
        // find 2
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder
                .union()
                .variableValue("var-2", "val-2")
                .variableValue("var-4", "val-4")
                .build();
        logs = query.getResultList();
        assertEquals("2 log expected", 2, logs.size());
        for( org.kie.api.runtime.manager.audit.VariableInstanceLog varLog : logs ) { 
           String id = varLog.getVariableId().substring("var-".length());
           assertEquals( "variable value", "val-" + id, varLog.getValue());
        }
        
        // regex: find 1
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder
                .like()
                .variableValue("var-2", "val-*")
                .build();
        logs = query.getResultList();
        assertEquals("1 log expected", 1, logs.size());
        assertEquals("Incorrect variable val", "val-2", logs.get(0).getValue());
        assertEquals("Incorrect variable id", "var-2", logs.get(0).getVariableId());
        
        // regex: find 2
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder
                .regex().union()
                .variableValue("var-2", "val-*")
                .variableValue("var-3", "val-*")
                .build();
        logs = query.getResultList();
        assertEquals("2 log expected", 2, logs.size());
        for( org.kie.api.runtime.manager.audit.VariableInstanceLog varLog : logs ) { 
           String id = varLog.getVariableId().substring("var-".length());
           assertEquals( "variable value", "val-" + id, varLog.getValue());
        }
        
        // regex: find 2 with last
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder
                .newGroup()
                .regex().union()
                .variableValue("var-2", "val-*")
                .variableValue("var-3", "val-*")
                .endGroup()
                .equals().intersect()
                .last()
                .build();
        logs = query.getResultList();
        assertEquals("2 log expected", 2, logs.size());
        for( org.kie.api.runtime.manager.audit.VariableInstanceLog varLog : logs ) { 
           String id = varLog.getVariableId().substring("var-".length());
           assertEquals( "variable value", "val-" + id, varLog.getValue());
        }

        removeEntities(testData);
    }

    private void persistEntities(VariableInstanceLog[] testData) {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();

        int numLogs = testData.length;
        Object tx = jtaHelper.joinTransaction(em);
        for( int i = 0; i < numLogs; ++i ) {
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
    }

    private void removeEntities(VariableInstanceLog[] testData) {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();

        int numLogs = testData.length;
        Object tx = jtaHelper.joinTransaction(em);
        for( int i = 0; i < numLogs; ++i ) {
            VariableInstanceLog mergedEntity = em.merge(testData[i]);
            em.remove(mergedEntity);
        }
        jtaHelper.leaveTransaction(em, tx);
    }

}