/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jbpm.task.wih;

import java.util.logging.LogManager;
import org.jbpm.shared.services.api.ServicesSessionManager;
import org.kie.api.runtime.KieSession;
import org.kie.internal.task.api.TaskService;


/**
 *
 * @author salaboy
 */
public class HTWorkItemHandlerFactory {
    public static LocalHTWorkItemHandler newHandler(KieSession ksession, TaskService taskService){
        ExternalTaskEventListener externalTaskEventListener = new ExternalTaskEventListener();
        externalTaskEventListener.setTaskService(taskService);
        externalTaskEventListener.addSession(ksession);
        externalTaskEventListener.setLogger(LogManager.getLogManager().getLogger(""));
        LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler();
        humanTaskHandler.addSession(ksession);
        humanTaskHandler.setTaskService(taskService);
        humanTaskHandler.setTaskEventListener(externalTaskEventListener);
        return humanTaskHandler;
    }
    
    public static LocalHTWorkItemHandler newHandler(ServicesSessionManager sessionManager, TaskService taskService){
        ExternalTaskEventListener externalTaskEventListener = new ExternalTaskEventListener();
        externalTaskEventListener.setTaskService(taskService);
        externalTaskEventListener.setLogger(LogManager.getLogManager().getLogger(""));
        
        LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler();
        humanTaskHandler.setSessionManager(sessionManager);
        humanTaskHandler.setTaskService(taskService);
        humanTaskHandler.setTaskEventListener(externalTaskEventListener);
        return humanTaskHandler;
    }
}
