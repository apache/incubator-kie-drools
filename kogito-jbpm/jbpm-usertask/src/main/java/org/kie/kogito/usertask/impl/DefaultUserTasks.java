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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kie.kogito.Application;
import org.kie.kogito.uow.events.UnitOfWorkUserTaskEventListener;
import org.kie.kogito.usertask.UserTask;
import org.kie.kogito.usertask.UserTaskConfig;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTaskInstances;
import org.kie.kogito.usertask.UserTasks;

public class DefaultUserTasks implements UserTasks {

    private Map<String, UserTask> userTasks;
    private Application application;
    private UserTaskInstances userTaskInstances;

    public DefaultUserTasks() {
        this.userTasks = new HashMap<>();
    }

    public DefaultUserTasks(Application application, UserTask... userTasks) {
        this(application, List.of(userTasks));
    }

    public DefaultUserTasks(Application application, Iterable<UserTask> userTasks) {
        this.application = application;
        this.userTasks = new HashMap<>();
        Iterator<UserTask> userTaskIterator = userTasks.iterator();
        while (userTaskIterator.hasNext()) {
            UserTask userTask = userTaskIterator.next();
            this.userTasks.put(userTask.id(), userTask);
        }
        userTaskInstances = application.config().get(UserTaskConfig.class).userTaskInstances();
        userTaskInstances.setDisconnectUserTaskInstance(this::disconnect);
        userTaskInstances.setReconnectUserTaskInstance(this::connect);
    }

    @Override
    public UserTask userTaskById(String userTaskId) {
        return userTasks.get(userTaskId);
    }

    @Override
    public Collection<String> userTaskIds() {
        return userTasks.keySet();
    }

    @Override
    public UserTaskInstances instances() {
        return userTaskInstances;
    }

    public UserTaskInstance disconnect(UserTaskInstance userTaskInstance) {
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
        instance.setUserTask(application.get(UserTasks.class).userTaskById(instance.getUserTaskId()));
        instance.setUserTaskEventSupport(impl);
        instance.setUserTaskLifeCycle(userTaskConfig.userTaskLifeCycle());
        instance.setInstances(userTaskInstances);
        return instance;
    }
}
