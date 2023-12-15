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
package org.kie.kogito.quarkus.workflow;

import java.util.concurrent.ScheduledExecutorService;

import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.correlation.CorrelationService;
import org.kie.kogito.event.correlation.DefaultCorrelationService;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.ProcessVersionResolver;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.version.ProjectVersionProcessVersionResolver;
import org.kie.kogito.services.jobs.impl.InMemoryJobService;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.uow.UnitOfWorkManager;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.properties.IfBuildProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class KogitoBeanProducer {

    @DefaultBean
    @Produces
    CorrelationService correlationService() {
        return new DefaultCorrelationService();
    }

    @DefaultBean
    @Produces
    UnitOfWorkManager unitOfWorkManager() {
        return new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory());
    }

    @DefaultBean
    @Produces
    JobsService jobsService(Instance<Processes> processes, UnitOfWorkManager uowm, ScheduledExecutorService executor) {
        return InMemoryJobService.get(processes.isResolvable() ? processes.get() : null, uowm, executor);
    }

    @Produces
    @IfBuildProperty(name = "kogito.workflow.version-strategy", stringValue = "project")
    ProcessVersionResolver projectVersionResolver(ConfigBean configBean) {
        return new ProjectVersionProcessVersionResolver(configBean.getGav().orElseThrow(() -> new RuntimeException("Unable to use kogito.workflow.version-strategy without a project GAV")));
    }
}
