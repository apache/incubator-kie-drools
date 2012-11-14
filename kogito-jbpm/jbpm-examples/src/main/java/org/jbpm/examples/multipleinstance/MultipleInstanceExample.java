package org.jbpm.examples.multipleinstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


public class MultipleInstanceExample {
	
	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KnowledgeBase kbase = readKnowledgeBase();
			StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, "test", 1000);
                        HornetQHTWorkItemHandler hornetQHTWorkItemHandler = new HornetQHTWorkItemHandler(ksession);
                        hornetQHTWorkItemHandler.setPort(5153);
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", hornetQHTWorkItemHandler);
			
			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			List<String> list = new ArrayList<String>();
			list.add("krisv");
			list.add("john doe");
			list.add("superman");
			params.put("list", list);
			ksession.startProcess("com.sample.multipleinstance", params);
			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("multipleinstance/multipleinstance.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}

}
