package com.sample;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.utils.OnErrorAction;
import org.jbpm.test.JBPMHelper;
import org.kie.KieBase;
import org.kie.SystemEventListenerFactory;
import org.kie.api.builder.KnowledgeBuilder;
import org.kie.api.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.logger.KnowledgeRuntimeLogger;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to launch a process.
 */
public class ProcessTest {

	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KieBase kbase = readKnowledgeBase();
			StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, "test", 1000);
			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("employee", "krisv");
			params.put("reason", "Yearly performance evaluation");
			ksession.startProcess("com.sample.evaluation", params);
			System.out.println("Process started ...");
			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KieBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("Evaluation.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}
	
	private static StatefulKnowledgeSession createKnowledgeSession(KieBase kbase) {
		StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) kbase.newKieSession();
		UserGroupCallbackManager.getInstance().setCallback(
			new DefaultUserGroupCallbackImpl("classpath:/usergroups.properties"));
		JBPMHelper.setupDataSource();
		JBPMHelper.startH2Server();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		TaskService taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
		LocalTaskService localTaskService = new LocalTaskService(taskService);
		LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler(
			localTaskService, ksession, OnErrorAction.RETHROW);
		humanTaskHandler.connect();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
		return ksession;
	}

	
}
