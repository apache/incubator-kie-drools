package org.jbpm;

import org.jbpm.test.JBPMHelper;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to launch a process.
 */
public class ProcessMain {

	public static final void main(String[] args) throws Exception {
		startUp();
		// load up the knowledge base
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = JBPMHelper.newStatefulKnowledgeSession(kbase);
		// start a new process instance
		ksession.startProcess("com.sample.bpmn.hello");
		System.out.println("Process started ...");
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("humantask.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}
	
	private static void startUp() {
		JBPMHelper.startH2Server();
		JBPMHelper.setupDataSource();
		// please comment this line if you already have the task service running,
		// for example when running the jbpm-installer
		JBPMHelper.startTaskService();
	}
	
}