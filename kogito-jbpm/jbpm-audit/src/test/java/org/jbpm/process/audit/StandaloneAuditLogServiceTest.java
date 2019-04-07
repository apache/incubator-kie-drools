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

package org.jbpm.process.audit;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.jbpm.process.audit.command.AuditCommand;
import org.jbpm.process.audit.command.ClearHistoryLogsCommand;
import org.jbpm.process.audit.command.FindNodeInstancesCommand;
import org.jbpm.process.audit.command.FindProcessInstancesCommand;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneAuditLogServiceTest extends AbstractAuditLogServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneAuditLogServiceTest.class);
    private HashMap<String, Object> context;
   
    private AuditLogService auditLogService;
    private KieSession ksession;
    
    @Before
    public void setUp() throws Exception {
        // persistence
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        
        // create a new session
        Environment env = createEnvironment(context);
        KieBase kbase = createKnowledgeBase();
        ksession = createKieSession(kbase, env);
        new JPAWorkingMemoryDbLogger(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
       
        // log service
        auditLogService = new JPAAuditLogService(env);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }
    
    private <T> T setAuditLogServiceAndExecute(AuditCommand<T> cmd) { 
       cmd.setAuditLogService(auditLogService); 
       return cmd.execute(null);
    }

    // TESTS ----------------------------------------------------------------------------------------------------------------------
    
    @Test
    public void setAuditLogServiceForCommandTest() { 
        String PROCESS_ID = "com.sample.ruleflow";
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = setAuditLogServiceAndExecute(new FindProcessInstancesCommand(PROCESS_ID));
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        long processInstanceId = ksession.startProcess(PROCESS_ID).getId();
        
        logger.debug("Checking process instances for process '{}'", PROCESS_ID);
        processInstances = setAuditLogServiceAndExecute(new FindProcessInstancesCommand(PROCESS_ID));
        assertEquals(initialProcessInstanceSize + 1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        
        logger.debug( "{} -> {} - {}",processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        
        assertNotNull(processInstance.getStart());
        assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        assertEquals(PROCESS_ID, processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = setAuditLogServiceAndExecute(new FindNodeInstancesCommand(processInstanceId));
        assertEquals(6, nodeInstances.size()); 
        
        setAuditLogServiceAndExecute(new ClearHistoryLogsCommand());
        nodeInstances = setAuditLogServiceAndExecute(new FindNodeInstancesCommand(processInstanceId));
        assertEquals(0, nodeInstances.size());
        processInstances = setAuditLogServiceAndExecute(new FindProcessInstancesCommand(PROCESS_ID));
        assertEquals(0, processInstances.size());
    }
}
