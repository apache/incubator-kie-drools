/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

/**
 * This is a sample file to launch a process.
 */
public class HandlerTest extends AbstractBaseTest {

    @Test
	public void testHandler() throws Exception {
		KieBase kbase = readKnowledgeBase();
		KieSession ksession = createSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Handler", new JavaHandlerWorkItemHandler(ksession));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employeeId", "12345-ABC");
		ksession.startProcess("com.sample.bpmn.java", params);
	}

	private static KieBase readKnowledgeBase() throws Exception {
		ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
		ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("JavaHandler.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKieBase();
	}
	
	private static KieSession createSession(KieBase kbase) {
		Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
		KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
		return kbase.newKieSession(config, KieServices.get().newEnvironment());
	}
}
