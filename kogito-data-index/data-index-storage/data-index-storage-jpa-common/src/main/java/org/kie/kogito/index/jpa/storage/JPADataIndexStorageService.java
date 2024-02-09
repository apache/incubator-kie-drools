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
package org.kie.kogito.index.jpa.storage;

import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.index.storage.ProcessInstanceStorage;
import org.kie.kogito.index.storage.UserTaskInstanceStorage;
import org.kie.kogito.persistence.api.Storage;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class JPADataIndexStorageService implements DataIndexStorageService {

    @Inject
    ProcessDefinitionEntityStorage definitionStorage;

    @Inject
    JobEntityStorage jobsStorage;

    @Inject
    ProcessInstanceStorage processInstanceStorage;

    @Inject
    UserTaskInstanceStorage userTaskInstanceStorage;

    @Override
    public Storage<ProcessDefinitionKey, ProcessDefinition> getProcessDefinitionStorage() {
        return definitionStorage;
    }

    @Override
    public ProcessInstanceStorage getProcessInstanceStorage() {
        return processInstanceStorage;
    }

    @Override
    public UserTaskInstanceStorage getUserTaskInstanceStorage() {
        return userTaskInstanceStorage;
    }

    @Override
    public Storage<String, Job> getJobsStorage() {
        return jobsStorage;
    }

    @Override
    public Storage<String, ObjectNode> getDomainModelCache(String processId) {
        throw new UnsupportedOperationException("Generic custom type cache not available in JPA");
    }

    @Override
    public String getDomainModelCacheName(String processId) {
        return processId + "_domain";
    }

    @Override
    public Storage<String, String> getProcessIdModelCache() {
        throw new UnsupportedOperationException("Generic String cache not available in JPA");
    }
}
