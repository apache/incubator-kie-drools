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

package org.kie.kogito.runtime.tools.quarkus.extension.runtime.rpc;

import java.util.function.Supplier;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.DataIndexClient;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.FormsStorage;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.common.annotation.NonBlocking;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class JBPMDevuiJsonRPCService {
    public static final String ALL_TASKS_IDS_QUERY = "{ \"operationName\": \"getAllTasksIds\", \"query\": \"query getAllTasksIds{  UserTaskInstances{ id } }\" }";
    public static final String ALL_PROCESS_INSTANCES_IDS_QUERY = "{ \"operationName\": \"getAllProcessesIds\", \"query\": \"query getAllProcessesIds{  ProcessInstances{ id } }\" }";
    public static final String ALL_JOBS_IDS_QUERY = "{ \"operationName\": \"getAllJobsIds\", \"query\": \"query getAllJobsIds{  Jobs{ id } }\" }";

    private final ObjectMapper mapper;
    private final DataIndexClient dataIndexClient;
    private final FormsStorage formsStorage;

    @Inject
    public JBPMDevuiJsonRPCService(ObjectMapper mapper, @RestClient DataIndexClient dataIndexClient, FormsStorage formsStorage) {
        this.mapper = mapper;
        this.dataIndexClient = dataIndexClient;
        this.formsStorage = formsStorage;
    }

    @NonBlocking
    public String queryProcessInstancesCount() {
        return doQuery(() -> this.dataIndexClient.queryProcessInstances(ALL_PROCESS_INSTANCES_IDS_QUERY).getData().getProcessInstances().size());
    }

    @NonBlocking
    public String queryTasksCount() {
        return doQuery(() -> this.dataIndexClient.queryTasks(ALL_TASKS_IDS_QUERY).getData().getTasks().size());
    }

    @NonBlocking
    public String queryJobsCount() {
        return doQuery(() -> this.dataIndexClient.queryJobs(ALL_JOBS_IDS_QUERY).getData().getJobs().size());
    }

    @NonBlocking
    public String getFormsCount() {
        return doQuery(this.formsStorage::getFormsCount);
    }

    private String doQuery(Supplier<Integer> countSupplier) {
        try {
            return String.valueOf(countSupplier.get());
        } catch (Exception ex) {
            return "-";
        }
    }
}
