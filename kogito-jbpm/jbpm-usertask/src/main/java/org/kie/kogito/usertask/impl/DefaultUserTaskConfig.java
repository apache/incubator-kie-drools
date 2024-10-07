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

import java.util.Iterator;
import java.util.function.Supplier;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.services.identity.NoOpIdentityProvider;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.usertask.UserTaskAssignmentStrategyConfig;
import org.kie.kogito.usertask.UserTaskConfig;
import org.kie.kogito.usertask.UserTaskEventListenerConfig;
import org.kie.kogito.usertask.UserTaskInstances;
import org.kie.kogito.usertask.impl.lifecycle.DefaultUserTaskLifeCycle;
import org.kie.kogito.usertask.lifecycle.UserTaskLifeCycle;

public class DefaultUserTaskConfig implements UserTaskConfig {

    private UserTaskEventListenerConfig userTaskEventListeners;
    private UnitOfWorkManager unitOfWorkManager;
    private JobsService jobService;
    private IdentityProvider identityProvider;
    private UserTaskLifeCycle userTaskLifeCycle;
    private UserTaskAssignmentStrategyConfig userTaskAssignmentStrategyConfig;
    private UserTaskInstances userTaskInstances;

    public DefaultUserTaskConfig() {
        this(new DefaultUserTaskEventListenerConfig(),
                new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()),
                null,
                new NoOpIdentityProvider(),
                new DefaultUserTaskLifeCycle(),
                new DefaultUserTaskAssignmentStrategyConfig(),
                new InMemoryUserTaskInstances());
    }

    public DefaultUserTaskConfig(
            Iterable<UserTaskEventListenerConfig> userTaskEventListenerConfig,
            Iterable<UnitOfWorkManager> unitOfWorkManager,
            Iterable<JobsService> jobService,
            Iterable<IdentityProvider> identityProvider,
            Iterable<UserTaskLifeCycle> userTaskLifeCycle,
            Iterable<UserTaskAssignmentStrategyConfig> userTaskAssignmentStrategyConfig,
            Iterable<UserTaskInstances> userTaskInstances) {

        this.userTaskEventListeners = singleton(userTaskEventListenerConfig, DefaultUserTaskEventListenerConfig::new);
        this.unitOfWorkManager = singleton(unitOfWorkManager, () -> new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));
        this.jobService = singleton(jobService, () -> null);
        this.identityProvider = singleton(identityProvider, NoOpIdentityProvider::new);
        this.userTaskLifeCycle = singleton(userTaskLifeCycle, DefaultUserTaskLifeCycle::new);
        this.userTaskAssignmentStrategyConfig = singleton(userTaskAssignmentStrategyConfig, DefaultUserTaskAssignmentStrategyConfig::new);
        this.userTaskInstances = singleton(userTaskInstances, InMemoryUserTaskInstances::new);

    }

    private <T> T singleton(Iterable<T> value, Supplier<T> defaultValue) {
        Iterator<T> iterator = value.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return defaultValue.get();
    }

    public DefaultUserTaskConfig(
            UserTaskEventListenerConfig userTaskEventListenerConfig,
            UnitOfWorkManager unitOfWorkManager,
            JobsService jobService,
            IdentityProvider identityProvider,
            UserTaskLifeCycle userTaskLifeCycle,
            DefaultUserTaskAssignmentStrategyConfig userTaskAssignmentStrategyConfig,
            UserTaskInstances userTaskInstances) {
        this.userTaskEventListeners = userTaskEventListenerConfig;
        this.unitOfWorkManager = unitOfWorkManager;
        this.jobService = jobService;
        this.identityProvider = identityProvider;
        this.userTaskLifeCycle = userTaskLifeCycle;
        this.userTaskAssignmentStrategyConfig = userTaskAssignmentStrategyConfig;
        this.userTaskInstances = userTaskInstances;
    }

    @Override
    public UserTaskEventListenerConfig userTaskEventListeners() {
        return userTaskEventListeners;
    }

    @Override
    public UnitOfWorkManager unitOfWorkManager() {
        return unitOfWorkManager;
    }

    @Override
    public JobsService jobsService() {
        return jobService;
    }

    @Override
    public IdentityProvider identityProvider() {
        return identityProvider;
    }

    @Override
    public UserTaskLifeCycle userTaskLifeCycle() {
        return userTaskLifeCycle;
    }

    @Override
    public UserTaskAssignmentStrategyConfig userTaskAssignmentStrategies() {
        return userTaskAssignmentStrategyConfig;
    }

    @Override
    public UserTaskInstances userTaskInstances() {
        return userTaskInstances;
    }

}
