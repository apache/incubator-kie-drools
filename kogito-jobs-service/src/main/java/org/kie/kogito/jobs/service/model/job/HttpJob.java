/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.model.job;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.kie.kogito.jobs.service.executor.HttpJobExecutor;
import org.kie.kogito.timer.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;

/**
 * The job that sends an HTTP Request based on the {@link HttpJobContext}.
 */
public class HttpJob implements Job<HttpJobContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpJob.class);

    private Optional<HttpJobExecutor> executor;

    public HttpJob(HttpJobExecutor executor) {
        this.executor = Optional.ofNullable(executor);
    }

    public HttpJob() {
        this.executor = Optional.ofNullable(Arc.container())
                .map(c -> c.instance(HttpJobExecutor.class))
                .map(InstanceHandle::get);
    }

    @Override
    public void execute(HttpJobContext ctx) {
        LOGGER.info("Executing for context {}", ctx.getJobDetails());
        executor.ifPresent(e -> e.execute(CompletableFuture.completedFuture(ctx.getJobDetails())).thenAccept(j -> LOGGER.debug("Executed {}", j)));
    }
}
