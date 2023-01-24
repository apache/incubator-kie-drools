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
package org.kie.kogito.index.storage;

public final class Constants {

    private Constants() {
    }

    public static final String PROCESS_INSTANCES_DOMAIN_ATTRIBUTE = "processInstances";
    public static final String USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE = "userTasks";
    public static final String KOGITO_DOMAIN_ATTRIBUTE = "metadata";
    public static final String LAST_UPDATE = "lastUpdate";
    public static final String PROCESS_ID = "processId";
    public static final String PROCESS_NAME = "processName";
    public static final String ID = "id";

    public static final String PROCESS_INSTANCES_STORAGE = "processinstances";
    public static final String USER_TASK_INSTANCES_STORAGE = "usertaskinstances";
    public static final String JOBS_STORAGE = "jobs";
    public static final String PROCESS_ID_MODEL_STORAGE = "processidmodel";
}
