package org.jbpm;

import java.util.List;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a sample file to test a process.
 */
public class ProcessPersistenceHumanTaskTest extends JbpmJUnitTestCase {

    private Logger testLogger = LoggerFactory.getLogger(ProcessPersistenceHumanTaskTest.class);

    public ProcessPersistenceHumanTaskTest() {
        super(true);
        setPersistence(true);
    }

    @Test
    public void testProcess() throws Exception {
        StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
        TaskService taskService = getTaskService(ksession);

        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");

        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");

        // simulating a system restart
        ksession = restoreSession(ksession, true);
        taskService = getTaskService(ksession);

        // let john execute Task 1
        String taskGroup = "en-UK";
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", taskGroup);
        TaskSummary task = list.get(0);
        testLogger.debug("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");

        // simulating a system restart
        ksession = restoreSession(ksession, true);
        taskService = getTaskService(ksession);

        // let mary execute Task 2
        String taskUser = "mary";
        list = taskService.getTasksAssignedAsPotentialOwner(taskUser, taskGroup);
        assertTrue("No tasks found for potential owner " + taskUser + "/" + taskGroup, list.size() > 0);
        task = list.get(0);
        testLogger.debug("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testTransactions() throws Exception {
        StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
        TaskService taskService = getTaskService(ksession);

        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
        ut.rollback();

        assertNull(ksession.getProcessInstance(processInstance.getId()));
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(0, list.size());
    }

}