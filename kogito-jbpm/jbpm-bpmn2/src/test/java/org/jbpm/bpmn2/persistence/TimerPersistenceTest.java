/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.bpmn2.persistence;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class TimerPersistenceTest {
    
    private PoolingDataSource ds = new PoolingDataSource();
    
    
    @Before
    public void setUp() {
        ds.setUniqueName("jdbc/testDS1");
        ds.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds.setMaxPoolSize(3);
        ds.setAllowLocalTransactions(true);
        ds.getDriverProperties().put("user", "sa");
        ds.getDriverProperties().put("password", "sasa");
        ds.getDriverProperties().put("URL", "jdbc:h2:mem:mydb");
        ds.init();
    }
    
    @After
    public void tearDown() {
        ds.close();
    }

    @Test
    public void testTimerBoundaryEventCycleISO() throws Exception {
        // load up the knowledge base
        KnowledgeBase kbase = readKnowledgeBase("BPMN2-TimerBoundaryEventCycleISO.bpmn2");

        Environment env = EnvironmentFactory.newEnvironment();
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("org.jbpm.persistence.jpa");
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);

        StatefulKnowledgeSession ksession = JPAKnowledgeService
                .newStatefulKnowledgeSession(kbase, null, env);

        final List<Long> list = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("TimerEvent")) {
                    list.add(event.getProcessInstance().getId());
                }
            }

        };
        ksession.addEventListener(listener);
        int sessionId = ksession.getId();
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertEquals(processInstance.getState(), ProcessInstance.STATE_ACTIVE);

        Thread.sleep(1000);
        assertEquals(processInstance.getState(), ProcessInstance.STATE_ACTIVE);
        System.out.println("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        ksession.addEventListener(listener);
        Thread.sleep(1000);
        assertEquals(processInstance.getState(), ProcessInstance.STATE_ACTIVE);
        Thread.sleep(2000);
        assertEquals(processInstance.getState(), ProcessInstance.STATE_ACTIVE);
        ksession.abortProcessInstance(processInstance.getId());
        Thread.sleep(1000);
        ksession.dispose();
        emf.close();
        assertEquals(2, list.size());
    }
    
    private KnowledgeBase readKnowledgeBase(String process) throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2);

        return kbuilder.newKnowledgeBase();
    }
    
}