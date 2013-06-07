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

package org.jbpm.bpmn2;

import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.junit.Test;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;

public class ProcessFactoryTest extends JbpmBpmn2TestCase {
    
    public ProcessFactoryTest() {
        super(false);
    }

    @Test
	public void testProcessFactory() throws Exception {
		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.process");
		factory
			// header
			.name("My process").packageName("org.jbpm")
			// nodes
			.startNode(1).name("Start").done()
			.actionNode(2).name("Action")
				.action("java", "System.out.println(\"Action\");").done()
			.endNode(3).name("End").done()
			// connections
			.connection(1, 2)
			.connection(2, 3);
		RuleFlowProcess process = factory.validate().getProcess();
		Resource res = ResourceFactory.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
		res.setSourcePath("/tmp/processFactory.bpmn2"); // source path or target path must be set to be added into kbase
		KieBase kbase = createKnowledgeBaseFromResources(res);
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.startProcess("org.jbpm.process");
		ksession.dispose();
	}

}
