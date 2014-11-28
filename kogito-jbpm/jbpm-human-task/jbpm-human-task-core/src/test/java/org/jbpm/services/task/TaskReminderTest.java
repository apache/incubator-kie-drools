package org.jbpm.services.task;


import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.impl.EnvironmentFactory;
import org.drools.persistence.TransactionManager;
import org.jbpm.persistence.JpaProcessPersistenceContextManager;
import org.jbpm.persistence.jta.ContainerManagedTransactionManager;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.persistence.JPATaskPersistenceContextManager;
import org.jbpm.services.task.query.DeadlineSummaryImpl;
import org.jbpm.services.task.utils.ClassUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.model.Task;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskPersistenceContextManager;
import org.kie.internal.task.api.model.InternalTask;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class TaskReminderTest extends HumanTaskServicesBaseTest {
	
	private PoolingDataSource pds;
	private EntityManagerFactory emf;
	@Before
	public void setup() {
		pds = setupPoolingDataSource();
		emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );
		this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
												.entityManagerFactory(emf)
												.getTaskService();
	}
	
	@After
	public void clean() {
		super.tearDown();
		if (emf != null) {
			emf.close();
		}
		if (pds != null) {
			pds.close();
		}
	}
	
	 @Test
	 public void testTaskReminderWithoutNotification() {
		 Map<String, Object> vars = new HashMap<String, Object>();
	        vars.put("now", new Date());

	     Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.ReminderWithoutNotification));
	     Task task = (Task) TaskFactory.evalTask(reader, vars);
	     System.out.println("testTaskReminderWithoutNotification "+task.getTaskData().getStatus());
	     InternalTask iTask = (InternalTask)task;
	     org.junit.Assert.assertEquals(0,iTask.getDeadlines().getEndDeadlines().size());
	     org.junit.Assert.assertEquals(0,iTask.getDeadlines().getStartDeadlines().size());
	     long taskId = taskService.addTask(task, new HashMap<String, Object>());
	     org.junit.Assert.assertEquals(0,0);
		 taskService.executeReminderForTask(taskId, "Luke Cage");
	 }
	 
	 @Test
	 public void testTaskReminderWithNotificationByTaskNostarted() {
		 Map<String, Object> vars = new HashMap<String, Object>();
	        vars.put("now", new Date());

	     Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.ReminderWithNotificationReserved));
	     Task task = (Task) TaskFactory.evalTask(reader, vars);
	     System.out.println("testTaskReminderWithNotificationByTaskNostarted "+task.getTaskData().getStatus());
	     InternalTask iTask = (InternalTask)task;
	     org.junit.Assert.assertEquals(1,iTask.getDeadlines().getEndDeadlines().size());
	     org.junit.Assert.assertEquals(1,iTask.getDeadlines().getStartDeadlines().size());
	     long taskId = taskService.addTask(task, new HashMap<String, Object>());
	     taskService.executeReminderForTask(taskId, "Luke Cage");
	 }
	 
	 @Test
	 public void testTaskReminderWithNotificationByTaskNoCompleted() {
		 Map<String, Object> vars = new HashMap<String, Object>();
	        vars.put("now", new Date());

	     Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.ReminderWithNotificationInProgress));
	     Task task = (Task) TaskFactory.evalTask(reader, vars);
	     System.out.println("testTaskReminderWithNotificationByTaskNoCompleted "+task.getTaskData().getStatus());
	     InternalTask iTask = (InternalTask)task;
	     org.junit.Assert.assertEquals(1,iTask.getDeadlines().getEndDeadlines().size());
	     org.junit.Assert.assertEquals(1,iTask.getDeadlines().getStartDeadlines().size());
	     long taskId = taskService.addTask(task, new HashMap<String, Object>());
	     taskService.executeReminderForTask(taskId, "Luke Cage");
	 }
}
