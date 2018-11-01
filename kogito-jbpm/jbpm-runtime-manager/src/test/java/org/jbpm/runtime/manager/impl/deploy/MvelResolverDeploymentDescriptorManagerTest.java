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

package org.jbpm.runtime.manager.impl.deploy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class MvelResolverDeploymentDescriptorManagerTest extends AbstractDeploymentDescriptorTest {
	protected static final String ARTIFACT_ID = "test-module";
	protected static final String GROUP_ID = "org.jbpm.test";
	protected static final String VERSION = "1.0.0";

	private PoolingDataSource pds;
	private RuntimeManager manager;

	@Before
	public void setup() {
		TestUtil.cleanupSingletonSessionId();
		pds = TestUtil.setupPoolingDataSource();
		Properties properties = new Properties();
		properties.setProperty("mary", "HR");
		properties.setProperty("john", "HR");
	}

	@After
	public void teardown() {
		if (manager != null) {
			manager.close();
		}
		EntityManagerFactoryManager.get().clear();
		pds.close();
	}

	@Test
	public void testDeploymentDescriptorFromKieContainer() throws IOException {

		Map<String, String> resources = new HashMap<String, String>();

		KieServices ks = KieServices.Factory.get();
		ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);

		String kmoduleString = IOUtils.toString(this.getClass()
				.getResourceAsStream("/kmodule-custom-wih.xml"), "UTF-8");

		resources.put("src/main/resources/META-INF/kmodule.xml", kmoduleString);
		String processString = IOUtils.toString(this.getClass()
				.getResourceAsStream("/BPMN2-CustomTask.bpmn2"), "UTF-8");
		resources.put("src/main/resources/BPMN2-CustomTask.bpmn2",
				processString);

		InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
		installKjar(releaseId, kJar1);

		Properties properties = new Properties();
		UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(
				properties);

		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory
				.get().newDefaultBuilder(releaseId)
				.userGroupCallback(userGroupCallback).get();

		manager = RuntimeManagerFactory.Factory.get()
				.newPerProcessInstanceRuntimeManager(environment);

		manager.getRuntimeEngine(ProcessInstanceIdContext.get())
				.getKieSession().startProcess("customtask");

	}

}
