package org.jbpm.examples.quickstarts;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample file to test a process.
 */
public class JavaServiceQuickstartTest extends JbpmJUnitBaseTestCase {

	@Test
	public void testProcess() {
		KieSession ksession = createRuntimeManager("quickstarts/ScriptTask.bpmn").getRuntimeEngine(null).getKieSession();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("person", new Person("krisv"));
		ksession.startProcess("com.sample.script", params);
	}

}
