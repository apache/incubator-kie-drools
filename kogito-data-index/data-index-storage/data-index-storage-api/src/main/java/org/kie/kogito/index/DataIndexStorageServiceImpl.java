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

package org.kie.kogito.index;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;

@ApplicationScoped
public class DataIndexStorageServiceImpl implements DataIndexStorageService {

    private static final String PROCESS_INSTANCES_CACHE = "processinstances";
    private static final String USER_TASK_INSTANCES_CACHE = "usertaskinstances";
    private static final String JOBS_CACHE = "jobs";
    private static final String PROCESS_ID_MODEL_CACHE = "processidmodel";

    @Inject
    StorageService cacheService;

    @Override
    public Storage<String, ProcessInstance> getProcessInstancesCache() {
        return cacheService.getCache(PROCESS_INSTANCES_CACHE, ProcessInstance.class);
    }

    @Override
    public Storage<String, UserTaskInstance> getUserTaskInstancesCache() {
        return  cacheService.getCache(USER_TASK_INSTANCES_CACHE, UserTaskInstance.class);
    }

    @Override
    public Storage<String, Job> getJobsCache() {
        return cacheService.getCache(JOBS_CACHE, Job.class);
    }

    @Override
    public Storage<String, ObjectNode> getDomainModelCache(String processId) {
        String rootType = getProcessIdModelCache().get(processId);
        return rootType == null ? null : cacheService.getCacheWithDataFormat(processId + "_domain", ObjectNode.class, rootType);
    }

    @Override
    public Storage<String, String> getProcessIdModelCache() {
        return cacheService.getCache(PROCESS_ID_MODEL_CACHE);
    }
}
