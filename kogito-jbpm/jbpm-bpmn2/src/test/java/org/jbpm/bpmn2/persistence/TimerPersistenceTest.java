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

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.drools.impl.EnvironmentFactory;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.event.process.DefaultProcessEventListener;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.io.ResourceFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

public class TimerPersistenceTest {
    
    
    private HashMap<String, Object> context;
    private Environment env;
    
    @Before
    public void setUp() {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);

        // load up the knowledge base
        env = EnvironmentFactory.newEnvironment();
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
    }
    
    @After
    public void tearDown() {
        env = null;
        cleanUp(context);
    }

    @Test
    public void testTimerBoundaryEventCycleISO() throws Exception {
        // load up the knowledge base
        KnowledgeBase kbase = readKnowledgeBase("BPMN2-TimerBoundaryEventCycleISO.bpmn2");

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
        assertEquals(2, list.size());
    }
    
    private KnowledgeBase readKnowledgeBase(String process) throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2);

        return kbuilder.newKnowledgeBase();
    }
    
}