package org.jbpm.persistence;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.persistence.objects.MedicalRecord;
import org.jbpm.persistence.objects.Patient;
import org.jbpm.persistence.objects.RecordRow;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.task.api.TaskService;
import org.kie.internal.task.api.model.Content;
import org.kie.internal.task.api.model.Task;
import org.kie.internal.task.api.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientVariablePersistenceStrategyTest extends JbpmJUnitTestCase {

    private static Logger logger = LoggerFactory.getLogger(PatientVariablePersistenceStrategyTest.class);
    
    

    private EntityManagerFactory emfDomain;

    
    
    protected TaskService taskService ;

    
    protected KieSession ksession;

    public PatientVariablePersistenceStrategyTest() {
        super(true);
        setPersistence(true);
    }
    
    

    @Test
    public void simplePatientMedicalRecordTest() throws Exception {
        Patient salaboy = new Patient("salaboy");
        MedicalRecord medicalRecord = new MedicalRecord("Last Three Years Medical Hisotry", salaboy);
        
        emfDomain = Persistence.createEntityManagerFactory("org.jbpm.persistence.patient.example");
        EntityManager em = emfDomain.createEntityManager();
        
        em.getTransaction().begin();
        em.persist(medicalRecord);
        em.getTransaction().commit();
        
        
        ksession = createKnowledgeSession("patient-appointment.bpmn");
        
        taskService = getTaskService();
        
        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("medicalRecord", medicalRecord);
        ProcessInstance process = ksession.startProcess("org.jbpm.PatientAppointment", parameters); 
        long processInstanceId = process.getId();

        //The process is in the first Human Task waiting for its completion
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());
        
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
        MedicalRecord taskMedicalRecord = getTaskContent(frontDeskTasks.get(0));
        Assert.assertNotNull(taskMedicalRecord.getId());
        taskMedicalRecord.setDescription("Initial Description of the Medical Record");
        
        
        em.getTransaction().begin();
        em.merge(taskMedicalRecord);
        em.getTransaction().commit();
        

        
        taskService.complete(frontDeskTasks.get(0).getId(), "frontDesk", null);
        
        //Now doctor has 1 task
        doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertEquals(1, doctorTasks.size());
        
         //No tasks for manager yet
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());
        
        taskMedicalRecord = getTaskContent(doctorTasks.get(0));
        
        taskService.start(doctorTasks.get(0).getId(), "doctor");
        //Check that we have the Modified Document
        taskMedicalRecord = em.find(MedicalRecord.class, taskMedicalRecord.getId());
        
        Assert.assertEquals("Initial Description of the Medical Record", taskMedicalRecord.getDescription());
        
        
        em.getTransaction().begin();
        taskMedicalRecord.setDescription("Medical Record Validated by Doctor");
        List<RecordRow> rows = new ArrayList<RecordRow>();
        RecordRow recordRow = new RecordRow("CODE-999", "Just a regular Cold");
        recordRow.setMedicalRecord(medicalRecord);
        rows.add(recordRow);
        taskMedicalRecord.setRows(rows);
        taskMedicalRecord.setPriority(1);
        
        em.getTransaction().commit();
        
        
        taskService.complete(doctorTasks.get(0).getId(), "doctor", null);
        
         // tasks for manager 
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertEquals(1, managerTasks.size());
        taskService.start(managerTasks.get(0).getId(), "manager");
        
        em.getTransaction().begin();
        Patient patient = taskMedicalRecord.getPatient();
        patient.setNextAppointment(new Date());
        
        em.getTransaction().commit();
       
       // ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htHandler);
        
        taskService.complete(managerTasks.get(0).getId(), "manager", null);
        
        // since persisted process instance is completed it should be null
        process = ksession.getProcessInstance(process.getId());
        Assert.assertNull(process);
        
        
        
    }
    
    private MedicalRecord getTaskContent(TaskSummary summary) throws IOException, ClassNotFoundException{
        logger.info(" >>> Getting Task Content = "+summary.getId());
        
        Task task = taskService.getTaskById(summary.getId());
        long documentContentId = task.getTaskData().getDocumentContentId();
        Content content = taskService.getContentById(documentContentId);
        Object readObject = 
                ContentMarshallerHelper.unmarshall(         content.getContent(), 
                                                            ksession.getEnvironment());
        
        logger.info(" >>> Object = "+readObject);
        return (MedicalRecord)readObject;
    }
    
  
}
