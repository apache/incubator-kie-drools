package org.drools.process;

import junit.framework.TestCase;

import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.core.RuleFlowProcessFactory;

public class ProcessFactoryTest extends TestCase {
	
	public void testProcessFactory() {
		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.drools.process");
		factory
			// header
			.name("My process").packageName("org.drools")
			// nodes
			.startNode(1).name("Start").done()
			.actionNode(2).name("Action")
				.action("java", "System.out.println(\"Action\");").done()
			.endNode(3).name("End").done()
			// connections
			.connection(1, 2)
			.connection(2, 3);
		RuleFlowProcess process = factory.validate().getProcess();
	}

}
