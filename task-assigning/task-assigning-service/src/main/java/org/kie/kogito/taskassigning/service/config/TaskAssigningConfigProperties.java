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

package org.kie.kogito.taskassigning.service.config;

public class TaskAssigningConfigProperties {

    private TaskAssigningConfigProperties() {
    }

    public static final String TASK_ASSIGNING_PROPERTY_PREFIX = "kogito.task-assigning";

    public static final String OIDC_CLIENT = TASK_ASSIGNING_PROPERTY_PREFIX + ".oidc-client";

    public static final String CLIENT_AUTH_USER = TASK_ASSIGNING_PROPERTY_PREFIX + ".user";

    public static final String CLIENT_AUTH_PASSWORD = TASK_ASSIGNING_PROPERTY_PREFIX + ".password";

    public static final String DATA_INDEX_SERVER_URL = TASK_ASSIGNING_PROPERTY_PREFIX + ".data-index.server-url";

    public static final String DATA_INDEX_CONNECT_TIMEOUT_DURATION = TASK_ASSIGNING_PROPERTY_PREFIX + ".data-index-connect-timeout-duration";

    public static final String DATA_INDEX_READ_TIMEOUT_DURATION = TASK_ASSIGNING_PROPERTY_PREFIX + ".data-index-read-timeout-duration";

    public static final String DATA_LOADER_PAGE_SIZE = TASK_ASSIGNING_PROPERTY_PREFIX + ".data-loader.page-size";

    public static final String PUBLISH_WINDOW_SIZE = TASK_ASSIGNING_PROPERTY_PREFIX + ".publish-window-size";

    public static final String USER_SERVICE_CONNECTOR = TASK_ASSIGNING_PROPERTY_PREFIX + ".user-service-connector";

    public static final String USER_SERVICE_SYNC_INTERVAL = TASK_ASSIGNING_PROPERTY_PREFIX + ".user-service-sync.interval";

    public static final String WAIT_FOR_IMPROVED_SOLUTION_DURATION = TASK_ASSIGNING_PROPERTY_PREFIX + ".wait-for-improved-solution-duration";

    public static final String IMPROVE_SOLUTION_ON_BACKGROUND_DURATION = TASK_ASSIGNING_PROPERTY_PREFIX + ".improve-solution-on-background-duration";

    public static final String PROCESS_RUNTIME_CONNECT_TIMEOUT_DURATION = TASK_ASSIGNING_PROPERTY_PREFIX + ".process-runtime-connect-timeout-duration";

    public static final String PROCESS_RUNTIME_READ_TIMEOUT_DURATION = TASK_ASSIGNING_PROPERTY_PREFIX + ".process-runtime-read-timeout-duration";
}