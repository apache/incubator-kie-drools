package org.jbpm.examples.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

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
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WSHumanTaskHandler());
			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("employee", "krisv");
			params.put("reason", "Yearly performance evaluation");
			ksession.startProcess("com.sample.evaluation", params);

			SystemEventListenerFactory.setSystemEventListener(new SystemEventListener());
			TaskClient taskClient = new TaskClient(new MinaTaskClientConnector("MinaConnector",
				new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
			taskClient.connect("127.0.0.1", 9123);
			Thread.sleep(1000);
			
			// "krisv" executes his own performance evaluation
			BlockingTaskSummaryResponseHandler taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
			taskClient.getTasksAssignedAsPotentialOwner("krisv", "en-UK", taskSummaryHandler);
			TaskSummary task1 = taskSummaryHandler.getResults().get(0);
			System.out.println("Krisv executing task " + task1.getName() + "(" + task1.getId() + ": " + task1.getDescription() + ")");
			BlockingTaskOperationResponseHandler taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.start(task1.getId(), "krisv", taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.complete(task1.getId(), "krisv", null, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			Thread.sleep(1000);
			
			// "john", part of the "PM" group, executes a performance evaluation
			taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
			List<String> groups = new ArrayList<String>();
			groups.add("PM");
			taskClient.getTasksAssignedAsPotentialOwner("john", groups, "en-UK", taskSummaryHandler);
			TaskSummary task2 = taskSummaryHandler.getResults().get(0);
			System.out.println("John executing task " + task2.getName() + "(" + task2.getId() + ": " + task2.getDescription() + ")");
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.claim(task2.getId(), "john", groups, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.start(task2.getId(), "john", taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.complete(task2.getId(), "john", null, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			Thread.sleep(1000);
			
			// "mary", part of the "HR" group, delegates a performance evaluation
			taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
			groups = new ArrayList<String>();
			groups.add("HR");
			taskClient.getTasksAssignedAsPotentialOwner("mary", groups, "en-UK", taskSummaryHandler);
			TaskSummary task3 = taskSummaryHandler.getResults().get(0);
			System.out.println("Mary delegating task " + task3.getName() + "(" + task3.getId() + ": " + task3.getDescription() + ") to krisv");
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.claim(task3.getId(), "mary", groups, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.delegate(task3.getId(), "mary", "krisv", taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			
			// "administrator" delegates the task back to mary
			System.out.println("Administrator delegating task back to mary");
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.delegate(task3.getId(), "Administrator", "mary", taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			
			// mary executing the task
			taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
			taskClient.getTasksAssignedAsPotentialOwner("mary", "en-UK", taskSummaryHandler);
			TaskSummary task3b = taskSummaryHandler.getResults().get(0);
			System.out.println("Mary executing task " + task3b.getName() + "(" + task3b.getId() + ": " + task3b.getDescription() + ")");
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.start(task3b.getId(), "mary", taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.complete(task3b.getId(), "mary", null, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			Thread.sleep(1000);
			
			logger.close();
			System.exit(0);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("Evaluation2.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}
	
	private static class SystemEventListener implements org.drools.SystemEventListener {
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
