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

    public static final String QUARKUS_OIDC_TENANT_ENABLED = "quarkus.oidc.tenant-enabled";

    public static final String QUARKUS_OIDC_AUTH_SERVER_URL = "quarkus.oidc.auth-server-url";

    public static final String QUARKUS_OIDC_CLIENT_ID = "quarkus.oidc.client-id";

    public static final String QUARKUS_OIDC_CREDENTIALS_SECRET = "quarkus.oidc.credentials.secret";

    public static final String CLIENT_AUTH_USER = TASK_ASSIGNING_PROPERTY_PREFIX + ".user";

    public static final String CLIENT_AUTH_PASSWORD = TASK_ASSIGNING_PROPERTY_PREFIX + ".password";

    public static final String DATA_INDEX_SERVER_URL = TASK_ASSIGNING_PROPERTY_PREFIX + ".data-index.server-url";

    public static final String DATA_LOADER_RETRY_INTERVAL_DURATION = TASK_ASSIGNING_PROPERTY_PREFIX + ".data-loader.retry-interval-duration";

    public static final String DATA_LOADER_RETRIES = TASK_ASSIGNING_PROPERTY_PREFIX + ".data-loader.retries";

    public static final String DATA_LOADER_PAGE_SIZE = TASK_ASSIGNING_PROPERTY_PREFIX + ".data-loader.page-size";

    public static final String PUBLISH_WINDOW_SIZE = TASK_ASSIGNING_PROPERTY_PREFIX + ".publish-window-size";
}
