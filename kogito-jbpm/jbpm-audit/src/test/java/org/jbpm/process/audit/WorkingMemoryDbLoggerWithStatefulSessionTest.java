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

package org.jbpm.process.audit;

import org.drools.core.SessionConfiguration;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import java.util.Properties;

import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;

/**
 * This class tests the following classes:
 * <ul>
 * <li>WorkingMemoryDbLogger</li>
 * </ul>
 */
public class WorkingMemoryDbLoggerWithStatefulSessionTest extends AbstractWorkingMemoryDbLoggerTest {

    private KieSession session = null;

    @Override
    public ProcessInstance startProcess(String processId) {
        if (session == null) {
            // load the process
            KieBase kbase = createKnowledgeBase();
            // create a new session
            Properties properties = new Properties();
            properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
            properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
            SessionConfiguration config = SessionConfiguration.newInstance(properties);
            session = kbase.newKieSession(config, createEnvironment(context));
            
            new JPAWorkingMemoryDbLogger(session);
            session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        }
        return session.startProcess(processId);
    }

}