package org.jbpm.examples.quickstarts;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Test;
import org.kie.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to test a process.
 */
public class JavaServiceQuickstartTest extends JbpmJUnitTestCase {

	@Test
	public void testProcess() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("quickstarts/ScriptTask.bpmn");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("person", new Person("krisv"));
		ksession.startProcess("com.sample.script", params);
	}

}