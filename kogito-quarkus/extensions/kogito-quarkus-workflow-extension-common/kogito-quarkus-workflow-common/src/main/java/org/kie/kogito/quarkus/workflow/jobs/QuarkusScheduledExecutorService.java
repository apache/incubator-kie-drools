/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.quarkus.workflow.jobs;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ThreadContext;
import org.kie.kogito.services.jobs.impl.InMemoryJobService;

import io.quarkus.arc.DefaultBean;

@ApplicationScoped
@DefaultBean
public class QuarkusScheduledExecutorService extends ScheduledThreadPoolExecutor {

    @Inject
    ThreadContext context;

    public QuarkusScheduledExecutorService() {
        super(Integer.parseInt(System.getProperty(InMemoryJobService.IN_MEMORY_JOB_SERVICE_POOL_SIZE_PROPERTY, "10")));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(context.contextualRunnable(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return super.schedule(context.contextualRunnable(command), delay, unit);
    }
}
