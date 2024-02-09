/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.storage;

import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.kie.kogito.index.storage.Constants.JOBS_STORAGE;
import static org.kie.kogito.index.storage.Constants.PROCESS_DEFINITIONS_STORAGE;
import static org.kie.kogito.index.storage.Constants.PROCESS_ID_MODEL_STORAGE;
import static org.kie.kogito.index.storage.Constants.PROCESS_INSTANCES_STORAGE;
import static org.kie.kogito.index.storage.Constants.USER_TASK_INSTANCES_STORAGE;

@ApplicationScoped
public class ModelDataIndexStorageService implements DataIndexStorageService {

    @Inject
    StorageService storageService;

    @Override
    public Storage<ProcessDefinitionKey, ProcessDefinition> getProcessDefinitionStorage() {
        return new ModelProcessDefinitionStorage(storageService.getCache(PROCESS_DEFINITIONS_STORAGE, ProcessDefinition.class));
    }

    @Override
    public ProcessInstanceStorage getProcessInstanceStorage() {
        return new ModelProcessInstanceStorage(storageService.getCache(PROCESS_INSTANCES_STORAGE, ProcessInstance.class));
    }

    @Override
    public UserTaskInstanceStorage getUserTaskInstanceStorage() {
        return new ModelUserTaskInstanceStorage(storageService.getCache(USER_TASK_INSTANCES_STORAGE, UserTaskInstance.class));
    }

    @Override
    public Storage<String, Job> getJobsStorage() {
        return storageService.getCache(JOBS_STORAGE, Job.class);
    }

    @Override
    public Storage<String, ObjectNode> getDomainModelCache(String processId) {
        String rootType = getProcessIdModelCache().get(processId);
        return rootType == null ? null : storageService.getCache(getDomainModelCacheName(processId), ObjectNode.class, rootType);
    }

    @Override
    public String getDomainModelCacheName(String processId) {
        return processId + "_domain";
    }

    @Override
    public Storage<String, String> getProcessIdModelCache() {
        return storageService.getCache(PROCESS_ID_MODEL_STORAGE);
    }
}
