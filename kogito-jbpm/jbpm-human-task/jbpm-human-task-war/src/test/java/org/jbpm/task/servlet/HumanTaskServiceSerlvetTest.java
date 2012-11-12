package org.jbpm.task.servlet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Properties;

import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallback;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.service.DefaultEscalatedDeadlineHandler;
import org.jbpm.task.service.EscalatedDeadlineHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.hornetq.HornetQTaskClientConnector;
import org.jbpm.task.service.hornetq.HornetQTaskClientHandler;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.junit.Before;
import org.junit.Test;
import org.kie.SystemEventListenerFactory;


public class HumanTaskServiceSerlvetTest {

    @Before
    public void prepare() {
        UserGroupCallbackManager.getInstance().setCallback(null);
    }
	
	@Test
	public void testDefaultMinaConfiguration() {
		String host = "localhost";
		String port = "9123";
		Properties parameters = new Properties();
		parameters.setProperty("active.config", "mina");
		parameters.setProperty("task.persistence.unit", "org.jbpm.task.test");
		
		
		HumanTaskServiceServlet servlet = new JUnitHumanTaskServiceServlet(parameters);
		try {
			servlet.init();
			Thread.sleep(1000);
			TaskClient client = getMinaTaskClient();
			boolean connected = client.connect(host, Integer.parseInt(port));
			assertTrue(connected);
			
			// check user callback handler
			UserGroupCallback callback = UserGroupCallbackManager.getInstance().getCallback();
			assertNull(callback);
			
			servlet.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed wiht exception " + e.getMessage());
			
		}
	}
	
	@Test
	public void testMinaConfigurationDifferentPort() {
		String host = "localhost";
		String port = "9321";
		Properties parameters = new Properties();
		parameters.setProperty("active.config", "mina");
		parameters.setProperty("task.persistence.unit", "org.jbpm.task.test");
		parameters.setProperty("mina.port", port);
		
		
		HumanTaskServiceServlet servlet = new JUnitHumanTaskServiceServlet(parameters);
		try {
			servlet.init();
			Thread.sleep(1000);
			TaskClient client = getMinaTaskClient();
			boolean connected = client.connect(host, Integer.parseInt(port));
			assertTrue(connected);
			
			// check user callback handler
			UserGroupCallback callback = UserGroupCallbackManager.getInstance().getCallback();
			assertNull(callback);
			
			servlet.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed wiht exception " + e.getMessage());
			
		}
	}
	
	@Test
	public void testDefaultHornetQConfiguration() {
		String host = "localhost";
		String port = "5153";
		Properties parameters = new Properties();
		parameters.setProperty("active.config", "hornetq");
		parameters.setProperty("task.persistence.unit", "org.jbpm.task.test");
		
		
		HumanTaskServiceServlet servlet = new JUnitHumanTaskServiceServlet(parameters);
		try {
			servlet.init();
			Thread.sleep(1000);
			TaskClient client = getHornetQTaskClient();
			boolean connected = client.connect(host, Integer.parseInt(port));
			assertTrue(connected);
			
			// check user callback handler
			UserGroupCallback callback = UserGroupCallbackManager.getInstance().getCallback();
			assertNull(callback);
			
			servlet.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed wiht exception " + e.getMessage());
			
		}
	}
	
	@Test
	public void testHornetQConfigurationDifferentPort() {
		String host = "localhost";
		String port = "4556";
		Properties parameters = new Properties();
		parameters.setProperty("active.config", "hornetq");
		parameters.setProperty("task.persistence.unit", "org.jbpm.task.test");
		parameters.setProperty("hornetq.port", port);
		
		
		HumanTaskServiceServlet servlet = new JUnitHumanTaskServiceServlet(parameters);
		try {
			servlet.init();
			Thread.sleep(1000);
			TaskClient client = getHornetQTaskClient();
			boolean connected = client.connect(host, Integer.parseInt(port));
			assertTrue(connected);
			
			// check user callback handler
			UserGroupCallback callback = UserGroupCallbackManager.getInstance().getCallback();
			assertNull(callback);
			servlet.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed wiht exception " + e.getMessage());
			
		}
	}
	
	@Test
	public void testDefaultConfiguration() {
		String host = "localhost";
		String port = "5153";
		Properties parameters = new Properties();
		parameters.setProperty("task.persistence.unit", "org.jbpm.task.test");
		
		
		HumanTaskServiceServlet servlet = new JUnitHumanTaskServiceServlet(parameters);
		try {
			servlet.init();
			Thread.sleep(1000);
			TaskClient client = getHornetQTaskClient();
			boolean connected = client.connect(host, Integer.parseInt(port));
			assertTrue(connected);
			
			// check user callback handler
			UserGroupCallback callback = UserGroupCallbackManager.getInstance().getCallback();
			assertNull(callback);
			
			TaskService service = getTaskService(servlet.getServer());
			assertNotNull(service);
			
			EscalatedDeadlineHandler escalationHandler = getEscalationHandler(service);
			assertNotNull(escalationHandler);
			assertTrue(escalationHandler instanceof DefaultEscalatedDeadlineHandler);
//			assertTrue(((DefaultEscalatedDeadlineHandler)escalationHandler).getUserInfo() instanceof DefaultUserInfo);
			
			servlet.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed wiht exception " + e.getMessage());
			
		}
	}
	
	@Test
	public void testDefaultTransportConfigurationWithCustomUserGroupCallback() {
		String host = "localhost";
		String port = "5153";
		Properties parameters = new Properties();
		parameters.setProperty("task.persistence.unit", "org.jbpm.task.test");
		parameters.setProperty("user.group.callback.class", CustomUserGroupCallbackImpl.class.getName());
		
		HumanTaskServiceServlet servlet = new JUnitHumanTaskServiceServlet(parameters);
		try {
			servlet.init();
			Thread.sleep(1000);
			TaskClient client = getHornetQTaskClient();
			boolean connected = client.connect(host, Integer.parseInt(port));
			assertTrue(connected);
			
			// check user callback handler
			UserGroupCallback callback = UserGroupCallbackManager.getInstance().getCallback();
			assertTrue(callback instanceof CustomUserGroupCallbackImpl);
			
			TaskService service = getTaskService(servlet.getServer());
			assertNotNull(service);
			
			EscalatedDeadlineHandler escalationHandler = getEscalationHandler(service);
			assertNotNull(escalationHandler);
			assertTrue(escalationHandler instanceof DefaultEscalatedDeadlineHandler);
//			assertTrue(((DefaultEscalatedDeadlineHandler)escalationHandler).getUserInfo() instanceof DefaultUserInfo);
			
			servlet.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed wiht exception " + e.getMessage());
			
		}
	}
	
	@Test
	public void testDefaultTransportConfigurationWithCustomEscalatedDeadlineHandler() {
		String host = "localhost";
		String port = "5153";
		Properties parameters = new Properties();
		parameters.setProperty("task.persistence.unit", "org.jbpm.task.test");
		parameters.setProperty("escalated.deadline.handler.class", CustomEscalatedDeadlineHandler.class.getName());
		
		HumanTaskServiceServlet servlet = new JUnitHumanTaskServiceServlet(parameters);
		try {
			servlet.init();
			Thread.sleep(1000);
			TaskClient client = getHornetQTaskClient();
			boolean connected = client.connect(host, Integer.parseInt(port));
			assertTrue(connected);
			
			TaskService service = getTaskService(servlet.getServer());
			assertNotNull(service);
			
			EscalatedDeadlineHandler escalationHandler = getEscalationHandler(service);
			assertNotNull(escalationHandler);
			assertTrue(escalationHandler instanceof CustomEscalatedDeadlineHandler);
			
			
			servlet.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed wiht exception " + e.getMessage());
			
		}
	}
	
	@Test
	public void testDefaultTransportConfigurationWithCustomUserInfo() {
		String host = "localhost";
		String port = "5153";
		Properties parameters = new Properties();
		parameters.setProperty("task.persistence.unit", "org.jbpm.task.test");
		parameters.setProperty("user.info.class", CustomUserInfo.class.getName());
		
		HumanTaskServiceServlet servlet = new JUnitHumanTaskServiceServlet(parameters);
		try {
			servlet.init();
			Thread.sleep(1000);
			TaskClient client = getHornetQTaskClient();
			boolean connected = client.connect(host, Integer.parseInt(port));
			assertTrue(connected);
			
			
			TaskService service = getTaskService(servlet.getServer());
			assertNotNull(service);
			
			EscalatedDeadlineHandler escalationHandler = getEscalationHandler(service);
			assertNotNull(escalationHandler);
			assertTrue(escalationHandler instanceof DefaultEscalatedDeadlineHandler);
			assertTrue(((DefaultEscalatedDeadlineHandler)escalationHandler).getUserInfo() instanceof CustomUserInfo);
			
			servlet.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed wiht exception " + e.getMessage());
			
		}
	}
	
	
	
    @Test
    public void testDefaultConfigurationWithDefaultUserGroupCallbackImpl() {
        String host = "localhost";
        String port = "5153";
        Properties parameters = new Properties();
        parameters.setProperty("task.persistence.unit", "org.jbpm.task.test");
        parameters.setProperty("user.group.callback.class", DefaultUserGroupCallbackImpl.class.getName());
        
        HumanTaskServiceServlet servlet = new JUnitHumanTaskServiceServlet(parameters);
        try {
            servlet.init();
            Thread.sleep(1000);
            TaskClient client = getHornetQTaskClient();
            boolean connected = client.connect(host, Integer.parseInt(port));
            assertTrue(connected);
            
            // check user callback handler
            UserGroupCallback callback = UserGroupCallbackManager.getInstance().getCallback();
            assertTrue(callback instanceof DefaultUserGroupCallbackImpl);
            
            servlet.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed wiht exception " + e.getMessage());
            
        }
    }
	
	private TaskClient getMinaTaskClient() {
		
		return new TaskClient(new MinaTaskClientConnector("test", new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
	}
	
	private TaskClient getHornetQTaskClient() {
		
		return new TaskClient(new HornetQTaskClientConnector("test", new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
	}
	
	// reflection method to inspect if everything is set properly
	private TaskService getTaskService(TaskServer taskServer) {
		try {
			Field handlerField = taskServer.getClass().getSuperclass().getDeclaredField("handler");
			handlerField.setAccessible(true);
			Object firstHandler = handlerField.get(taskServer);
			
			handlerField = firstHandler.getClass().getDeclaredField("handler");
			handlerField.setAccessible(true);
			Object secondHandler = handlerField.get(firstHandler);
			
			Field serviceField = secondHandler.getClass().getDeclaredField("service");
			serviceField.setAccessible(true);
			Object service = serviceField.get(secondHandler);
			
			return (TaskService) service;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	private EscalatedDeadlineHandler getEscalationHandler(TaskService service) {
		
		try { 
			Field handlerField = service.getClass().getDeclaredField("escalatedDeadlineHandler");
			handlerField.setAccessible(true);
		
		return (EscalatedDeadlineHandler) handlerField.get(service);
		} catch (Exception e) {
			return null;
		}
	}
}
