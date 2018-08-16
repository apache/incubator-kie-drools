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

package org.jbpm.bpmn2.persistence;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.bpmn2.concurrency.MultipleProcessesPerThreadTest;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnmarshallingOverdueTimersTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(MultipleProcessesPerThreadTest.class);

    private HashMap<String, Object> context;

    @Before
    public void setup() {
         context = PersistenceUtil.setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    private static KieBase loadKnowledgeBase(String bpmn2FileName) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(bpmn2FileName, UnmarshallingOverdueTimersTest.class), ResourceType.BPMN2);
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    private KieSession createStatefulKnowledgeSession(KieBase kbase) {
        Environment env = createEnvironment(context);
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }

    private static long knowledgeSessionDispose(KieSession ksession) {
        long ksessionId = ksession.getIdentifier();
        logger.debug("disposing of ksesssion");
        ksession.dispose();
        return ksessionId;
    }

    private KieSession reloadStatefulKnowledgeSession(String bpmn2FileName, int ksessionId) {
        KieBase kbase = loadKnowledgeBase(bpmn2FileName);

        logger.debug("reloading ksession {}", ksessionId);
        Environment env = null;
        env = createEnvironment(context);

        return JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, env);
    }

    private static long seconds = 2;
    private static String timeUnit = "s";
    private static String bpmn2FileName = "BPMN2-TimerInterrupted.bpmn2";

    private static boolean debug = true;
    
    @Test(timeout=10000)
    public void startDisposeAndReloadTimerProcess() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);
        if( debug ) { 
            String shellVar = "TEST";
            String shellVarVal = System.getenv(shellVar);
            if( shellVarVal != null ) { 
                debug = false;
            }
        }
        
        String sessionPropName = "KSESSION_ID";
        String sessionPropVal = System.getenv(sessionPropName);
        String processPropName = "PROCESS_ID";
        String processPropVal = System.getenv(sessionPropName);
        
        if (sessionPropVal == null || debug ) {
            KieBase kbase = loadKnowledgeBase(bpmn2FileName);
            KieSession ksession = createStatefulKnowledgeSession(kbase);
            ksession.addEventListener(countDownListener);
            // setup parameters
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("time", seconds + timeUnit);

            // note process start time
            Calendar cal = GregorianCalendar.getInstance();
            
            // start process
            ProcessInstance processInstance = ksession.startProcess("interruptedTimer", params);
            long processId = processInstance.getId();
            // print info for next test
            if( debug ) { 
                processPropVal = Long.toString(processId);
            }
            else { 
                logger.info("export {}={}", processPropName, processId );
            }

            // dispose of session 
            KieSessionConfiguration config = ksession.getSessionConfiguration();
            long ksessionId = knowledgeSessionDispose(ksession);
            
            // print info for next test
            if( debug ) { 
                sessionPropVal = Long.toString(ksessionId);
            }
            else { 
                logger.info("export {}={}", sessionPropName, ksessionId );
                
            }
            
            if( !debug ) { 
                cal.add(Calendar.SECOND, (int) seconds);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                logger.info("Please wait at least {} [{}]", (seconds + timeUnit),  sdf.format(cal.getTime()));
            }
        } 
       
        
        if( sessionPropVal != null || debug ) {
            // reload session
            int ksessionId = Integer.parseInt(sessionPropVal);
            KieSession ksession = reloadStatefulKnowledgeSession(bpmn2FileName, ksessionId);
            ksession.addEventListener(countDownListener);
            long processInstanceId = Integer.parseInt(processPropVal);

            logger.debug("! waiting 5 seconds for timer to fire");
            countDownListener.waitTillCompleted();
            
            ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
            if( processInstance != null ) { 
                assertTrue("Process has not terminated.", processInstance.getState() == ProcessInstance.STATE_COMPLETED );
            }
        }
    }
}
