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
package org.kie.kogito.serverless.workflow.executor;

import org.kogito.workitem.rest.RestWorkItemHandler;
import org.kogito.workitem.rest.RestWorkItemHandlerUtils;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

class StaticRestWorkItemHandler extends RestWorkItemHandler implements AutoCloseable {

    public StaticRestWorkItemHandler(Vertx vertx) {
        this(vertx, new WebClientOptions(), RestWorkItemHandlerUtils.sslWebClientOptions());
    }

    public StaticRestWorkItemHandler(Vertx vertx, WebClientOptions httpOptions, WebClientOptions httpsOptions) {
        super(WebClient.create(vertx, httpOptions), WebClient.create(vertx, httpsOptions));
    }

    @Override
    public String getName() {
        return RestWorkItemHandler.REST_TASK_TYPE;
    }

    @Override
    public void close() {
        httpClient.close();
        httpsClient.close();
    }
}
