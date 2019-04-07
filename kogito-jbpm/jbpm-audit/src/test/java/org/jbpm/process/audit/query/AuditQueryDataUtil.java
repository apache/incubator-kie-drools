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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;

public class AuditQueryDataUtil {
    
    private static Random random = new Random();

    private static List<Object> createdEntities = new LinkedList<>();

    static long randomLong() { 
        long result = (long) Math.abs(random.nextInt());
        while( result == 23l ) { 
           result = (long) Math.abs(random.nextInt());
        }
        return result;
    }

    static String randomString() { 
        return UUID.randomUUID().toString();
    }

    static Calendar randomCal() { 
        Calendar cal = GregorianCalendar.getInstance();
        cal.roll(Calendar.DAY_OF_YEAR, -1*random.nextInt(10*365));
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        return cal;
    }
    
    static ProcessInstanceLog [] createTestProcessInstanceLogData(EntityManagerFactory emf) { 
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
            pil.setCorrelationKey(randomString());
            
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

        createdEntities.addAll(Arrays.asList(testData));
        return testData;
    }

    static VariableInstanceLog [] createTestVariableInstanceLogData(EntityManagerFactory emf) { 
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

        createdEntities.addAll(Arrays.asList(testData));
        return testData;
    }

    static NodeInstanceLog [] createTestNodeInstanceLogData(EntityManagerFactory emf) { 
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

        createdEntities.addAll(Arrays.asList(testData));
        return testData;
    }

    static int MAX = 2;
    static int MIN = 1;
    static int BOTH = 0;
    
    static void verifyMaxMinDuration( List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> procInstLogs, int test, long... maxOrMin ) {
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

    static void cleanDB(EntityManagerFactory emf) {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();

        Object tx = jtaHelper.joinTransaction(em);

        for (Object entity : createdEntities) {
            Object mergedEntity = em.merge(entity);
            em.remove(mergedEntity);
        }

        jtaHelper.leaveTransaction(em, tx);
    }
    
}