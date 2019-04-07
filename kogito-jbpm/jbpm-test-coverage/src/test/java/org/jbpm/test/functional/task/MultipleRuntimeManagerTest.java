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

package org.jbpm.test.functional.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.task.api.UserGroupCallback;

import static org.junit.Assert.*;

public class MultipleRuntimeManagerTest extends JbpmTestCase  {

	public MultipleRuntimeManagerTest() {
		super(true, true);
	}

    @Test
    public void testCreationOfRuntimeManagersConcurrently() throws Exception {
    	List<RuntimeManager> managers = new CopyOnWriteArrayList<RuntimeManager>();
    	
        HumanTaskResolver htr1 = new HumanTaskResolver(1, managers);
        HumanTaskResolver htr2 = new HumanTaskResolver(2, managers);

        Thread t1 = new Thread(htr1, "first");
        Thread t2 = new Thread(htr2, "second");

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        
        assertEquals(2, managers.size());
    }

    class HumanTaskResolver implements Runnable {

        private final long pid;
        private List<RuntimeManager> managers;

        public HumanTaskResolver(long pid, List<RuntimeManager> managers) {
            this.pid = pid;
            this.managers = managers;
        }

        @Override
        public void run() {
            RuntimeManager manager = getRuntimeManager("org/jbpm/test/functional/task/humantask.bpmn");
            managers.add(manager);
            manager.close();
        }

        private RuntimeManager getRuntimeManager(String process) {

            Properties properties = new Properties();
            properties.setProperty("john", "");

            UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);
            // load up the knowledge base

            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2);

            RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                    .persistence(false)
                    .entityManagerFactory(getEmf())
                    .userGroupCallback(userGroupCallback)
                    .knowledgeBase(kbuilder.newKieBase())
                    .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                        @Override
                        public List<TaskLifeCycleEventListener> getTaskListeners() {
                            List<TaskLifeCycleEventListener> defaultListeners = new ArrayList<TaskLifeCycleEventListener>();
                            defaultListeners.add(new JPATaskLifeCycleEventListener(false));
                            // add any custom listeners
                            defaultListeners.addAll(super.getTaskListeners());
                            // add listeners from deployment descriptor
                            defaultListeners.addAll(getTaskListenersFromDescriptor());

                            return defaultListeners;
                        }
                    })
                    .get();

            return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "exec-" + pid);
        }
    }
}
