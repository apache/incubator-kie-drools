package org.jbpm.examples.humantask;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.AsyncMinaHTWorkItemHandler;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.DefaultEscalatedDeadlineHandler;
import org.jbpm.task.service.DefaultUserInfo;
import org.jbpm.task.service.EscalatedDeadlineHandler;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.task.utils.ContentMarshallerContext;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.subethamail.wiser.Wiser;

public class HumanTaskDeadlineExample {
	
    private static Wiser wiser;
    
	public static final void main(String[] args) {
		try {
		    setupTaskServer();
			// load up the knowledge base
			KnowledgeBase kbase = readKnowledgeBase();
			StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
			AsyncMinaHTWorkItemHandler asyncMinaHTWorkItemHandler = new AsyncMinaHTWorkItemHandler(ksession);

			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", asyncMinaHTWorkItemHandler);
			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", "krisv");
			params.put("s", "Need a new laptop computer");
			ksession.startProcess("UserTask", params);

			SystemEventListenerFactory.setSystemEventListener(new SystemEventListener());
			AsyncTaskService taskClient = asyncMinaHTWorkItemHandler.getClient();
			Thread.sleep(1000);
			
			// sleep to allow notification to be sent for deadline start
            Thread.sleep(6000);
//            Assert.assertEquals(4, wiser.getMessages().size());
//            Assert.assertEquals("admin@domain.com", wiser.getMessages().get(0).getEnvelopeReceiver());
//            Assert.assertEquals("mike@domain.com", wiser.getMessages().get(1).getEnvelopeReceiver());
//            Assert.assertEquals("Task is ready for mike", wiser.getMessages().get(0).getMimeMessage().getSubject());
            
			
			// wait another few seconds to trigger complete deadline
			Thread.sleep(6000);
//            Assert.assertEquals(6, wiser.getMessages().size());
//            Assert.assertEquals("admin@domain.com", wiser.getMessages().get(2).getEnvelopeReceiver());
//            Assert.assertEquals("mike@domain.com", wiser.getMessages().get(3).getEnvelopeReceiver());
//            Assert.assertEquals("Not completedTask is ready for mike", wiser.getMessages().get(4).getMimeMessage().getSubject());

            
            BlockingTaskSummaryResponseHandler taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
            taskClient.getTasksAssignedAsPotentialOwner("mike", "en-UK", taskSummaryHandler);
			TaskSummary task1 = taskSummaryHandler.getResults().get(0);

			BlockingTaskOperationResponseHandler taskOperationHandler = new BlockingTaskOperationResponseHandler();
			taskClient.start(task1.getId(), "mike", taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			taskOperationHandler = new BlockingTaskOperationResponseHandler();
			Map<String, Object> results = new HashMap<String, Object>();
			results.put("comment", "Agreed, existing laptop needs replacing");
			results.put("outcome", "Accept");
			ContentData contentData = ContentMarshallerHelper.marshal(results, new ContentMarshallerContext(), ksession.getEnvironment());
			taskClient.complete(task1.getId(), "mike", contentData, taskOperationHandler);
			taskOperationHandler.waitTillDone(1000);
			Thread.sleep(1000);
			
			
			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		wiser.stop();
		System.exit(0);
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("humantask/HumanTaskDeadline.bpmn"), ResourceType.BPMN2);
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
	
	private static void setupTaskServer() throws InterruptedException {
	    EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        
        EscalatedDeadlineHandler handler = buildDeadlineHnadler();
        
        TaskService taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener(), handler);
        
        // start server
        TaskServer taskServer = new MinaTaskServer(taskService, 9123);
        Thread serverThread = new Thread(taskServer);
        serverThread.start();
        
        Thread.sleep(1000);
        
        TaskServiceSession taskSession = taskService.createSession();
        taskSession.addUser( new User("john") );
        taskSession.addUser( new User("mike") );
        taskSession.addUser( new User("Administrator") );
        taskSession.dispose();
        
	}
	
	protected static EscalatedDeadlineHandler buildDeadlineHnadler() {
	    
	    wiser = new Wiser();
	    wiser.setHostname("localhost");
	    wiser.setPort(2345);        
	    wiser.start();
	    
        Properties emailProperties = new Properties();
        emailProperties.setProperty("from", "jbpm@domain.com");
        emailProperties.setProperty("replyTo", "jbpm@domain.com");
        emailProperties.setProperty("mail.smtp.host", "localhost");
        emailProperties.setProperty("mail.smtp.port", "2345");
        
        Properties userInfoProperties = new Properties();
        userInfoProperties.setProperty("john", "john@domain.com:en-UK:John");
        userInfoProperties.setProperty("mike", "mike@domain.com:en-UK:Mike");
    
        userInfoProperties.setProperty("Administrator", "admin@domain.com:en-UK:Admin");
        
            
        DefaultEscalatedDeadlineHandler handler = new DefaultEscalatedDeadlineHandler(emailProperties);
        handler.setUserInfo(new DefaultUserInfo(userInfoProperties));
        
        return handler;
    }

}
