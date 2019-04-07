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

package org.jbpm.memory;

import org.drools.core.event.KieBaseEventSupport;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.persistence.PersistableRunner;
import org.drools.persistence.jpa.processinstance.JPAWorkItemManagerFactory;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static org.jbpm.persistence.util.PersistenceUtil.*;
import static org.junit.Assert.*;

public class MemoryLeakTest {

    private static final Logger logger = LoggerFactory.getLogger(MemoryLeakTest.class);

    private static HashMap<String, Object> testContext;
    private Environment env = null;

    private static final String PROCESS_NAME = "RuleTaskWithProcessInstance";
    
    @BeforeClass
    public static void beforeClass() {
        testContext = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
    }

    @AfterClass
    public static void afterClass() {
        cleanUp(testContext);
    }

    @Before
    public void before() {
        env = createEnvironment(testContext);
    }

    @Test
    public void findEventSupportRegisteredInstancesTest() {
        // setup
        KieBase kbase = createKnowledgeBase();
        
        for( int i = 0; i < 3; ++i ) { 
            createKnowledgeSessionStartProcessEtc(kbase);
        }
        
        KieBaseEventSupport eventSupport = (KieBaseEventSupport) getValueOfField("eventSupport", KnowledgeBaseImpl.class, kbase);
        assertEquals( "Event listeners should have been detached", 0, eventSupport.getEventListeners().size());
    }
    
    private KieBase createKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("memory/BPMN2-RuleTaskWithInsertProcessInstance.bpmn2"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("memory/ProcessInstanceRule.drl"), ResourceType.DRL);
        
        if (!kbuilder.getErrors().isEmpty()) {
            Iterator<KnowledgeBuilderError> errIter = kbuilder.getErrors().iterator();
            while( errIter.hasNext() ) { 
                KnowledgeBuilderError err = errIter.next();
                StringBuilder lines = new StringBuilder("");
                if( err.getLines().length > 0 ) { 
                    lines.append(err.getLines()[0]);
                    for( int i = 1; i < err.getLines().length; ++i ) { 
                        lines.append(", " + err.getLines()[i]);
                    }
                }
                logger.warn( err.getMessage() + " (" + lines.toString() + ")" );
            }
            throw new IllegalArgumentException("Errors while parsing knowledge base");
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        
        return kbase;
    }

    private void createKnowledgeSessionStartProcessEtc(KieBase kbase) { 
        logger.info("session count=" + kbase.getKieSessions().size());
        
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, getKnowledgeSessionConfiguration(), env);
        addEventListenersToSession(ksession);
        
        /**
         * The following log line caused the memory leak. 
         * The specific (reverse-ordered) stack trace is the following: 
         * 
         *   MemoryLeakTest.createKnowledgeSessionStartProcessEtc(KnowledgeBase) calls kbase.getKieSessions()
         *   ..
         *   KnowledgeBaseImpl.getStatefulKnowledgeSessions() line: 186  
         *   StatefulKnowledgeSessionImpl.<init>(ReteooWorkingMemory, KnowledgeBase) line: 121   
         *   ReteooStatefulSession(AbstractWorkingMemory).setKnowledgeRuntime(InternalKnowledgeRuntime) line: 1268   
         *   ReteooStatefulSession(AbstractWorkingMemory).createProcessRuntime() line: 342   
         *   ProcessRuntimeFactory.newProcessRuntime(AbstractWorkingMemory) line: 12 
         *   ProcessRuntimeFactoryServiceImpl.newProcessRuntime(AbstractWorkingMemory) line: 1   
         *   ProcessRuntimeFactoryServiceImpl.newProcessRuntime(AbstractWorkingMemory) line: 10  
         *   ProcessRuntimeImpl.<init>(AbstractWorkingMemory) line: 84   
         *   ProcessRuntimeImpl.initProcessEventListeners() line: 215 
         *   
         * And ProcessRuntimeImpl.initProcessEventListeners() is what adds a new listener
         * to AbstractRuleBase.eventSupport.listeners via this line (235): 
         *   kruntime.getKnowledgeBase().addEventListener(knowledgeBaseListener);
         * 
         * The StatefulKnowledgeSessionImpl instance created in this .getStatefulKnowledgeSessions() 
         * method is obviously never disposed, which means that the listener is never removed. 
         * The listener then contains a link to a field (signalManager) of the ProcessRuntimeImpl, 
         * which contains a link to the StatefulKnowledgeSessionImpl instance created here. etc.. 
         */
        logger.info("session count=" + kbase.getKieSessions().size());
        
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
       
        try { 
            // create process instance, insert into session and start process
            Map<String, Object> processParams = new HashMap<String, Object>();
            String [] fireballVarHolder = new String[1];
            processParams.put("fireball", fireballVarHolder);
            ProcessInstance processInstance = ksession.createProcessInstance(PROCESS_NAME, processParams);
            ksession.insert(processInstance);
            ksession.startProcessInstance(processInstance.getId());
    
            // after the log line has been added, the DefaultProcessEventListener registered
            //  in the addEventListenersToSession() method no longer works?!?
            ksession.fireAllRules();
            
            // test process variables
            String [] procVar = (String []) ((WorkflowProcessInstance) processInstance).getVariable("fireball");
            assertEquals( "Rule task did NOT fire or complete.", "boom!", procVar[0] );
    
            // complete task and process
            Map<String, Object> results = new HashMap<String, Object>();
            results.put( "chaerg", new SerializableResult("zhrini", 302l, "F", "A", "T"));
            ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), results);
            
            assertNull( ksession.getProcessInstance(processInstance.getId()));
        } finally {
            // This should clean up all listeners, but doesn't -> see docs above
            ksession.dispose();
        }
        
    }

    private KieSessionConfiguration getKnowledgeSessionConfiguration() {
        Properties ksessionProperties;
        ksessionProperties = new Properties();
        ksessionProperties.put("drools.commandService", PersistableRunner.class.getName() );
        ksessionProperties.put("drools.processInstanceManagerFactory",
                "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        ksessionProperties.setProperty("drools.workItemManagerFactory", JPAWorkItemManagerFactory.class.getName());
        ksessionProperties
                .put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        return KnowledgeBaseFactory.newKnowledgeSessionConfiguration(ksessionProperties);
    }

    private void addEventListenersToSession(StatefulKnowledgeSession session) {
        session.addEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterProcessStarted(ProcessStartedEvent event) {
                logger.info(">>> Firing All the Rules after process started! " + event);
                ((StatefulKnowledgeSession) event.getKieRuntime()).fireAllRules();
            }
        });

    }
    
    private Object getValueOfField(String fieldname, Class<?> sourceClass, Object source ) {
        String sourceClassName = sourceClass.getName();
    
        Field field = null;
        try {
            field = sourceClass.getDeclaredField(fieldname);
            field.setAccessible(true);
        } catch (SecurityException e) {
            fail("Unable to retrieve " + fieldname + " field from " + sourceClassName + ": " + e.getCause());
        } catch (NoSuchFieldException e) {
            fail("Unable to retrieve " + fieldname + " field from " + sourceClassName + ": " + e.getCause());
        }
    
        assertNotNull("." + fieldname + " field is null!?!", field);
        Object fieldValue = null;
        try {
            fieldValue = field.get(source);
        } catch (IllegalArgumentException e) {
            fail("Unable to retrieve value of " + fieldname + " from " + sourceClassName + ": " + e.getCause());
        } catch (IllegalAccessException e) {
            fail("Unable to retrieve value of " + fieldname + " from " + sourceClassName + ": " + e.getCause());
        }
        return fieldValue;
    }

}
