package org.jbpm.examples.looping;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;

public class LoopingExample {
	
	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KieBase kbase = readKnowledgeBase();
			KieSession ksession = kbase.newKieSession();
			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("count", 5);
			ksession.startProcess("com.sample.looping", params);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KieBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(KieServices.Factory.get().getResources().newClassPathResource("looping/Looping.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}

}
