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

package org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.jobs.JobsResponse;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.processes.ProcessInstancesResponse;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.tasks.TasksResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import static org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.DataIndexClient.DATA_INDEX_CONFIG_KEY;

@Path("/graphql")
@RegisterRestClient(configKey = DATA_INDEX_CONFIG_KEY)
@ApplicationScoped
public interface DataIndexClient {

    String DATA_INDEX_CONFIG_KEY = "kogito.data-index.url";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    ProcessInstancesResponse queryProcessInstances(String query);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    TasksResponse queryTasks(String query);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    JobsResponse queryJobs(String query);
}
