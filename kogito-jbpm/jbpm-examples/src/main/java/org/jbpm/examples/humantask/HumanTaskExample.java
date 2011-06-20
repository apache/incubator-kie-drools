package org.jbpm.examples.humantask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

public class HumanTaskExample {
	
	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KnowledgeBase kbase = readKnowledgeBase();
			StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, "test", 1000);
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WSHumanTaskHandler());
			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", "krisv");
			params.put("description", "Need a new laptop computer");
			ksession.startProcess("com.sample.humantask", params);

			SystemEventListenerFactory.setSystemEventListener(new SystemEventListener());
			TaskClient taskClient = new TaskClient(new MinaTaskClientConnector("MinaConnector",
				new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
			taskClient.connect("127.0.0.1", 9123);
			Thread.sleep(1000);
			
			// "sales-rep" reviews request
			BlockingTaskSummaryResponseHandler taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
			List<String> groups = new ArrayList<String>();
			groups.add("sales");
			taskClient.getTasksAssignedAsPotentialOwner("sales-rep", groups, "en-UK", taskSummaryHandler);
			TaskSummary task1 = taskSummaryHandler.getResults().get(0);
			System.out.println("Sales-rep executing task " + task1.getName() + "(" + task1.getId() + ": " + task1.getDescription() + ")");
			BlockingTaskOperationResponseHandler taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.claim(task1.getId(), "sales-rep", groups, taskOperationHandler);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.start(task1.getId(), "sales-rep", taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			Map<String, Object> results = new HashMap<String, Object>();
			results.put("comment", "Agreed, existing laptop needs replacing");
			results.put("outcome", "Accept");
			ContentData contentData = new ContentData();
			contentData.setAccessType(AccessType.Inline);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(results);
				out.close();
				contentData = new ContentData();
				contentData.setContent(bos.toByteArray());
				contentData.setAccessType(AccessType.Inline);
			} catch (IOException e) {
				e.printStackTrace();
			}
			taskClient.complete(task1.getId(), "sales-rep", contentData, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			Thread.sleep(1000);
			
			// "krisv" approves result
			taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
			taskClient.getTasksAssignedAsPotentialOwner("krisv", "en-UK", taskSummaryHandler);
			TaskSummary task2 = taskSummaryHandler.getResults().get(0);
			System.out.println("krisv executing task " + task2.getName() + "(" + task2.getId() + ": " + task2.getDescription() + ")");
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.start(task2.getId(), "krisv", taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			results = new HashMap<String, Object>();
			results.put("outcome", "Agree");
			contentData = new ContentData();
			contentData.setAccessType(AccessType.Inline);
			bos = new ByteArrayOutputStream();
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(results);
				out.close();
				contentData = new ContentData();
				contentData.setContent(bos.toByteArray());
				contentData.setAccessType(AccessType.Inline);
			} catch (IOException e) {
				e.printStackTrace();
			}
			taskClient.complete(task2.getId(), "krisv", contentData, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			Thread.sleep(1000);
			
			// "john" as manager reviews request
			taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
			groups = new ArrayList<String>();
			groups.add("PM");
			taskClient.getTasksAssignedAsPotentialOwner("john", groups, "en-UK", taskSummaryHandler);
			TaskSummary task3 = taskSummaryHandler.getResults().get(0);
			System.out.println("john executing task " + task3.getName() + "(" + task3.getId() + ": " + task3.getDescription() + ")");
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.claim(task3.getId(), "john", groups, taskOperationHandler);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.start(task3.getId(), "john", taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			results = new HashMap<String, Object>();
			results.put("outcome", "Agree");
			contentData = new ContentData();
			contentData.setAccessType(AccessType.Inline);
			bos = new ByteArrayOutputStream();
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(results);
				out.close();
				contentData = new ContentData();
				contentData.setContent(bos.toByteArray());
				contentData.setAccessType(AccessType.Inline);
			} catch (IOException e) {
				e.printStackTrace();
			}
			taskClient.complete(task3.getId(), "john", contentData, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			Thread.sleep(1000);
			
			// "sales-rep" gets notification
			taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
			taskClient.getTasksAssignedAsPotentialOwner("sales-rep", "en-UK", taskSummaryHandler);
			TaskSummary task4 = taskSummaryHandler.getResults().get(0);
			System.out.println("sales-rep executing task " + task4.getName() + "(" + task4.getId() + ": " + task4.getDescription() + ")");
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.start(task4.getId(), "sales-rep", taskOperationHandler);
			BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
			taskClient.getTask(task4.getId(), getTaskResponseHandler);
			Task task = getTaskResponseHandler.getTask();
			BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
			taskClient.getContent(task.getTaskData().getDocumentContentId(), getContentResponseHandler);
			Content content = getContentResponseHandler.getContent();
			ByteArrayInputStream bis = new ByteArrayInputStream(content.getContent());
			ObjectInputStream in;
			try {
				in = new ObjectInputStream(bis);
				Object result = in.readObject();
				in.close();
				Map<?, ?> map = (Map<?, ?>) result;
				for (Map.Entry<?, ?> entry: map.entrySet()) {
					System.out.println(entry.getKey() + " = " + entry.getValue());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.complete(task4.getId(), "sales-rep", null, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			Thread.sleep(1000);
			
			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("humantask/HumanTask.bpmn"), ResourceType.BPMN2);
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
