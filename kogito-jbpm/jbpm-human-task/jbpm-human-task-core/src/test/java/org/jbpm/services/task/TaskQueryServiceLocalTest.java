package org.jbpm.services.task;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.kie.internal.task.api.InternalTaskService;

import bitronix.tm.resource.jdbc.PoolingDataSource;


public class TaskQueryServiceLocalTest extends TaskQueryServiceBaseTest {

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
}
