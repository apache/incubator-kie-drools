/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.resources;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.resources.ConditionalQuarkusTestResource;

public class TaskAssigningServiceQuarkusTestResource extends ConditionalQuarkusTestResource<TaskAssigningServiceResource> {

    public static final String KOGITO_TASK_ASSIGNING_SERVICE_URL = "kogito.task-assigning-service.url";
    public static final String KOGITO_DATA_INDEX_SERVICE_URL = "kogito.data-index-service.url";
    public static final String KOGITO_PROCESSES_SERVICE_URL = "kogito.processes-service.url";

    public TaskAssigningServiceQuarkusTestResource() {
        super(new TaskAssigningServiceResource());
    }

    @Override
    protected Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put(KOGITO_TASK_ASSIGNING_SERVICE_URL, "http://localhost:" + getTestResource().getMappedPort());
        properties.put(KOGITO_DATA_INDEX_SERVICE_URL, "http://localhost:" + getTestResource().getDataIndexMappedPort() + "/graphql");
        properties.put(KOGITO_PROCESSES_SERVICE_URL, "http://localhost:" + getTestResource().getKogitoServiceMappedPort());
        properties.putAll(getTestResource().getProperties());
        return properties;
    }
}