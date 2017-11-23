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

package org.jbpm.test.functional.jpa;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.jbpm.marshalling.impl.ProcessInstanceResolverStrategy;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.entity.MedicalRecord;
import org.jbpm.test.entity.Patient;
import org.jbpm.test.entity.RecordRow;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class PatientVariablePersistenceStrategyTest extends JbpmTestCase {

    private static final Logger logger = LoggerFactory.getLogger(PatientVariablePersistenceStrategyTest.class);

    private EntityManagerFactory emfDomain;


    public PatientVariablePersistenceStrategyTest() {
        super(true, true);
    }

    @Test
    public void simplePatientMedicalRecordTest() throws Exception {
        Patient salaboy = new Patient("salaboy");
        MedicalRecord medicalRecord = new MedicalRecord("Last Three Years Medical Hisotry", salaboy);
        
        emfDomain = Persistence.createEntityManagerFactory("org.jbpm.persistence.patient.example");
        addEnvironmentEntry(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, 
        		new ObjectMarshallingStrategy[] {
                new JPAPlaceholderResolverStrategy(emfDomain),
                new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT) });
        
        
        EntityManager em = emfDomain.createEntityManager();

        
        createRuntimeManager("org/jbpm/test/functional/jpa/patient-appointment.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        
        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("medicalRecord", medicalRecord);
        ProcessInstance process = ksession.startProcess("org.jbpm.PatientAppointment", parameters); 
        long processInstanceId = process.getId();

        //The process is in the first Human Task waiting for its completion
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());
        
        List<? extends VariableInstanceLog> varLogs = runtimeEngine.getAuditService().findVariableInstances(processInstanceId, "medicalRecord");
        assertNotNull(varLogs);
        assertEquals(1, varLogs.size());        
        
        //gets frontDesk's tasks
        List<TaskSummary> frontDeskTasks = taskService.getTasksAssignedAsPotentialOwner("frontDesk", "en-UK");
        Assert.assertEquals(1, frontDeskTasks.size());
        
         //doctor doesn't have any task
        List<TaskSummary> doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertTrue(doctorTasks.isEmpty());
        
        //manager doesn't have any task
        List<TaskSummary> managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());
        
        
        taskService.start(frontDeskTasks.get(0).getId(), "frontDesk");
        //frontDesk completes its task
        MedicalRecord taskMedicalRecord = getTaskContent(runtimeEngine, frontDeskTasks.get(0));
        assertNotNull(taskMedicalRecord.getId());
        taskMedicalRecord.setDescription("Initial Description of the Medical Record");
                
        Map<String, Object> output = new HashMap<String, Object>();
        output.put("output1", taskMedicalRecord);
        taskService.complete(frontDeskTasks.get(0).getId(), "frontDesk", output);
        
        varLogs = runtimeEngine.getAuditService().findVariableInstances(processInstanceId, "medicalRecord");
        assertNotNull(varLogs);        
        assertEquals(2, varLogs.size());        
        
        assertTrue(varLogs.get(0).getValue().contains("Last Three Years Medical Hisotry"));
        assertTrue(varLogs.get(1).getValue().contains("Initial Description of the Medical Record"));
        
        //Now doctor has 1 task
        doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertEquals(1, doctorTasks.size());
        
         //No tasks for manager yet
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());
        
        // modify the entity from outside
        taskMedicalRecord.setDescription("Initial Description of the Medical Record - Updated");
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        try {
            ut.begin();
            em.merge(taskMedicalRecord);
            ut.commit();
        } catch (Exception ex) {
            ut.rollback();
            throw ex;
        }
        
        taskMedicalRecord = getTaskContent(runtimeEngine, doctorTasks.get(0));
        
        assertNotNull(taskMedicalRecord.getId());
        taskMedicalRecord.setDescription("Initial Description of the Medical Record - Updated");
        
        taskService.start(doctorTasks.get(0).getId(), "doctor");
        //Check that we have the Modified Document
        taskMedicalRecord = em.find(MedicalRecord.class, taskMedicalRecord.getId());
        
        Assert.assertEquals("Initial Description of the Medical Record - Updated", taskMedicalRecord.getDescription());
        
        
        taskMedicalRecord.setDescription("Medical Record Validated by Doctor");
        List<RecordRow> rows = new ArrayList<RecordRow>();
        RecordRow recordRow = new RecordRow("CODE-999", "Just a regular Cold");
        recordRow.setMedicalRecord(medicalRecord);
        rows.add(recordRow);
        taskMedicalRecord.setRows(rows);
        taskMedicalRecord.setPriority(1);
        
        output = new HashMap<String, Object>();
        output.put("output2", taskMedicalRecord);
        
        taskService.complete(doctorTasks.get(0).getId(), "doctor", output);
        
         // tasks for manager 
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertEquals(1, managerTasks.size());
        taskService.start(managerTasks.get(0).getId(), "manager");
        
        Patient patient = taskMedicalRecord.getPatient();
        patient.setNextAppointment(new Date());

        
        output = new HashMap<String, Object>();
        output.put("output3", taskMedicalRecord);
       
       // ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htHandler);
        
        taskService.complete(managerTasks.get(0).getId(), "manager", output);
        
        // since persisted process instance is completed it should be null
        process = ksession.getProcessInstance(process.getId());
        Assert.assertNull(process);
    }
    
    @SuppressWarnings("rawtypes")
    private MedicalRecord getTaskContent(RuntimeEngine runtimeEngine, TaskSummary summary) throws IOException, ClassNotFoundException{
        logger.info(" >>> Getting Task Content = {}", summary.getId());
        
        Task task = runtimeEngine.getTaskService().getTaskById(summary.getId());
        long documentContentId = task.getTaskData().getDocumentContentId();
        Content content = runtimeEngine.getTaskService().getContentById(documentContentId);
        Object readObject = ContentMarshallerHelper.unmarshall(content.getContent(), 
                                runtimeEngine.getKieSession().getEnvironment());
        
        logger.info(" >>> Object = {}", readObject);
        return (MedicalRecord)((Map)readObject).get("Content");
    }
    
  
}
