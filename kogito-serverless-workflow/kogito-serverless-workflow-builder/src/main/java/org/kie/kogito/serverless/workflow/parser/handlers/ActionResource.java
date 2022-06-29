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
package org.kie.kogito.serverless.workflow.parser.handlers;

public class ActionResource {

    private final String uri;
    private final String operation;
    private final String service;

    public ActionResource(String uri, String operation, String service) {
        this.uri = uri;
        this.operation = operation;
        this.service = service;
    }

    public String getUri() {
        return uri;
    }

    public String getOperation() {
        return operation;
    }

    public String getService() {
        return service;
    }
}
