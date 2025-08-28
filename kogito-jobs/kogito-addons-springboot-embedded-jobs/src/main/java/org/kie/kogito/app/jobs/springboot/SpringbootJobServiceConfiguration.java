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
package org.kie.kogito.app.jobs.springboot;

import org.kie.kogito.app.jobs.api.JobExecutor;
import org.kie.kogito.app.jobs.integrations.ProcessInstanceJobExecutor;
import org.kie.kogito.app.jobs.integrations.ProcessJobExecutor;
import org.kie.kogito.app.jobs.integrations.UserTaskInstanceJobExecutor;
import org.kie.kogito.app.jobs.spi.JobContextFactory;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobContextFactory;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobStore;
import org.kie.kogito.process.Processes;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.usertask.UserTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
public class SpringbootJobServiceConfiguration {

    @Autowired(required = false)
    Processes processes;

    @Autowired
    UnitOfWorkManager unitOfWorkManager;

    @Autowired(required = false)
    UserTasks userTasks;

    @Bean
    @ConditionalOnMissingBean(UnitOfWorkManager.class)
    public UnitOfWorkManager unitOfWorkManagerProducer() {
        return new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory());
    }

    @Bean
    @ConditionalOnMissingBean(JobStore.class)
    public JobStore produceDefaulJobStore() {
        return new MemoryJobStore();
    }

    @Bean
    @ConditionalOnMissingBean(JobContextFactory.class)
    public JobContextFactory produceDefaulJobContextFactory() {
        return new MemoryJobContextFactory();
    }

    @Bean
    @ConditionalOnBean({ Processes.class, UnitOfWorkManager.class })
    public JobExecutor produceProcessJobExecutor() {
        return new ProcessJobExecutor(processes, unitOfWorkManager);
    }

    @Bean
    @ConditionalOnBean({ Processes.class, UnitOfWorkManager.class })
    public JobExecutor produceProcessInstanceJobExecutor() {
        return new ProcessInstanceJobExecutor(processes, unitOfWorkManager);
    }

    @Bean
    @ConditionalOnBean({ UserTasks.class, UnitOfWorkManager.class })
    public JobExecutor produceUserTaskInstanceJobExecutor() {
        return new UserTaskInstanceJobExecutor(userTasks, unitOfWorkManager);
    }
}
