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
package org.kie.kogito.jobs.service.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Helper resource for emulating the callback endpoint of a Job created by using the Job service events API and to have
 * the ability of registering the invocations.
 * 
 * @see BaseMessagingApiTest
 */
public abstract class BaseCallbackResource {

    public static final String CALLBACK_RESOURCE_PATH = "/test/callback/management/jobs";

    private static final Map<String, List<CallbackExecution>> EXECUTED_CALLBACKS = new HashMap<>();

    public static class CallbackExecution {

        String processId;
        String processInstanceId;
        String timerId;
        int limit;

        public CallbackExecution(String processId, String processInstanceId, String timerId, int limit) {
            this.processId = processId;
            this.processInstanceId = processInstanceId;
            this.timerId = timerId;
            this.limit = limit;
        }
    }

    @POST
    @Path("{processId}/instances/{processInstanceId}/timers/{timerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response executeCallback(
            @PathParam("processId") String processId,
            @PathParam("processInstanceId") String processInstanceId,
            @PathParam("timerId") String timerId,
            @QueryParam("limit") @DefaultValue("0") Integer limit) {
        EXECUTED_CALLBACKS.computeIfAbsent(timerId, id -> new ArrayList<>())
                .add(new CallbackExecution(processId, processInstanceId, timerId, limit));
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/executionsList/{timerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CallbackExecution> getExecutionsList(@PathParam("timerId") String timerId) {
        return EXECUTED_CALLBACKS.getOrDefault(timerId, new ArrayList<>());
    }

    @GET
    @Path("/executions/{timerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public int getExecutions(@PathParam("timerId") String timerId) {
        return EXECUTED_CALLBACKS.getOrDefault(timerId, new ArrayList<>()).size();
    }
}
