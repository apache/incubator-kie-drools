package org.jbpm.test;

import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a sample file to launch a process.
 */
public class ProcessMain {

    private static final Logger logger = LoggerFactory.getLogger(ProcessMain.class);
    
	public static final void main(String[] args) throws Exception {
		startUp();
		// load up the knowledge base
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = JBPMHelper.newStatefulKnowledgeSession(kbase);
		// start a new process instance
		ksession.startProcess("com.sample.bpmn.hello");
		logger.info("Process started ...");
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
