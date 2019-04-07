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
package org.jbpm.runtime.manager.impl;

import org.jbpm.runtime.manager.impl.task.SynchronizedTaskService;
import org.kie.api.runtime.KieSession;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.InternalTaskService;

/**
 * This is an extension of the regular <code>RuntimeEngine</code> implementation strictly dedicated to
 * <code>SingletonRuntimeManager</code> to ensure that access to <code>RuntimeEngine</code>
 * resources, such as <code>KieSession</code> and <code>TaskService</code>, remains synchronized.
 *
 */
public class SynchronizedRuntimeImpl extends RuntimeEngineImpl {

    private TaskService synchronizedTaskService;
    
    public SynchronizedRuntimeImpl(KieSession ksession, InternalTaskService taskService) {
        super(ksession, taskService);
        if (taskService != null) {
            this.synchronizedTaskService = new SynchronizedTaskService(ksession, taskService);
        }
    }

    @Override
    public TaskService getTaskService() {
    	if (synchronizedTaskService == null) {
    		throw new UnsupportedOperationException("TaskService was not configured");
    	}
        return this.synchronizedTaskService;
    }

}
