package org.jbpm.examples.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.KnowledgeBase;
import org.kie.SystemEventListenerFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.logger.KnowledgeRuntimeLogger;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.HornetQHTWorkItemHandler;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;


/**
 * This is a sample file to launch a process.
 */
public class EvaluationExample2 {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase();
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, "test", 1000);
            HornetQHTWorkItemHandler hornetQHTWorkItemHandler = new HornetQHTWorkItemHandler(ksession);
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task", hornetQHTWorkItemHandler);
            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("employee", "krisv");
            params.put("reason", "Yearly performance evaluation");
            ksession.startProcess("com.sample.evaluation", params);

            SystemEventListenerFactory.setSystemEventListener(new SystemEventListener());
            TaskService taskClient = new SyncTaskServiceWrapper(new AsyncHornetQTaskClient("EvaluationExample2-testClient"));
            taskClient.connect("127.0.0.1", 5153);


            Thread.sleep(1000);
            // "krisv" executes his own performance evaluation
            TaskSummary task1 = taskClient.getTasksAssignedAsPotentialOwner("krisv", "en-UK").get(0);
            System.out.println("Krisv executing task " + task1.getName() + "(" + task1.getId() + ": " + task1.getDescription() + ")");

            taskClient.start(task1.getId(), "krisv");

            taskClient.complete(task1.getId(), "krisv", null);


            Thread.sleep(1000);
            // "john", part of the "PM" group, executes a performance evaluation
            List<String> groups = new ArrayList<String>();
            groups.add("PM");

            TaskSummary task2 = taskClient.getTasksAssignedAsPotentialOwner("john", groups, "en-UK").get(0);
            System.out.println("John executing task " + task2.getName() + "(" + task2.getId() + ": " + task2.getDescription() + ")");

            taskClient.claim(task2.getId(), "john", groups);

            taskClient.start(task2.getId(), "john");


            taskClient.complete(task2.getId(), "john", null);
            
            Thread.sleep(1000);
            // "mary", part of the "HR" group, delegates a performance evaluation
            groups = new ArrayList<String>();
            groups.add("HR");

            TaskSummary task3 = taskClient.getTasksAssignedAsPotentialOwner("mary", groups, "en-UK").get(0);
            System.out.println("Mary delegating task " + task3.getName() + "(" + task3.getId() + ": " + task3.getDescription() + ") to krisv");

            taskClient.claim(task3.getId(), "mary", groups);

            taskClient.delegate(task3.getId(), "mary", "krisv");

            
            // "administrator" delegates the task back to mary
            System.out.println("Administrator delegating task back to mary");

            taskClient.delegate(task3.getId(), "Administrator", "mary");

            Thread.sleep(1000);
            // mary executing the task
            TaskSummary task3b = taskClient.getTasksAssignedAsPotentialOwner("mary", "en-UK").get(0);
            System.out.println("Mary executing task " + task3b.getName() + "(" + task3b.getId() + ": " + task3b.getDescription() + ")");

            taskClient.start(task3b.getId(), "mary");


            taskClient.complete(task3b.getId(), "mary", null);

            taskClient.disconnect();
            hornetQHTWorkItemHandler.dispose();
            Thread.sleep(1000);
            
            logger.close();
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("evaluation/Evaluation2.bpmn"), ResourceType.BPMN2);
        return kbuilder.newKnowledgeBase();
    }

    private static class SystemEventListener implements org.kie.SystemEventListener {

        public void debug(String arg0) {
        }

        public void debug(String arg0, Object arg1) {
        }

        public void exception(Throwable arg0) {
        }

        public void exception(String arg0, Throwable arg1) {
        }

        public void info(String arg0) {
        }

        public void info(String arg0, Object arg1) {
        }

        public void warning(String arg0) {
        }

        public void warning(String arg0, Object arg1) {
        }
    }
}
