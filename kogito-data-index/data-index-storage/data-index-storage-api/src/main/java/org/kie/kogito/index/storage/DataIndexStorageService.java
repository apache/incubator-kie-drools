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

import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.persistence.api.Storage;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface DataIndexStorageService {

    Storage<String, ProcessInstance> getProcessInstancesCache();

    Storage<String, UserTaskInstance> getUserTaskInstancesCache();

    Storage<String, Job> getJobsCache();

    Storage<String, ObjectNode> getDomainModelCache(String processId);

    String getDomainModelCacheName(String processId);

    Storage<String, String> getProcessIdModelCache();
}
