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

package org.jbpm.runtime.manager.handler;

import org.junit.Assert;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;

public class MvelPropertiesWorkItemHandler implements WorkItemHandler {
	
	private KieSession ksession;
	private TaskService taskService;
	private RuntimeManager runtimeManager;
	private ClassLoader classLoader;
	
	public MvelPropertiesWorkItemHandler(KieSession ksession, TaskService taskService, RuntimeManager runtimeManager, ClassLoader classloader) {
		this.ksession = ksession;
		this.taskService = taskService;
		this.runtimeManager = runtimeManager;
		this.classLoader = classloader;
	}

	@Override
	public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {
		Assert.assertNotNull(this.ksession);
		Assert.assertNotNull(this.taskService);
		Assert.assertNotNull(this.runtimeManager);
		Assert.assertNotNull(this.classLoader);
		arg1.completeWorkItem(arg0.getId(),null);
	}
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
	}

}
