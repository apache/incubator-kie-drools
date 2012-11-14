package org.jbpm.task.service.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.kie.SystemEventListenerFactory;
import org.easymock.EasyMock;
import org.jbpm.task.Group;
import org.jbpm.task.I18NText;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;

/**
 * Test case to see if this component works.
 * 
 */
public class JMSTaskServerTest extends TestCase {

	/**
	 * Initial context
	 */
	private Context context;
	
	/**
	 * server instance
	 */
	private TaskServer server;
	
	/**
	 * Starts the server
	 */
	@Override
	protected void setUp() throws Exception {

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
		
		this.context = EasyMock.createMock(Context.class);
		EasyMock.expect(context.lookup("ConnectionFactory")).andReturn(factory).anyTimes();
		EasyMock.replay(context);
		
		EntityManagerFactory localEntityManagerFactory = Persistence.createEntityManagerFactory("org.jbpm.task");
		TaskService localTaskService = new TaskService(localEntityManagerFactory, SystemEventListenerFactory.getSystemEventListener());
		TaskServiceSession localTaskServiceSession = localTaskService.createSession();
		for (int i = 0; i < 10; i++) {
			User user = new User("usr" + i);
			localTaskServiceSession.addUser(user);
		}
		for (int j = 0; j < 3; j++) {
			localTaskServiceSession.addGroup(new Group("grp" + j));
		}

  		Properties serverProperties = new Properties();
		serverProperties.setProperty("JMSTaskServer.connectionFactory", "ConnectionFactory");
		serverProperties.setProperty("JMSTaskServer.transacted", "true");
		serverProperties.setProperty("JMSTaskServer.acknowledgeMode", "AUTO_ACKNOWLEDGE");
		serverProperties.setProperty("JMSTaskServer.queueName", "tasksQueue");
		serverProperties.setProperty("JMSTaskServer.responseQueueName", "tasksResponseQueue");
  		
		this.server = new JMSTaskServer(localTaskService, serverProperties, context);
		Thread thread = new Thread(this.server);
		thread.start();
		localTaskServiceSession.dispose();
	}
	
	/**
	 * Creates a new client
	 * @return the created client.
	 */
	protected TaskClient createTaskClient() {
		Properties clientProperties = new Properties();
		clientProperties.setProperty("JMSTaskClient.connectionFactory", "ConnectionFactory");
		clientProperties.setProperty("JMSTaskClient.transactedQueue", "true");
		clientProperties.setProperty("JMSTaskClient.acknowledgeMode", "AUTO_ACKNOWLEDGE");
		clientProperties.setProperty("JMSTaskClient.queueName", "tasksQueue");
		clientProperties.setProperty("JMSTaskClient.responseQueueName", "tasksResponseQueue");
		TaskClient client = new TaskClient(
				new JMSTaskClientConnector(
						"org.jbpm.process.workitem.wsht.WSThroughJMSHumanTaskHandler",
						new JMSTaskClientHandler(SystemEventListenerFactory.getSystemEventListener()),
						clientProperties, context
				)
		);
		
		return client;
	}
	
	/**
	 * Stops the server
	 */
	@Override
	protected void tearDown() throws Exception {
		server.stop();
	}
	
	/**
	 * Tests two consecutive connections to see how it works.
	 * @throws Exception
	 */
	public void testDoubleUsage() throws Exception {
		
		while(!server.isRunning()) {
			Thread.sleep(100); // waits until the server finishes the startup
		}
		
		TaskClient client = createTaskClient();
		
		client.connect();
		
		Task task = new Task();
		List<I18NText> names1 = new ArrayList<I18NText>();
		I18NText text1 = new I18NText("en-UK", "tarea1");
		names1.add(text1);
		task.setNames(names1);
		TaskData taskData = new TaskData();
		taskData.setStatus(Status.Created);
		taskData.setCreatedBy(new User("usr0"));
		taskData.setActualOwner(new User("usr0"));
		task.setTaskData(taskData);
		
		ContentData data = new ContentData();
		BlockingAddTaskResponseHandler addTaskHandler = new BlockingAddTaskResponseHandler();
		client.addTask(task, data, addTaskHandler);
		
		long taskId = addTaskHandler.getTaskId();

		client.disconnect();
		
		client.connect();
		
		assertTrue("taskId debe ser un valor mayor a cero", taskId > 0);
		
		Task task2 = new Task();
		List<I18NText> names2 = new ArrayList<I18NText>();
		I18NText text2 = new I18NText("en-UK", "tarea1");
		names2.add(text2);
		task2.setNames(names2);
		TaskData taskData2 = new TaskData();
		taskData2.setStatus(Status.Created);
		taskData2.setCreatedBy(new User("usr0"));
		taskData2.setActualOwner(new User("usr0"));
		task2.setTaskData(taskData2);
	    
		ContentData data2 = new ContentData();
		BlockingAddTaskResponseHandler addTaskHandler2 = new BlockingAddTaskResponseHandler();
		client.addTask(task2, data2, addTaskHandler2);
		
		long taskId2 = addTaskHandler2.getTaskId();
		
		assertTrue("taskId2 debe ser un valor mayor a cero", taskId2 > 0);
		assertNotSame("taskId y taskId2 deben ser distintos", taskId, taskId2);
		
		client.disconnect();
	}
	
}
