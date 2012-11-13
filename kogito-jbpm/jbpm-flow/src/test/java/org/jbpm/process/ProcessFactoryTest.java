/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process;

import org.jbpm.JbpmTestCase;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;

public class ProcessFactoryTest extends JbpmTestCase {
	
	public void testProcessFactory() {
		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.kie.process");
		factory
			// header
			.name("My process").packageName("org.kie")
			// nodes
			.startNode(1).name("Start").done()
			.actionNode(2).name("Action")
				.action("java", "System.out.println(\"Action\");").done()
			.endNode(3).name("End").done()
			// connections
			.connection(1, 2)
			.connection(2, 3);
		factory.validate().getProcess();
	}

}
