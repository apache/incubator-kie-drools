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

import java.util.*;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.ClassPathResource;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.util.AbstractBaseTest;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tests the following classes: 
 * <ul>
 * <li>JPAWorkingMemoryDbLogger</li>
 * <li>AuditLogService</li>
 * </ul>
 */
public abstract class AbstractAuditLogServiceTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAuditLogServiceTest.class);
   
    public static KieBase createKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("ruleflow.rf"), ResourceType.DRF);
        kbuilder.add(new ClassPathResource("ruleflow2.rf"), ResourceType.DRF);
        kbuilder.add(new ClassPathResource("ruleflow3.rf"), ResourceType.DRF);
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        return kbase;
    }
    
    public static KieSession createKieSession(KieBase kbase, Environment env) { 
        // create a new session
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
       
        return session;
    }

    public static void runTestLogger1(KieSession session, AuditLogService auditLogService) throws Exception {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assertions.assertThat(processInstances.size()).isEqualTo(initialProcessInstanceSize + 1);

        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        
        logger.debug( "{} -> {} - {}",processInstance.toString(), processInstance.getStart(), processInstance.getEnd());

        Assertions.assertThat(processInstance.getStart()).isNotNull();
        Assertions.assertThat(processInstance.getEnd()).isNotNull().withFailMessage("ProcessInstanceLog does not contain end date.");
        Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
        Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow");
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        Assertions.assertThat(nodeInstances.size()).isEqualTo(6);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.debug(nodeInstance.toString());
            Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
            Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow");
            Assertions.assertThat(nodeInstance.getDate()).isNotNull();
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assertions.assertThat(processInstances).isEmpty();
    }
    
    public static void runTestLogger2(KieSession session, AuditLogService auditLogService) {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances =
            auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        session.startProcess("com.sample.ruleflow");
        session.startProcess("com.sample.ruleflow");
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assertions.assertThat(processInstances.size()).isEqualTo(initialProcessInstanceSize + 2);
        for (ProcessInstanceLog processInstance: processInstances) {
            logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
            List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstance.getProcessInstanceId());
            for (NodeInstanceLog nodeInstance: nodeInstances) {
                logger.debug("{} -> {}",nodeInstance.toString(), nodeInstance.getDate());
            }
            Assertions.assertThat(nodeInstances.size()).isEqualTo(6);
        }
        auditLogService.clear();
    }
    
    public static void runTestLogger3(KieSession session, AuditLogService auditLogService) {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow2").getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow2'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow2");
        Assertions.assertThat(processInstances.size()).isEqualTo(initialProcessInstanceSize + 1);
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assertions.assertThat(processInstance.getStart()).isNotNull();
        Assertions.assertThat(processInstance.getEnd()).isNotNull().withFailMessage("ProcessInstanceLog does not contain end date.");
        Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
        Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow2");
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.debug("{} -> {}", nodeInstance.toString(), nodeInstance.getDate());
            Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
            Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow2");
            Assertions.assertThat(nodeInstance.getDate()).isNotNull();
        }
        Assertions.assertThat(nodeInstances.size()).isEqualTo(14);
        auditLogService.clear();
    }
    
    public static void runTestLogger4(KieSession session, AuditLogService auditLogService) throws Exception {
        final List<Long> workItemIds = new ArrayList<Long>();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                workItemIds.add(workItem.getId());
            }
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        params.put("list", list);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();

        // Test findVariableInstancesByName* methods: check for variables (only) in active processes
        List<VariableInstanceLog> varLogs = auditLogService.findVariableInstancesByName("s", true) ;
        Assertions.assertThat(varLogs).isNotEmpty();
        Assertions.assertThat(varLogs.size()).isEqualTo(1);
                
        for( Long workItemId : workItemIds ) { 
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", "ResultValue");
            session.getWorkItemManager().completeWorkItem(workItemId, results);
        }
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assertions.assertThat(processInstances.size()).isEqualTo(initialProcessInstanceSize + 1);
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assertions.assertThat(processInstance.getStart()).isNotNull();
        Assertions.assertThat(processInstance.getEnd()).isNotNull().withFailMessage("ProcessInstanceLog does not contain end date.");
        Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
        Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        Assertions.assertThat(variableInstances.size()).isEqualTo(11);
        for (VariableInstanceLog variableInstance: variableInstances) {
            logger.debug(variableInstance.toString());
            Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
            Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
            Assertions.assertThat(variableInstance.getDate()).isNotNull();
        }
        
        // Test findVariableInstancesByName* methods
        List<VariableInstanceLog> emptyVarLogs = auditLogService.findVariableInstancesByName("s", true) ;
        Assertions.assertThat(emptyVarLogs).isEmpty();
        for( VariableInstanceLog origVarLog : variableInstances ) { 
           varLogs = auditLogService.findVariableInstancesByName(origVarLog.getVariableId(), false) ;
           for( VariableInstanceLog varLog : varLogs ) {
               Assertions.assertThat(varLog.getVariableId()).isEqualTo(origVarLog.getVariableId());
           }
        }
        emptyVarLogs = auditLogService.findVariableInstancesByNameAndValue("s", "InitialValue", true);
        Assertions.assertThat(emptyVarLogs).isEmpty();
        String varId = "s";
        String varValue = "ResultValue";
        variableInstances = auditLogService.findVariableInstancesByNameAndValue(varId, varValue, false);
        Assertions.assertThat(variableInstances.size()).isEqualTo(3);
        VariableInstanceLog varLog = variableInstances.get(0);
        Assertions.assertThat(varLog.getVariableId()).isEqualTo(varId);
        Assertions.assertThat(varLog.getValue()).isEqualTo(varValue);
        
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assertions.assertThat(processInstances).isEmpty();
    }
    
    public static void runTestLogger4LargeVariable(KieSession session, AuditLogService auditLogService) throws Exception {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("Result", "ResultValue");
                manager.completeWorkItem(workItem.getId(), results);
            }
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("One");
        list.add("Two");
        String three = "";
        for (int i = 0; i < 1024; i++) {
            three += "*";
        }
        list.add(three);
        params.put("list", list);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        int expected = initialProcessInstanceSize + 1;
        Assertions.assertThat(processInstances.size()).isEqualTo(expected).withFailMessage(String.format("Expected %d ProcessInstanceLog instances, not %d", expected, processInstances.size()));
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug("{} -> {} - {}",processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assertions.assertThat(processInstance.getStart()).isNotNull();
        Assertions.assertThat(processInstance.getEnd()).isNotNull().withFailMessage("ProcessInstanceLog does not contain end date.");
        Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
        Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        Assertions.assertThat(variableInstances.size()).isEqualTo(8);
        for (VariableInstanceLog variableInstance: variableInstances) {
            logger.debug(variableInstance.toString());
            Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
            Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
            Assertions.assertThat(variableInstance.getDate()).isNotNull();
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assertions.assertThat(processInstances).isNullOrEmpty();
    }
    
    public static void runTestLogger5(KieSession session, AuditLogService auditLogService) throws Exception {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assertions.assertThat(processInstances.size()).isEqualTo(initialProcessInstanceSize + 1);
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assertions.assertThat(processInstance.getStart()).isNotNull();
        Assertions.assertThat(processInstance.getEnd()).isNotNull();
        Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
        Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow");
        Assertions.assertThat(processInstance.getStatus().intValue()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        Assertions.assertThat(nodeInstances.size()).isEqualTo(6);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.debug(nodeInstance.toString());
            Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
            Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow");
            Assertions.assertThat(nodeInstance.getDate()).isNotNull();
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assertions.assertThat(processInstances).isEmpty();
    }
    
    public static void runTestLoggerWithCustomVariableLogLength(KieSession session, AuditLogService auditLogService) throws Exception {
    	System.setProperty("org.jbpm.var.log.length", "15");
        final List<Long> workItemIds = new ArrayList<Long>();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                workItemIds.add(workItem.getId());
            }
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        processInstances = auditLogService.findActiveProcessInstances();
        int initialActiveProcessInstanceSize = processInstances.size();
        
        // prepare variable value
        String variableValue = "very short value that should be trimmed by custom variable log length";
        
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        params.put("list", list);
        params.put("s", variableValue);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();
        int numActiveProcesses = auditLogService.findActiveProcessInstances().size();
        Assertions.assertThat(numActiveProcesses).isEqualTo(initialActiveProcessInstanceSize + 1).withFailMessage("find active processes did not work");
 
        // Test findVariableInstancesByName* methods: check for variables (only) in active processes
        List<VariableInstanceLog> varLogs = auditLogService.findVariableInstancesByName("s", true) ;
        varLogs = varLogs.stream().sorted((o1, o2) -> Long.compare(o1.getId(), o2.getId())).collect(Collectors.toList());
        Assertions.assertThat(varLogs).isNotEmpty();
        Assertions.assertThat(varLogs.size()).isEqualTo(2);
        Assertions.assertThat(varLogs).flatExtracting(VariableInstanceLog::getValue).containsExactly("InitialValue", variableValue.substring(0, 15));
                
        for( Long workItemId : workItemIds ) { 
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", "ResultValue");
            session.getWorkItemManager().completeWorkItem(workItemId, results);
        }
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assertions.assertThat(processInstances.size()).isEqualTo(initialProcessInstanceSize + 1);
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assertions.assertThat(processInstance.getStart()).isNotNull();
        Assertions.assertThat(processInstance.getEnd()).isNotNull().withFailMessage("ProcessInstanceLog does not contain end date.");
        Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
        Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        Assertions.assertThat(variableInstances.size()).isEqualTo(12);
        for (VariableInstanceLog variableInstance: variableInstances) {
            logger.debug(variableInstance.toString());
            Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
            Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
            Assertions.assertThat(variableInstance.getDate()).isNotNull();
        }
        
        // Test findVariableInstancesByName* methods
        List<VariableInstanceLog> emptyVarLogs = auditLogService.findVariableInstancesByName("s", true) ;
        Assertions.assertThat(emptyVarLogs).isEmpty();
        for( VariableInstanceLog origVarLog : variableInstances ) { 
           varLogs = auditLogService.findVariableInstancesByName(origVarLog.getVariableId(), false) ;
           for( VariableInstanceLog varLog : varLogs ) {
               Assertions.assertThat(varLog.getVariableId()).isEqualTo(origVarLog.getVariableId());
           }
        }
        emptyVarLogs = auditLogService.findVariableInstancesByNameAndValue("s", "InitialValue", true);
        Assertions.assertThat(emptyVarLogs).isEmpty();
        String varId = "s";
        String varValue = "ResultValue";
        variableInstances = auditLogService.findVariableInstancesByNameAndValue(varId, varValue, false);
        Assertions.assertThat(variableInstances.size()).isEqualTo(3);
        VariableInstanceLog varLog = variableInstances.get(0);
        Assertions.assertThat(varLog.getVariableId()).isEqualTo(varId);
        Assertions.assertThat(varLog.getValue()).isEqualTo(varValue);
        
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assertions.assertThat(processInstances).isEmpty();
    }
    
    public static void runTestLogger4WithCustomVariableIndexer(KieSession session, AuditLogService auditLogService) throws Exception {
        final List<Long> workItemIds = new ArrayList<Long>();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                workItemIds.add(workItem.getId());
            }
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new LinkedList<String>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        params.put("list", list);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();

        // Test findVariableInstancesByName* methods: check for variables (only) in active processes
        List<VariableInstanceLog> varLogs = auditLogService.findVariableInstancesByName("s", true) ;
        Assertions.assertThat(varLogs).isNotEmpty();
        Assertions.assertThat(varLogs.size()).isEqualTo(1);
                
        for( Long workItemId : workItemIds ) { 
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", "ResultValue");
            session.getWorkItemManager().completeWorkItem(workItemId, results);
        }
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assertions.assertThat(processInstances.size()).isEqualTo(initialProcessInstanceSize + 1);
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assertions.assertThat(processInstance.getStart()).isNotNull();
        // ProcessInstanceLog does not contain end date.
        Assertions.assertThat(processInstance.getEnd()).isNotNull();
        Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
        Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        Assertions.assertThat(variableInstances.size()).isEqualTo(13);
        for (VariableInstanceLog variableInstance: variableInstances) {
            logger.debug(variableInstance.toString());
            Assertions.assertThat(processInstance.getProcessInstanceId().longValue()).isEqualTo(processInstanceId);
            Assertions.assertThat(processInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
            Assertions.assertThat(variableInstance.getDate()).isNotNull();
        }
        
        List<VariableInstanceLog> listVariables = new ArrayList<VariableInstanceLog>();
        // collect only those that are related to list process variable
        for (VariableInstanceLog v : variableInstances) {
            if (v.getVariableInstanceId().equals("list")) {
                listVariables.add(v);
            }
        }

        Assertions.assertThat(listVariables.size()).isEqualTo(3);

        List<String> variableValues = new ArrayList<String>();
        List<String> variableIds = new ArrayList<String>();
        for (VariableInstanceLog var : listVariables) {
            variableValues.add(var.getValue());
            variableIds.add(var.getVariableId());
            // Various DBs return various empty values. (E.g. Oracle returns null.)
            Assertions.assertThat(var.getOldValue()).isIn("", " ", null);
            Assertions.assertThat(var.getProcessInstanceId()).isEqualTo(processInstance.getProcessInstanceId());
            Assertions.assertThat(var.getProcessId()).isEqualTo(processInstance.getProcessId());
            Assertions.assertThat(var.getVariableInstanceId()).isEqualTo("list");
        }

        Assertions.assertThat(variableValues).contains("One", "Two", "Three");
        Assertions.assertThat(variableIds).contains("list[0]", "list[1]", "list[2]");

        // Test findVariableInstancesByName* methods
        List<VariableInstanceLog> emptyVarLogs = auditLogService.findVariableInstancesByName("s", true) ;
        Assertions.assertThat(emptyVarLogs).isEmpty();
        for( VariableInstanceLog origVarLog : variableInstances ) { 
           varLogs = auditLogService.findVariableInstancesByName(origVarLog.getVariableId(), false) ;
           for( VariableInstanceLog varLog : varLogs ) {
               Assertions.assertThat(varLog.getVariableId()).isEqualTo(origVarLog.getVariableId());
           }
        }
        emptyVarLogs = auditLogService.findVariableInstancesByNameAndValue("s", "InitialValue", true);
        Assertions.assertThat(emptyVarLogs).isEmpty();
        String varId = "s";
        String varValue = "ResultValue";
        variableInstances = auditLogService.findVariableInstancesByNameAndValue(varId, varValue, false);
        Assertions.assertThat(variableInstances.size()).isEqualTo(3);
        VariableInstanceLog varLog = variableInstances.get(0);
        Assertions.assertThat(varLog.getVariableId()).isEqualTo(varId);
        Assertions.assertThat(varLog.getValue()).isEqualTo(varValue);
        
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assertions.assertThat(processInstances).isEmpty();
    }
    
}
