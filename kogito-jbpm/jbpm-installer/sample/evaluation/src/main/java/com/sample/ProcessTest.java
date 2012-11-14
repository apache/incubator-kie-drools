package com.sample;

import java.util.HashMap;
import java.util.Map;

import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.logger.KnowledgeRuntimeLogger;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.HornetQHTWorkItemHandler;

/**
 * This is a sample file to launch a process.
 */
public class ProcessTest {

	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KnowledgeBase kbase = readKnowledgeBase();
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

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("Evaluation.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}
	
	private static StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		HornetQHTWorkItemHandler humanTaskHandler = new HornetQHTWorkItemHandler(ksession);
		humanTaskHandler.setIpAddress("127.0.0.1");
		humanTaskHandler.setPort(5153);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
		return ksession;
	}

	
}
