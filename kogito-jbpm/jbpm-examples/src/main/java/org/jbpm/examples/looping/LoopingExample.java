package org.jbpm.examples.looping;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManagerFactory;

public class LoopingExample {
	
	public static final void main(String[] args) {
		try {
			// load up the knowledge session
			KieSession ksession = getKieSession();
			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("count", 5);
			ksession.startProcess("com.sample.looping", params);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KieSession getKieSession() throws Exception {
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder()
            .addAsset(KieServices.Factory.get().getResources()
        		.newClassPathResource("looping/Looping.bpmn"), ResourceType.BPMN2)
            .get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment).getRuntimeEngine(null).getKieSession();
	}

}
