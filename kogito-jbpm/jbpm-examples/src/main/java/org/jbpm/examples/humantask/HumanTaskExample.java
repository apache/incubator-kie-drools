package org.jbpm.examples.humantask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.workitem.wsht.HornetQHTWorkItemHandler;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.kie.KnowledgeBase;
import org.kie.SystemEventListenerFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.logger.KnowledgeRuntimeLogger;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.runtime.StatefulKnowledgeSession;

public class HumanTaskExample {

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
            params.put("userId", "krisv");
            params.put("description", "Need a new laptop computer");
            ksession.startProcess("com.sample.humantask", params);

            SystemEventListenerFactory.setSystemEventListener(new SystemEventListener());
            // we can reuse the client used by the Work Item Hander.
            TaskService taskClient = new SyncTaskServiceWrapper(new AsyncHornetQTaskClient("HumanTaskExample-testClient"));

            taskClient.connect("127.0.0.1", 5153);
            
            Thread.sleep(1000);
            // "sales-rep" reviews request
            List<String> groups = new ArrayList<String>();
            groups.add("sales");

            TaskSummary task1 = taskClient.getTasksAssignedAsPotentialOwner("sales-rep", groups, "en-UK").get(0);
            System.out.println("Sales-rep executing task " + task1.getName() + "(" + task1.getId() + ": " + task1.getDescription() + ")");
            taskClient.claim(task1.getId(), "sales-rep", groups);
            
            Thread.sleep(1000);
            
            taskClient.start(task1.getId(), "sales-rep");

            Map<String, Object> results = new HashMap<String, Object>();
            results.put("comment", "Agreed, existing laptop needs replacing");
            results.put("outcome", "Accept");
            ContentData contentData = ContentMarshallerHelper.marshal(results,  null);

            taskClient.complete(task1.getId(), "sales-rep", contentData);

            Thread.sleep(2000);

            // "krisv" approves result
            TaskSummary task2 = taskClient.getTasksAssignedAsPotentialOwner("krisv", "en-UK").get(0);
            System.out.println("krisv executing task " + task2.getName() + "(" + task2.getId() + ": " + task2.getDescription() + ")");
            taskClient.start(task2.getId(), "krisv");

            
            
            results = new HashMap<String, Object>();
            results.put("outcome", "Agree");
            contentData = ContentMarshallerHelper.marshal(results, null);


            taskClient.complete(task2.getId(), "krisv", contentData);

            Thread.sleep(2000);
            // "john" as manager reviews request

            groups = new ArrayList<String>();
            groups.add("PM");

            TaskSummary task3 = taskClient.getTasksAssignedAsPotentialOwner("john", groups, "en-UK").get(0);
            System.out.println("john executing task " + task3.getName() + "(" + task3.getId() + ": " + task3.getDescription() + ")");

            
            taskClient.claim(task3.getId(), "john", groups);
            
            taskClient.start(task3.getId(), "john");

            results = new HashMap<String, Object>();
            results.put("outcome", "Agree");
            contentData = ContentMarshallerHelper.marshal(results, null);

            taskClient.complete(task3.getId(), "john", contentData);

            Thread.sleep(2000);
            // "sales-rep" gets notification
            TaskSummary task4 = taskClient.getTasksAssignedAsPotentialOwner("sales-rep", "en-UK").get(0);
            System.out.println("sales-rep executing task " + task4.getName() + "(" + task4.getId() + ": " + task4.getDescription() + ")");

            taskClient.start(task4.getId(), "sales-rep");


            Task task = taskClient.getTask(task4.getId());


            Content content = taskClient.getContent(task.getTaskData().getDocumentContentId());

            Object result = ContentMarshallerHelper.unmarshall(content.getContent(), null);

            Map<?, ?> map = (Map<?, ?>) result;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }


            taskClient.complete(task4.getId(), "sales-rep", null);

            
            Thread.sleep(2000);
            taskClient.disconnect();
            hornetQHTWorkItemHandler.dispose();
            logger.close();
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("humantask/HumanTask.bpmn"), ResourceType.BPMN2);
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
