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
package org.kie.kogito.app.jobs.quarkus;

import org.kie.kogito.app.jobs.api.JobExecutor;
import org.kie.kogito.app.jobs.integregations.ProcessInstanceJobExecutor;
import org.kie.kogito.app.jobs.integregations.ProcessJobExecutor;
import org.kie.kogito.app.jobs.integregations.UserTaskInstanceJobExecutor;
import org.kie.kogito.app.jobs.spi.JobContextFactory;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobContextFactory;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobStore;
import org.kie.kogito.process.Processes;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.usertask.UserTasks;

import io.quarkus.arc.DefaultBean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusJobServiceProducer {

    @Inject
    Instance<Processes> processes;

    @Inject
    Instance<UserTasks> userTasks;

    @Inject
    UnitOfWorkManager unitOfWorkManager;

    @Produces
    @DefaultBean
    public JobStore produceDefaultJobStore() {
        return new MemoryJobStore();
    }

    @Produces
    @DefaultBean
    public JobContextFactory produceJobContextFactory() {
        return new MemoryJobContextFactory();
    }

    @Produces
    public JobExecutor produceExecutors() {
        if (processes.isResolvable()) {
            return new ProcessJobExecutor(processes.get(), unitOfWorkManager);
        }

        return new QuarkusEmptyJobExecutor();
    }

    @Produces
    public JobExecutor produceUserTaskExecutor() {
        if (userTasks.isResolvable()) {
            return new UserTaskInstanceJobExecutor(userTasks.get(), unitOfWorkManager);
        }
        return new QuarkusEmptyJobExecutor();
    }

    @Produces
    public JobExecutor produceProcessInstanceExecutors() {
        if (processes.isResolvable()) {
            return new ProcessInstanceJobExecutor(processes.get(), unitOfWorkManager);
        }

        return new QuarkusEmptyJobExecutor();
    }
}
