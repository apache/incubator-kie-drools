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

import java.util.Properties;

import org.drools.impl.EnvironmentFactory;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

public class BPMN2Test extends JbpmBpmn2TestCase {
	
	public void testResourceType() {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MinimalProcess.bpmn2"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
		ksession.startProcess("Minimal");
	}

    public void testMultipleProcessInOneFile() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MultipleProcessInOneFile.xml"), ResourceType.BPMN2);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
        ProcessInstance processInstance = ksession.startProcess("Evaluation");
        assertNotNull(processInstance);
        ProcessInstance processInstance2 = ksession.startProcess("Simple");
        assertNotNull(processInstance2);
    }

}
