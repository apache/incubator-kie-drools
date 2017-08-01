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

package org.jbpm.services.cdi.impl.manager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.TaskServiceFactory;

@ApplicationScoped
public class RuntimeManagerFactoryCDIImpl extends RuntimeManagerFactoryImpl {

    @Inject
    private Instance<TaskServiceFactory> taskServiceFactoryInjected;
    
    @Override
    protected TaskServiceFactory getTaskServiceFactory(RuntimeEnvironment environment) {
    	
    	// if there is an implementation of TaskServiceFactory in the environment then use it
        TaskServiceFactory taskServiceFactory = (TaskServiceFactory) ((SimpleRuntimeEnvironment)environment).getEnvironmentTemplate()
        											.get("org.kie.internal.runtime.manager.TaskServiceFactory");
        if (taskServiceFactory != null) {
        	return taskServiceFactory;
        }
        try {
            taskServiceFactory = taskServiceFactoryInjected.get();
            // since this is CDI let's make sure it has all dependencies met
            taskServiceFactory.newTaskService().toString();
        } catch (Exception e) {
            taskServiceFactory = new LocalTaskServiceFactory(environment);
        }
        
        return taskServiceFactory;
    }
}
