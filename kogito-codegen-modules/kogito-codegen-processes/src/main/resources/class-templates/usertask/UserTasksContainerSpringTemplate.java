/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.usertask.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.kie.kogito.Application;
import org.kie.kogito.uow.events.UnitOfWorkUserTaskEventListener;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.impl.KogitoUserTaskEventSupportImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.kogito.usertask.UserTask;
import org.kie.kogito.usertask.UserTaskConfig;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTaskInstances;

@org.springframework.web.context.annotation.ApplicationScope
@org.springframework.stereotype.Component
public class UserTasks implements org.kie.kogito.usertask.UserTasks {

    private static Logger LOG = LoggerFactory.getLogger(UserTasks.class);

    @org.springframework.beans.factory.annotation.Autowired
    Collection<UserTask> userTasks;

    @org.springframework.beans.factory.annotation.Autowired
    Application application;

    private Map<String, UserTask> mappedUserTask = new HashMap<>();

    @jakarta.annotation.PostConstruct
    public void setup() {
        UserTaskInstances userTaskInstances = application.config().get(UserTaskConfig.class).userTaskInstances();
        userTaskInstances.setDisconnectUserTaskInstance(this::disconnect);
        userTaskInstances.setReconnectUserTaskInstance(this::connect);

        for (UserTask userTask : userTasks) {
            mappedUserTask.put(userTask.id(), userTask);
            LOG.info("Registering user task {} with task name {}", userTask.id(), userTask.getTaskName());
        }
    }

    public UserTask userTaskById(String userTaskId) {
        return mappedUserTask.get(userTaskId);
    }

    public Collection<String> userTaskIds() {
        return mappedUserTask.keySet();
    }

    @Override
    public UserTaskInstances instances() {
        return application.config().get(UserTaskConfig.class).userTaskInstances();
    }

    private UserTaskInstance disconnect(UserTaskInstance userTaskInstance) {
        DefaultUserTaskInstance instance = (DefaultUserTaskInstance) userTaskInstance;
        instance.setUserTask(null);
        instance.setUserTaskEventSupport(null);
        instance.setUserTaskLifeCycle(null);
        instance.setInstances(null);
        return instance;
    }

    public UserTaskInstance connect(UserTaskInstance userTaskInstance) {
        DefaultUserTaskInstance instance = (DefaultUserTaskInstance) userTaskInstance;
        UserTaskConfig userTaskConfig = application.config().get(UserTaskConfig.class);
        KogitoUserTaskEventSupportImpl impl = new KogitoUserTaskEventSupportImpl(userTaskConfig.identityProvider());
        userTaskConfig.userTaskEventListeners().listeners().forEach(impl::addEventListener);
        impl.addEventListener(new UnitOfWorkUserTaskEventListener(application.unitOfWorkManager()));
        instance.setUserTask(application.get(org.kie.kogito.usertask.UserTasks.class).userTaskById(instance.getUserTaskId()));
        instance.setUserTaskEventSupport(impl);
        instance.setUserTaskLifeCycle(userTaskConfig.userTaskLifeCycle());
        instance.setInstances(application.config().get(UserTaskConfig.class).userTaskInstances());
        return instance;
    }
}