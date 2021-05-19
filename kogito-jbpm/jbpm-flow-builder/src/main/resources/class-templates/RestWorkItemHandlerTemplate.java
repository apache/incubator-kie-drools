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
package org.jbpm.process.codegen;

import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.core.Vertx;
import org.kogito.workitem.rest.RestWorkItemHandler;

public class xxxRestWorkItemHandler extends RestWorkItemHandler {

    public xxxRestWorkItemHandler() {
        this(Vertx.vertx());
    }

    public xxxRestWorkItemHandler(Vertx vertx) {
        super(WebClient.create(vertx));
    }
    
    @Override
    public String getName() {
        return this.getClass().getName();
    }

}