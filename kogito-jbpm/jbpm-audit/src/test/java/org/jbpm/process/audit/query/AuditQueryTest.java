package org.jbpm.process.audit.query;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.*;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder.OrderBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditQueryTest extends JPAAuditLogService {
    
    private static HashMap<String, Object> context;
    private static EntityManagerFactory emf;

    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceTest.class);
   
    private ProcessInstanceLog [] pilTestData;
    private VariableInstanceLog [] vilTestData;
    private NodeInstanceLog [] nilTestData;
    
    
    @BeforeClass
    public static void configure() { 
        LoggingPrintStream.interceptSysOutSysErr();
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        
    }
    
    @AfterClass
    public static void reset() { 
        LoggingPrintStream.resetInterceptSysOutSysErr();
        cleanUp(context);
    }

    @Before
    public void setUp() throws Exception {
        if( pilTestData == null ) { 
            pilTestData = createTestProcessInstanceLogData();
            vilTestData = createTestVariableInstanceLogData();
            nilTestData = createTestNodeInstanceLogData();
        }
        this.persistenceStrategy = new StandaloneJtaStrategy(emf);
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

    @Test
    public void simpleProcessInstanceLogQueryBuilderTest() { 
        int p = 0;
        long duration = pilTestData[p++].getDuration();
        ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery().duration(duration);
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList 
            = builder.buildQuery().getResultList();
        assertEquals( "duration query result", 2, resultList.size());
     
        {
        Date end = pilTestData[p++].getEnd();
        builder = this.processInstanceLogQuery().endDate(end);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "end date query result", 2, resultList.size());
        }
       
        {
        String identity = pilTestData[p++].getIdentity();
        builder = this.processInstanceLogQuery().identity(identity);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "identity query result", 2, resultList.size());
        }
       
        {
        String processId = pilTestData[p++].getProcessId();
        builder = this.processInstanceLogQuery().processId(processId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "process id query result", 2, resultList.size());
        }
       
        {
        long processInstanceId = pilTestData[p++].getProcessInstanceId();
        builder = this.processInstanceLogQuery().processInstanceId(processInstanceId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "process instance id query result", 2, resultList.size());
        }
       
        {
        String processName = pilTestData[p++].getProcessName();
        builder = this.processInstanceLogQuery().processName(processName);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "process name query result", 2, resultList.size());
        }
        
        {
        String version = pilTestData[p++].getProcessVersion();
        builder = this.processInstanceLogQuery().processVersion(version);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "process version query result", 2, resultList.size());
        }
        
        {
        Date start = pilTestData[p++].getStart();
        builder = this.processInstanceLogQuery().startDate(start);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "start date query result", 2, resultList.size());
        }
        
        {
        int status = pilTestData[p++].getStatus();
        builder = this.processInstanceLogQuery().status(status);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "status query result", 2, resultList.size());
        }
        
        {
        String outcome = pilTestData[p++].getOutcome();
        builder = this.processInstanceLogQuery().outcome(outcome);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "outcome query result", 2, resultList.size());
        }
    }
    
    private VariableInstanceLog [] createTestVariableInstanceLogData() { 
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();
    
        int numEntities = 8;
        VariableInstanceLog [] testData = new VariableInstanceLog[numEntities];
       
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
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        
        return testData;
    }

    @Test
    public void simpleVariableInstanceLogQueryBuilderTest() { 
        int p = 0;
        Date date = vilTestData[p++].getDate();
        VariableInstanceLogQueryBuilder builder = this.variableInstanceLogQuery().date(date);
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> resultList = builder.buildQuery().getResultList();
        assertEquals( "date query result", 2, resultList.size());
       
        {
        String oldValue = vilTestData[p++].getOldValue();
        builder = this.variableInstanceLogQuery().oldValue(oldValue);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "old value query result", 2, resultList.size());
        }
       
        {
        String processId = vilTestData[p++].getProcessId();
        builder = this.variableInstanceLogQuery().processId(processId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "process id query result", 2, resultList.size());
        }
       
        {
        long processInstanceId = vilTestData[p++].getProcessInstanceId();
        builder = this.variableInstanceLogQuery().processInstanceId(processInstanceId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "process instance id query result", 2, resultList.size());
        }
       
        {
        String value = vilTestData[p++].getValue();
        builder = this.variableInstanceLogQuery().value(value);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "value query result", 2, resultList.size());
        }
        
        {
        String variableId = vilTestData[p++].getVariableId();
        builder = this.variableInstanceLogQuery().variableId(variableId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "variable id query result", 2, resultList.size());
        }
        
        {
        String varInstId = vilTestData[p++].getVariableInstanceId();
        builder = this.variableInstanceLogQuery().variableInstanceId(varInstId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "variable instance id query result", 2, resultList.size());
        }
    }
    
    private NodeInstanceLog [] createTestNodeInstanceLogData() { 
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();
    
        int numEntities = 9;
        NodeInstanceLog [] testData = new NodeInstanceLog[numEntities];
        
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
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        
        return testData;
    }

    @Test
    public void simpleNodeInstanceLogQueryBuilderTest() { 
        int p = 0;
        Date date = nilTestData[p++].getDate();
        NodeInstanceLogQueryBuilder builder = this.nodeInstanceLogQuery().date(date);
        List<org.kie.api.runtime.manager.audit.NodeInstanceLog> resultList = builder.buildQuery().getResultList();
        assertEquals( "date query result", 2, resultList.size());
       
        {
        String nodeId = nilTestData[p++].getNodeId();
        builder = this.nodeInstanceLogQuery().nodeId(nodeId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "node id query result", 2, resultList.size());
        }
        
        {
        String nodeInstId = nilTestData[p++].getNodeInstanceId();
        builder = this.nodeInstanceLogQuery().nodeInstanceId(nodeInstId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "node instance id query result", 2, resultList.size());
        }
        
        {
        String name = nilTestData[p++].getNodeName();
        builder = this.nodeInstanceLogQuery().nodeName(name);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "node name query result", 2, resultList.size());
        }
       
        {
        String nodeType = nilTestData[p++].getNodeType();
        builder = this.nodeInstanceLogQuery().nodeType(nodeType);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "node type query result", 2, resultList.size());
        }
       
        {
        String processId = nilTestData[p++].getProcessId();
        builder = this.nodeInstanceLogQuery().processId(processId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "process id query result", 2, resultList.size());
        }
       
        {
        long processInstanceId = nilTestData[p++].getProcessInstanceId();
        builder = this.nodeInstanceLogQuery().processInstanceId(processInstanceId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "process instance id query result", 2, resultList.size());
        }
       
        {
        long workItemId = nilTestData[p++].getWorkItemId();
        builder = this.nodeInstanceLogQuery().workItemId(workItemId);
        resultList = builder.buildQuery().getResultList();
        assertEquals( "work item id query result", 2, resultList.size());
        }
        
        // pagination
        int maxResults = 5;
        resultList = this.nodeInstanceLogQuery().buildQuery().getResultList();
        assertTrue( "Not enough to do pagination test", resultList.size() > maxResults );
        resultList = this.nodeInstanceLogQuery()
                .maxResults(maxResults)
                .orderBy(org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder.OrderBy.processInstanceId)
                .buildQuery().getResultList();
        assertTrue( "Only expected"  + maxResults + " results, not " + resultList.size(), resultList.size() <= 5 );
        
        int offset = 3;
        List<org.kie.api.runtime.manager.audit.NodeInstanceLog> newResultList = this.nodeInstanceLogQuery()
                .maxResults(maxResults)
                .offset(offset)
                .orderBy(org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder.OrderBy.processInstanceId)
                .buildQuery().getResultList();
        assertTrue( "Only expected"  + maxResults + " results, not " + newResultList.size(), newResultList.size() <= 5 );
        assertEquals( "Offset should have been " + offset + ": " + resultList.get(offset).getProcessInstanceId() + " != " + newResultList.get(0).getProcessInstanceId(),
                resultList.get(offset).getProcessInstanceId(), newResultList.get(0).getProcessInstanceId() );
    }
    
    @Test
    public void unionQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
       
       builder.duration(pilTestData[4].getDuration());
       
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.buildQuery().getResultList();
       assertEquals( "duration result", 1, resultList.size());
       
       builder.endDate(pilTestData[5].getEnd(), pilTestData[6].getEnd());
       resultList = builder.buildQuery().getResultList();
       assertEquals( "union: duration OR end result", 3, resultList.size());
       
       builder.identity(pilTestData[7].getIdentity(), pilTestData[8].getIdentity());
       resultList = builder.buildQuery().getResultList();
       assertEquals( "union: duration OR end OR identity result", 5, resultList.size());
    }
    
    @Test
    public void intersectQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
       
       builder.intersect();
       builder.duration(pilTestData[4].getDuration());
       
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.buildQuery().getResultList();
       assertEquals( "duration result", 1, resultList.size());
       
       builder.endDate(pilTestData[5].getEnd());
       resultList = builder.buildQuery().getResultList();
       assertEquals( "intersect: duration AND end result", 0, resultList.size());
       
       builder.identity(pilTestData[6].getIdentity());
       resultList = builder.buildQuery().getResultList();
       assertEquals( "intersect: duration AND end AND identity result", 0, resultList.size());
    } 
    
    @Test
    public void intersectUnionQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
       
       builder.duration(pilTestData[0].getDuration(), pilTestData[2].getDuration());
       builder.intersect();
       
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.buildQuery().getResultList();
       assertEquals( "duration result", 3, resultList.size());
       
       builder.endDate(pilTestData[0].getEnd());
       resultList = builder.buildQuery().getResultList();
       assertEquals( "intersect: duration AND end result", 1, resultList.size());
       
       builder.union();
       builder.processId(pilTestData[10].getProcessId());
       
       resultList = builder.buildQuery().getResultList();
       assertEquals( "intersect/union: duration AND end OR processId result", 1 + 1, resultList.size());
    } 
    
    @Test
    public void likeRegexQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
      
       builder.like();
       boolean parameterFailed = false;
       try { 
           builder.duration(pilTestData[0].getDuration(), pilTestData[2].getDuration());
       } catch( Exception e ) { 
          parameterFailed = true; 
       }
       assertTrue( "adding critera should have failed because of like()", parameterFailed);
     
       String regex = pilTestData[0].getIdentity();
       builder.identity(regex);
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.buildQuery().getResultList();
       assertEquals( "literal regex identity result", 1, resultList.size());
       String externalId = resultList.get(0).getExternalId();
       
       builder = this.processInstanceLogQuery();
       regex = regex.substring(0, regex.length()-1) + ".";
       builder.like().identity(regex);
       resultList = builder.buildQuery().getResultList(); 
       assertEquals( "literal regex identity result", 1, resultList.size());
       assertEquals( externalId, resultList.get(0).getExternalId() );
       
       builder = this.processInstanceLogQuery();
       regex = regex.substring(0, 10) + "*";
       builder.like().identity(regex);
       resultList = builder.buildQuery().getResultList(); 
       assertEquals( "literal regex identity result", 1, resultList.size());
       assertEquals( externalId, resultList.get(0).getExternalId() );
       
       builder = this.processInstanceLogQuery();
       String regex2 = "*" + pilTestData[0].getIdentity().substring(10);
       builder.like().intersect().identity(regex, regex2);
       resultList = builder.buildQuery().getResultList(); 
       assertEquals( "literal regex identity result", 1, resultList.size());
       assertEquals( externalId, resultList.get(0).getExternalId() );
      
       builder = this.processInstanceLogQuery();
       regex2 = "*" + pilTestData[5].getIdentity().substring(10);
       builder.like().intersect().identity(regex, regex2);
       resultList = builder.buildQuery().getResultList(); 
       assertEquals( "literal regex identity result", 0, resultList.size());
      
       builder = this.processInstanceLogQuery();
       builder.like().union().identity(regex, regex2);
       resultList = builder.buildQuery().getResultList(); 
       assertEquals( "literal regex identity result", 2, resultList.size());
       
       builder = this.processInstanceLogQuery();
       builder.like().union().identity("*");
       resultList = builder.buildQuery().getResultList(); 
       assertEquals( "literal regex identity result", this.processInstanceLogQuery().buildQuery().getResultList().size(), resultList.size());
    }       
   
    private static int MAX = 2;
    private static int MIN = 1;
    private static int BOTH = 0;
    
    @Test
    public void rangeQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
     
       long duration = pilTestData[5].getDuration();
       builder.intersect().durationMin(duration-1).durationMax(duration+1);
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.buildQuery().getResultList();
       assertEquals( "duration min + max result", 1, resultList.size());
       
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> durationOrderedProcInstLogList = this.processInstanceLogQuery().buildQuery().getResultList();
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
       resultList = builder.buildQuery().getResultList();
       verifyMaxMinDuration( resultList, MAX, max );
           
       builder = this.processInstanceLogQuery();
       long min = durationOrderedProcInstLogList.get(lastElemIndex).getDuration();
       builder.durationMin(min);
       resultList = builder.buildQuery().getResultList();
       duration = resultList.get(0).getDuration();
       verifyMaxMinDuration(resultList, MIN, min);
           
       // union max and min
       builder = this.processInstanceLogQuery();
       min = durationOrderedProcInstLogList.get(lastElemIndex).getDuration();
       builder.durationMin(min);
       max = durationOrderedProcInstLogList.get(0).getDuration();
       builder.durationMax(max);
       resultList = builder.buildQuery().getResultList();
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
       resultList = builder.buildQuery().getResultList();
       verifyMaxMinDuration(resultList, BOTH, min, max);
       
       builder = this.processInstanceLogQuery().intersect();
       min = durationOrderedProcInstLogList.get(2).getDuration();
       max = durationOrderedProcInstLogList.get(3).getDuration();
       builder.durationMin(min);
       builder.durationMax(max);
       resultList = builder.buildQuery().getResultList();
       // there are 2 ProcessInstanceLog's with the same duration
       verifyMaxMinDuration(resultList, BOTH, min, max );
    }

    private void verifyMaxMinDuration( List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> procInstLogs, int test, long... maxOrMin ) {
       for( org.kie.api.runtime.manager.audit.ProcessInstanceLog log : procInstLogs ) { 
           assertNotNull( "Duration is null" , log.getDuration() );
           long dur = log.getDuration();
           if( test == MAX ) { 
               assertTrue( "Duration " + dur + " is larger than max " + maxOrMin[0] + ": " + dur, dur <= maxOrMin[0] ); 
           } else if( test == MIN ) { 
               assertTrue( "Duration " + dur + " is smaller than min " + maxOrMin[0], dur >= maxOrMin[0] ); 
           } else { // BOTH
               assertTrue( "Duration " + dur + " is smaller than min " + maxOrMin[0], dur >= maxOrMin[0] ); 
               assertTrue( "Duration " + dur + " is larger than max " + maxOrMin[1], dur <= maxOrMin[1] ); 
           }
       }
    }
    
    @Test
    public void orderByQueryBuilderTest() { 
       ProcessInstanceLogQueryBuilder builder = this.processInstanceLogQuery();
       List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.buildQuery().getResultList();
       for( int i = 1; i < resultList.size(); ++i ) { 
          ProcessInstanceLog pilB = (ProcessInstanceLog) resultList.get(i);
          ProcessInstanceLog pilA = (ProcessInstanceLog) resultList.get(i-1);
          assertTrue( pilA.getId() < pilB.getId() );
       }
       
       builder.orderBy(OrderBy.processInstanceId);
       resultList = builder.buildQuery().getResultList();
       for( int i = 1; i < resultList.size(); ++i ) { 
          ProcessInstanceLog pilB = (ProcessInstanceLog) resultList.get(i);
          ProcessInstanceLog pilA = (ProcessInstanceLog) resultList.get(i-1);
          assertTrue( "order by process instance id failed:  " + pilA.getProcessInstanceId() + " ? " +  pilB.getProcessInstanceId(),
                  pilA.getProcessInstanceId() <= pilB.getProcessInstanceId() );
       }
       
       builder.descending();
       resultList = builder.buildQuery().getResultList();
       for( int i = 1; i < resultList.size(); ++i ) { 
          ProcessInstanceLog pilB = (ProcessInstanceLog) resultList.get(i);
          ProcessInstanceLog pilA = (ProcessInstanceLog) resultList.get(i-1);
          assertTrue( "order desc by process instance id failed", pilA.getProcessInstanceId() >= pilB.getProcessInstanceId() );
       }
      
       builder.orderBy(OrderBy.processId).ascending();
       resultList = builder.buildQuery().getResultList();
       for( int i = 1; i < resultList.size(); ++i ) { 
          ProcessInstanceLog pilA = (ProcessInstanceLog) resultList.get(i-1);
          ProcessInstanceLog pilB = (ProcessInstanceLog) resultList.get(i);
          assertTrue( "order desc by process id failed", pilA.getProcessId().compareTo(pilB.getProcessId()) <= 0 );
       }
    }
   
    @Test
    public void lastVariableTest() throws Exception { 
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();

        int numLogs = 10;
        VariableInstanceLog [] testData = new VariableInstanceLog[numLogs];
        Calendar cal = GregorianCalendar.getInstance(); 

        for( int i = 0; i < 5; ++i ) {
            cal.roll(Calendar.SECOND, 1);
            testData[i] = new VariableInstanceLog(23l, "org.lots.of.vars", "inst", "first-var", "val-a", "oldVal-" + i);
            testData[i+5] = new VariableInstanceLog(23l, "org.lots.of.vars", "inst", "second-var", "val-b", "oldVal-" + i);
            testData[i].setDate(cal.getTime());
            testData[i+5].setDate(cal.getTime());
        }
        
        Object tx = jtaHelper.joinTransaction(em);
        for( int i = 0; i < numLogs; ++i ) {
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
       
        VariableInstanceLogQueryBuilder queryBuilder;
        ParametrizedQuery<org.kie.api.runtime.manager.audit.VariableInstanceLog> query ;
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> logs;
       
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder.last().processInstanceId(23l).buildQuery();
        logs = query.getResultList();
        assertEquals("2 logs expected", 2, logs.size());
        
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder.value("val-a").last().buildQuery();
        logs = query.getResultList();
        assertEquals("Only 1 log expected", 1, logs.size());
        assertEquals("Incorrect variable val", "val-a", logs.get(0).getValue());
        assertEquals("Incorrect variable old val", "oldVal-4", logs.get(0).getOldValue());
    } 

    @Test
    public void variableValueTest() throws Exception { 
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();

        int numLogs = 9;
        VariableInstanceLog [] testData = new VariableInstanceLog[numLogs];
        Calendar cal = GregorianCalendar.getInstance(); 

        String processId =  "org.variable.value";
        for( int i = 0; i < testData.length; ++i ) {
            cal.roll(Calendar.SECOND, 1);
            testData[i] = new VariableInstanceLog(randomLong(), processId, "varInstId", "var-" +i, "val-"+i, "oldVal-" + i);
        }
        
        Object tx = jtaHelper.joinTransaction(em);
        for( int i = 0; i < numLogs; ++i ) {
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
       
        VariableInstanceLogQueryBuilder queryBuilder;
        ParametrizedQuery<org.kie.api.runtime.manager.audit.VariableInstanceLog> query ;
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> logs;
      
        // check
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder.processId(processId).buildQuery();
        logs = query.getResultList();
        assertEquals(numLogs + " logs expected", numLogs, logs.size());
        
        // control: don't find any
        queryBuilder = this.variableInstanceLogQuery()
                .intersect()
                .processId(processId);
        query = queryBuilder
                .variableValue("var-1", "val-2")
                .buildQuery();
        logs = query.getResultList();
        assertEquals("No logs expected", 0, logs.size());
        
        // control: don't find any
        queryBuilder = this.variableInstanceLogQuery()
                .intersect()
                .processId(processId);
        query = queryBuilder
                .variableValue("var-1", "val-1")
                .variableValue("var-2", "val-2")
                .buildQuery();
        logs = query.getResultList();
        assertEquals("No logs expected", 0, logs.size());
        
        // find 1
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder
                .union()
                .variableValue("var-1", "val-1")
                .buildQuery();
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
                .buildQuery();
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
                .buildQuery();
        logs = query.getResultList();
        assertEquals("1 log expected", 1, logs.size());
        assertEquals("Incorrect variable val", "val-2", logs.get(0).getValue());
        assertEquals("Incorrect variable id", "var-2", logs.get(0).getVariableId());
        
        // regex: find 2
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder
                .like().union()
                .variableValue("var-2", "val-*")
                .variableValue("var-3", "val-*")
                .buildQuery();
        logs = query.getResultList();
        assertEquals("2 log expected", 2, logs.size());
        for( org.kie.api.runtime.manager.audit.VariableInstanceLog varLog : logs ) { 
           String id = varLog.getVariableId().substring("var-".length());
           assertEquals( "variable value", "val-" + id, varLog.getValue());
        }
        
        // regex: find 2 with last
        queryBuilder = this.variableInstanceLogQuery();
        query = queryBuilder
                .like().union()
                .variableValue("var-2", "val-*")
                .variableValue("var-3", "val-*")
                .equals()
                .last()
                .buildQuery();
        logs = query.getResultList();
        assertEquals("2 log expected", 2, logs.size());
        for( org.kie.api.runtime.manager.audit.VariableInstanceLog varLog : logs ) { 
           String id = varLog.getVariableId().substring("var-".length());
           assertEquals( "variable value", "val-" + id, varLog.getValue());
        }
    } 
}