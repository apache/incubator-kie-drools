/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.runtime.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.process.audit.jms.AsyncAuditLogProducer;
import org.jbpm.runtime.manager.impl.deploy.AbstractDeploymentDescriptorTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class DefaultRegisterableItemsFactoryTest extends AbstractDeploymentDescriptorTest {

    private PoolingDataSource pds;
    private RuntimeManager manager;

    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
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
    public void testJmsAuditCacheInstance() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.jbpm.test.jms", "kjar-jms-audit", "1.0.0");

        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.persistence.jpa");
        customDescriptor.getBuilder()
                        .auditMode(AuditMode.JMS);
        Map<String, String> resources = new HashMap<String, String>();
        resources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());

        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
        installKjar(releaseId, kJar1);

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                                                                          .newDefaultBuilder(releaseId)
                                                                          .classLoader(this.getClass().getClassLoader())
                                                                          .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        assertNotNull(engine);
        AsyncAuditLogProducer asyncAuditLogProducer = null;
        KieSession kieSession = engine.getKieSession();
        for (ProcessEventListener listener : kieSession.getProcessEventListeners()) {
            if (listener instanceof AsyncAuditLogProducer) {
                asyncAuditLogProducer = (AsyncAuditLogProducer) listener;
                break;
            }
        }
        assertNotNull(asyncAuditLogProducer);

        manager.close();
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine engine2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());

        KieSession kieSession2 = engine2.getKieSession();
        AsyncAuditLogProducer asyncAuditLogProducer2 = null;
        for (ProcessEventListener listener : kieSession2.getProcessEventListeners()) {
            if (listener instanceof AsyncAuditLogProducer) {
                asyncAuditLogProducer2 = (AsyncAuditLogProducer) listener;
                break;
            }
        }
        assertNotNull(asyncAuditLogProducer2);
        // check if the instance is the same (cached)
        assertEquals(asyncAuditLogProducer, asyncAuditLogProducer2);
    }

}
