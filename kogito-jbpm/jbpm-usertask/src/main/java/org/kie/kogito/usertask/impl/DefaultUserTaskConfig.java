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
import org.kie.kogito.services.jobs.impl.StaticJobService;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.services.uow.StaticUnitOfWorkManger;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.usertask.UserTaskAssignmentStrategyConfig;
import org.kie.kogito.usertask.UserTaskConfig;
import org.kie.kogito.usertask.UserTaskEventListenerConfig;
import org.kie.kogito.usertask.UserTaskInstances;
import org.kie.kogito.usertask.impl.lifecycle.DefaultUserTaskLifeCycles;
import org.kie.kogito.usertask.lifecycle.UserTaskLifeCycles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.services.jobs.impl.StaticJobService.staticJobService;

public class DefaultUserTaskConfig implements UserTaskConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUserTaskConfig.class);

    private UserTaskEventListenerConfig userTaskEventListeners;
    private UnitOfWorkManager unitOfWorkManager;
    private JobsService jobService;
    private IdentityProvider identityProvider;
    private UserTaskLifeCycles userTaskLifeCycles;
    private UserTaskAssignmentStrategyConfig userTaskAssignmentStrategyConfig;
    private UserTaskInstances userTaskInstances;

    public DefaultUserTaskConfig() {
        this(new DefaultUserTaskEventListenerConfig(),
                new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()),
                staticJobService(),
                new NoOpIdentityProvider(),
                new DefaultUserTaskLifeCycles(),
                new DefaultUserTaskAssignmentStrategyConfig(),
                new InMemoryUserTaskInstances());
    }

    public DefaultUserTaskConfig(
            Iterable<UserTaskEventListenerConfig> userTaskEventListenerConfig,
            Iterable<UnitOfWorkManager> unitOfWorkManager,
            Iterable<JobsService> jobService,
            Iterable<IdentityProvider> identityProvider,
            Iterable<UserTaskLifeCycles> userTaskLifeCycles,
            Iterable<UserTaskAssignmentStrategyConfig> userTaskAssignmentStrategyConfig,
            Iterable<UserTaskInstances> userTaskInstances) {

        this.userTaskEventListeners = singleton(userTaskEventListenerConfig, DefaultUserTaskEventListenerConfig::new);
        this.unitOfWorkManager = singleton(unitOfWorkManager, StaticUnitOfWorkManger::staticUnitOfWorkManager);
        this.jobService = singleton(jobService, StaticJobService::staticJobService);
        this.identityProvider = singleton(identityProvider, NoOpIdentityProvider::new);
        this.userTaskLifeCycles = singleton(userTaskLifeCycles, DefaultUserTaskLifeCycles::new);
        this.userTaskAssignmentStrategyConfig = singleton(userTaskAssignmentStrategyConfig, DefaultUserTaskAssignmentStrategyConfig::new);
        this.userTaskInstances = singleton(userTaskInstances, InMemoryUserTaskInstances::new);
    }

    private <T> T singleton(Iterable<T> values, Supplier<T> defaultValue) {
        Iterator<T> iterator = values.iterator();
        T value = null;
        if (iterator.hasNext()) {
            value = iterator.next();
        } else {
            value = defaultValue.get();
        }
        LOG.debug("UserTask config element {}", value);
        return value;
    }

    public DefaultUserTaskConfig(
            UserTaskEventListenerConfig userTaskEventListenerConfig,
            UnitOfWorkManager unitOfWorkManager,
            JobsService jobService,
            IdentityProvider identityProvider,
            UserTaskLifeCycles userTaskLifeCycles,
            DefaultUserTaskAssignmentStrategyConfig userTaskAssignmentStrategyConfig,
            UserTaskInstances userTaskInstances) {
        this.userTaskEventListeners = userTaskEventListenerConfig;
        this.unitOfWorkManager = unitOfWorkManager;
        this.jobService = jobService;
        this.identityProvider = identityProvider;
        this.userTaskLifeCycles = userTaskLifeCycles;
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
    public UserTaskLifeCycles userTaskLifeCycles() {
        return userTaskLifeCycles;
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
