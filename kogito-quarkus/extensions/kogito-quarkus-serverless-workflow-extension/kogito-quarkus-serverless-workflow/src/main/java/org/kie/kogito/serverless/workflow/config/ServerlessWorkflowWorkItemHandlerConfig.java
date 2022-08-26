/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.serverless.workflow.config;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ServerlessWorkflowWorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessWorkflowWorkItemHandlerConfig.class);

    @Inject
    Instance<WorkflowWorkItemHandler> handlers;

    @PostConstruct
    public void init() {
        handlers.forEach(handler -> {
            LOGGER.info("Registering OpenAPI work item handler named: {}", handler.getName());
            register(handler.getName(), handler);
        });
    }

}
