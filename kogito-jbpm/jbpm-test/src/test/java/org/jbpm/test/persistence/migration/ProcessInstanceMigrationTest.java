package org.jbpm.test.persistence.migration;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.workflow.instance.WorkflowProcessInstanceUpgrader;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;

public class ProcessInstanceMigrationTest extends JbpmJUnitBaseTestCase {

	public ProcessInstanceMigrationTest() {
		super(true, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessInstanceMigration() throws Exception {

		RuntimeManager manager = createRuntimeManager("migration/sample.bpmn2", "migration/sample2.bpmn2");
		RuntimeEngine engine = manager.getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();

		BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
		transactionManager.setTransactionTimeout(3600); // longer timeout
														// for a debugger

		ProcessInstance p = ksession.startProcess("com.sample.bpmn.migration");
		long pid = p.getId();

		assertEquals("com.sample.bpmn.migration", ksession.getProcessInstance(pid).getProcessId());

		// let john execute Task 1
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		assertNotNull(list);
		assertEquals(1, list.size());

		// upgrade to version to of the process
		UpgradeCommand c = new UpgradeCommand(pid);
		ksession.execute(c);

		TaskSummary task = list.get(0);
		taskService.start(task.getId(), "john");
		taskService.complete(task.getId(), "john", null);

		// in second version of the process second user task is for mary while
		// for first version it's for john
		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		assertNotNull(list);
		assertEquals(1, list.size());

		assertEquals("com.sample.bpmn.migration2", ksession.getProcessInstance(pid).getProcessId());

		EntityManager em = getEmf().createEntityManager();
		Query query = em.createQuery(
						"select p from ProcessInstanceInfo p where p.processInstanceId = :pid")
				.setParameter("pid", pid);
		List<ProcessInstanceInfo> found = query.getResultList();

		assertNotNull(found);
		assertEquals(1, found.size());

		ProcessInstanceInfo instance = found.get(0);
		assertEquals("com.sample.bpmn.migration2", instance.getProcessId());

		Thread.sleep(400);

		manager.disposeRuntimeEngine(engine);
	}

	private static class UpgradeCommand implements GenericCommand<Object> {

		private static final long serialVersionUID = -626809842544969669L;

		private long pid;

		private UpgradeCommand(long pid) {
			this.pid = pid;
		}

		public Object execute(org.kie.internal.command.Context arg0) {
			KieSession ksession = ((KnowledgeCommandContext) arg0).getKieSession();

			WorkflowProcessInstanceUpgrader.upgradeProcessInstance(ksession,
					pid, "com.sample.bpmn.migration2", null);

			return null;

		}


	}

}
