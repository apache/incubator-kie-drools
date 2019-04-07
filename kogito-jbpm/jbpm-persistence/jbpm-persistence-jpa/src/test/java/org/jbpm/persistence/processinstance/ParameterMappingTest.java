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

package org.jbpm.persistence.processinstance;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

@RunWith(Parameterized.class)
public class ParameterMappingTest extends AbstractBaseTest {
    
    private HashMap<String, Object> context;
    
    private static final String PROCESS_ID = "org.jbpm.processinstance.subprocess";
    private static final String SUBPROCESS_ID = "org.jbpm.processinstance.helloworld";
    private StatefulKnowledgeSession ksession;
    private ProcessListener listener;
    
    public ParameterMappingTest(boolean locking) { 
       this.useLocking = locking; 
    }

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };
    
    @Before
    public void before() {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        Environment env = createEnvironment(context);
        if( useLocking ) { 
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }

        ksession = JPAKnowledgeService.newStatefulKnowledgeSession(createKnowledgeBase(), null, env);
        assertTrue("Valid KnowledgeSession could not be created.", ksession != null && ksession.getIdentifier() > 0);

        listener = new ProcessListener();
        ksession.addEventListener(listener);
    }

    private KieBase createKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/Subprocess.rf"), ResourceType.DRF);
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/HelloWorld.rf"), ResourceType.DRF);
    
        return kbuilder.newKieBase();
    }

    @After
    public void after() {
        if (ksession != null) {
            ksession.dispose();
        }
        cleanUp(context);
    }

    //org.jbpm.processinstance.subprocess
    @Test
    public void testChangingVariableByScript() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "script");
        mapping.put("var", "value");

        ksession.startProcess(PROCESS_ID, mapping);

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEvent() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");

        ksession.startProcess(PROCESS_ID, mapping).getId();
        ksession.signalEvent("pass", "new value");

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEventSignalWithProcessId() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");

        long processId = ksession.startProcess(PROCESS_ID, mapping).getId();
        ksession.signalEvent("pass", "new value", processId);

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testNotChangingVariable() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "default");
        mapping.put("var", "value");

        ksession.startProcess(PROCESS_ID, mapping);

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testNotSettingVariable() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "default");

        ksession.startProcess(PROCESS_ID, mapping);

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    
    public static class ProcessListener extends DefaultProcessEventListener {
        private final List<String> processesStarted = new ArrayList<String>();
        private final List<String> processesCompleted = new ArrayList<String>();

        public void afterProcessStarted(ProcessStartedEvent event) {
            processesStarted.add(event.getProcessInstance().getProcessId());
        }

        public void afterProcessCompleted(ProcessCompletedEvent event) {
            processesCompleted.add(event.getProcessInstance().getProcessId());
        }

        public boolean isProcessStarted(String processId) {
            return processesStarted.contains(processId);
        }

        public boolean isProcessCompleted(String processId) {
            return processesCompleted.contains(processId);
        }
    }
}
