/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.droolsjbpm.services.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.droolsjbpm.services.api.Domain;
import org.droolsjbpm.services.api.FileException;
import org.droolsjbpm.services.api.FileService;
import org.kie.definition.process.Process;

import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.impl.KnowledgeDomainServiceImpl;
import org.droolsjbpm.services.impl.SimpleDomainImpl;
import org.droolsjbpm.services.impl.model.NodeInstanceDesc;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.droolsjbpm.services.impl.model.VariableStateDesc;
import org.droolsjbpm.services.impl.CDISessionManager;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.Test;
import static org.junit.Assert.*;
import org.kie.commons.java.nio.file.Path;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

public abstract class DomainKnowledgeServiceBaseTest {

    @Inject
    protected TaskServiceEntryPoint taskService;
    @Inject
    protected KnowledgeDataService dataService;
    @Inject
    protected KnowledgeAdminDataService adminDataService;
    @Inject
    private FileService fs;
    @Inject
    private CDISessionManager sessionManager;

            
    @Test
    public void simpleDomainTest() {
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);

        Iterable<Path> loadFilesByType = null;
        try {
            loadFilesByType = fs.loadFilesByType("examples/general/", "bpmn");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Path p : loadFilesByType) {
            myDomain.addKsessionAsset("myKsession", p);
        }

        sessionManager.buildSessions(); //DO THIS -> OR oneSessionOneProcessStrategy.buildSessionByName("mySession");


        ProcessInstance pI = sessionManager.getKsessionByName("myKsession").startProcess("org.jbpm.writedocument");


        assertNotNull(pI);

        assertEquals(1, sessionManager.getProcessInstanceIdKsession().size());

        assertEquals("myKsession", sessionManager.getSessionForProcessInstanceId(pI.getId()));




    }

    @Test
    public void simpleDomainTwoSessionsTest() {
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);

        Iterable<Path> loadFilesByType = null;
        try {
            loadFilesByType = fs.loadFilesByType("examples/general/", "bpmn");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        int i = 0;
        for (Path p : loadFilesByType) {
            myDomain.addKsessionAsset("myKsession" + i, p);
            i++;
        }
        
        sessionManager.buildSessions();
        
        
        Collection<String> sessionNames = sessionManager.getAllSessionsNames();
        for (String sessionName : sessionNames) {
            Collection<String> processDefinitionsIds = sessionManager.getProcessesInSession(sessionName);
            for (String processDefId : processDefinitionsIds) {
                String ksessionName = sessionManager.getProcessInSessionByName(processDefId);
                ProcessInstance pI = sessionManager.getKsessionByName(ksessionName).startProcess(processDefId);
                assertNotNull(pI);
                
            }
        }
        assertEquals(2, sessionManager.getProcessInstanceIdKsession().size());


    }

    @Test
    public void testReleaseProcess() {
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);

        Iterable<Path> loadFilesByType = null;
        try {
            loadFilesByType = fs.loadFilesByType("examples/release/", "bpmn");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Path p : loadFilesByType) {
            myDomain.addKsessionAsset("myKsession", p);
        }

        sessionManager.buildSessions();

        ProcessInstance pI = sessionManager.getKsessionByName("myKsession").startProcess("org.jbpm.release.process");



    }

    //add release example here
    @Test
    public void testSimpleProcess() throws Exception {

        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);

        Iterable<Path> loadFilesByType = null;
        try {
            loadFilesByType = fs.loadFilesByType("examples/general/", "bpmn");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Path p : loadFilesByType) {
            myDomain.addKsessionAsset("myKsession", p);
        }

        sessionManager.buildSessions();



        StatefulKnowledgeSession ksession = sessionManager.getKsessionByName("myKsession");



        Collection<Process> processes = ksession.getKnowledgeBase().getProcesses();


        ProcessInstance processInstance = ksession.startProcess("org.jbpm.writedocument", null);
        ProcessInstanceDesc processInstanceById = dataService.getProcessInstanceById(0, processInstance.getId());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceById.getState());
        Collection<ProcessInstanceDesc> processInstancesDesc = dataService.getProcessInstances();
        assertEquals(1, processInstancesDesc.size());
        // I'm not using a persistent session here
        Collection<NodeInstanceDesc> processInstanceHistory = dataService.getProcessInstanceHistory(0, processInstance.getId());
        Iterator<NodeInstanceDesc> iterator = processInstanceHistory.iterator();
        assertEquals(2, processInstanceHistory.size());
        
        List<String> names = new ArrayList<String>(processInstanceHistory.size());
        while(iterator.hasNext()){
            names.add(iterator.next().getName());
        }
        
        assertTrue(names.contains("Start") && names.contains("Write a Document"));
        
        Collection<NodeInstanceDesc> processInstanceActiveNodes = dataService.getProcessInstanceActiveNodes(0, processInstance.getId());
        assertEquals(1, processInstanceActiveNodes.size());
        assertEquals("Write a Document", processInstanceActiveNodes.iterator().next().getName());

        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Collection<ProcessInstance> processInstances = ksession.getProcessInstances();

        assertEquals(1, processInstances.size());

        assertEquals(1, tasksAssignedAsPotentialOwner.size());
        Collection<VariableStateDesc> variablesCurrentState = dataService.getVariablesCurrentState(processInstance.getId());
        assertEquals(0, variablesCurrentState.size());
        // Get Twice to test duplicated items
        tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

        assertEquals(1, tasksAssignedAsPotentialOwner.size());


        TaskSummary task = tasksAssignedAsPotentialOwner.get(0);


        taskService.start(task.getId(), "salaboy");

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Result", "Initial Document");
        taskService.complete(task.getId(), "salaboy", result);





        List<TaskSummary> translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());

        variablesCurrentState = dataService.getVariablesCurrentState(processInstance.getId());
        assertEquals(1, variablesCurrentState.size());
        assertEquals("Initial Document", variablesCurrentState.iterator().next().getNewValue());

        processInstanceHistory = dataService.getProcessInstanceHistory(0, processInstance.getId());
        assertEquals(5, processInstanceHistory.size());
        
        iterator = processInstanceHistory.iterator();
        names = new ArrayList<String>(processInstanceHistory.size());
        while(iterator.hasNext()){
            names.add(iterator.next().getName());
        }
        
        assertTrue(names.contains("Start") && names.contains("Write a Document") 
                && names.contains("Review and Translate") && names.contains("Translate Document") 
                && names.contains("Review Document"));
        

        processInstanceActiveNodes = dataService.getProcessInstanceActiveNodes(0, processInstance.getId());
        assertEquals(2, processInstanceActiveNodes.size());
        Iterator<NodeInstanceDesc> iteratorActiveNodes = processInstanceActiveNodes.iterator();
        String nodeName = iteratorActiveNodes.next().getName();
        assertTrue(("Translate Document".equals(nodeName) || "Review Document".equals(nodeName)));
        nodeName = iteratorActiveNodes.next().getName();
        assertTrue(("Translate Document".equals(nodeName) || "Review Document".equals(nodeName)));


        List<TaskSummary> reviewerTasks = taskService.getTasksAssignedAsPotentialOwner("reviewer", "en-UK");
        assertEquals(1, reviewerTasks.size());

        taskService.start(reviewerTasks.get(0).getId(), "reviewer");
        result = new HashMap<String, Object>();
        result.put("Result", "Reviewed Document");
        taskService.complete(reviewerTasks.get(0).getId(), "reviewer", result);

        processInstanceHistory = dataService.getProcessInstanceHistory(0, processInstance.getId());
        assertEquals(6, processInstanceHistory.size());
        iterator = processInstanceHistory.iterator();
        
        
        names = new ArrayList<String>(processInstanceHistory.size());
        while(iterator.hasNext()){
            names.add(iterator.next().getName());
        }
        
        assertTrue(names.contains("Start") && names.contains("Write a Document") 
                && names.contains("Review and Translate") && names.contains("Translate Document") 
                && names.contains("Review Document") && names.contains("Reviewed and Translated"));
        
        

        variablesCurrentState = dataService.getVariablesCurrentState(processInstance.getId());
        assertEquals(2, variablesCurrentState.size());
        Iterator<VariableStateDesc> variableIterator = variablesCurrentState.iterator();
        assertEquals("Initial Document", variableIterator.next().getNewValue());
        assertEquals("Reviewed Document", variableIterator.next().getNewValue());


        ProcessInstance pi = ksession.getProcessInstance(processInstance.getId());
        ((WorkflowProcessInstance) pi).setVariable("approval_document", "Initial Document(updated)");

        variablesCurrentState = dataService.getVariablesCurrentState(processInstance.getId());
        assertEquals(2, variablesCurrentState.size());

        translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());

        taskService.start(translatorTasks.get(0).getId(), "translator");
        result = new HashMap<String, Object>();
        result.put("Result", "Translated Document");
        taskService.complete(translatorTasks.get(0).getId(), "translator", result);

        processInstanceHistory = dataService.getProcessInstanceHistory(0, processInstance.getId());
        assertEquals(9, processInstanceHistory.size());
        
        iterator = processInstanceHistory.iterator();
        names = new ArrayList<String>(processInstanceHistory.size());
        while(iterator.hasNext()){
            names.add(iterator.next().getName());
        }
        
        assertTrue(names.contains("Start") && names.contains("Write a Document") 
                && names.contains("Review and Translate") && names.contains("Translate Document") 
                && names.contains("Review Document") && names.contains("Reviewed and Translated")
                && names.contains("Report") && names.contains("End"));
        
      

        variablesCurrentState = dataService.getVariablesCurrentState(processInstance.getId());
        assertEquals(3, variablesCurrentState.size());
        variableIterator = variablesCurrentState.iterator();

        assertEquals("Reviewed Document", variableIterator.next().getNewValue());
        assertEquals("Initial Document(updated)", variableIterator.next().getNewValue());
        assertEquals("Translated Document", variableIterator.next().getNewValue());


        processInstanceHistory = dataService.getProcessInstanceFullHistory(0, processInstance.getId());
        assertEquals(18, processInstanceHistory.size());

        variablesCurrentState = dataService.getVariableHistory(processInstance.getId(), "approval_document");
        assertEquals(2, variablesCurrentState.size());
        variableIterator = variablesCurrentState.iterator();
        assertEquals("Initial Document(updated)", variableIterator.next().getNewValue());
        assertEquals("Initial Document", variableIterator.next().getNewValue());

    }

    @Test
    public void testMultiProcessInstances() {


        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);

        Iterable<Path> loadFilesByType = null;
        try {
            loadFilesByType = fs.loadFilesByType("examples/general/", "bpmn");
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Path p : loadFilesByType) {
            myDomain.addKsessionAsset("myKsession", p);
        }

        sessionManager.buildSessions();

        StatefulKnowledgeSession ksession = sessionManager.getKsessionByName("myKsession");



        ProcessInstance processInstance = ksession.startProcess("org.jbpm.writedocument", null);
        ProcessInstanceDesc processInstanceById = dataService.getProcessInstanceById(0, processInstance.getId());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceById.getState());

        processInstance = ksession.startProcess("org.jbpm.writedocument", null);
        processInstanceById = dataService.getProcessInstanceById(0, processInstance.getId());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceById.getState());

        Collection<ProcessInstanceDesc> processInstancesDesc = dataService.getProcessInstances();
        assertEquals(2, processInstancesDesc.size());





        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Collection<ProcessInstance> processInstances = ksession.getProcessInstances();



        // Get Twice to test duplicated items
        tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

        assertEquals(2, tasksAssignedAsPotentialOwner.size());


        TaskSummary task = tasksAssignedAsPotentialOwner.get(0);


        taskService.start(task.getId(), "salaboy");

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Result", "Initial Document");
        taskService.complete(task.getId(), "salaboy", result);





        List<TaskSummary> translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());





        List<TaskSummary> reviewerTasks = taskService.getTasksAssignedAsPotentialOwner("reviewer", "en-UK");
        assertEquals(1, reviewerTasks.size());

        taskService.start(reviewerTasks.get(0).getId(), "reviewer");

        taskService.complete(reviewerTasks.get(0).getId(), "reviewer", null);






        translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());

        taskService.start(translatorTasks.get(0).getId(), "translator");

        taskService.complete(translatorTasks.get(0).getId(), "translator", null);
        processInstanceById = dataService.getProcessInstanceById(0, processInstance.getId());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceById.getState());


        processInstance = ksession.startProcess("org.jbpm.writedocument", null);
        processInstanceById = dataService.getProcessInstanceById(0, processInstance.getId());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceById.getState());
        ProcessInstanceDesc next = dataService.getProcessInstances().iterator().next();
        assertTrue(next instanceof ProcessInstanceDesc);
        assertEquals(3, dataService.getProcessInstances().size());

    }
}
