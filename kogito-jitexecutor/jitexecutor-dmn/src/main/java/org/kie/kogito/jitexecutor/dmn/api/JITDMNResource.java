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
package org.kie.kogito.jitexecutor.dmn.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.dmn.core.internal.utils.MarshallingStubUtils;
import org.kie.kogito.jitexecutor.dmn.JITDMNService;
import org.kie.kogito.jitexecutor.dmn.requests.JITDMNPayload;
import org.kie.kogito.jitexecutor.dmn.responses.DMNResultWithExplanation;
import org.kie.kogito.jitexecutor.dmn.responses.JITDMNResult;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/jitdmn")
public class JITDMNResource {

    @Inject
    JITDMNService jitdmnService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response jitdmn(JITDMNPayload payload) {
        JITDMNResult evaluateAll = payload.getModel() != null ? jitdmnService.evaluateModel(payload.getModel(), payload.getContext()) : jitdmnService.evaluateModel(payload, payload.getContext());
        Map<String, Object> restResulk = new HashMap<>();
        for (Entry<String, Object> kv : evaluateAll.getContext().getAll().entrySet()) {
            restResulk.put(kv.getKey(), MarshallingStubUtils.stubDMNResult(kv.getValue(), String::valueOf));
        }
        return Response.ok(restResulk).build();
    }

    @POST
    @Path("/dmnresult")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response jitdmnResult(JITDMNPayload payload) {
        JITDMNResult dmnResult = payload.getModel() != null ? jitdmnService.evaluateModel(payload.getModel(), payload.getContext()) : jitdmnService.evaluateModel(payload, payload.getContext());
        return Response.ok(dmnResult).build();
    }

    @POST
    @Path("/evaluateAndExplain")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response jitEvaluateAndExplain(JITDMNPayload payload) {
        DMNResultWithExplanation response =
                payload.getModel() != null ? jitdmnService.evaluateModelAndExplain(payload.getModel(), payload.getContext()) : jitdmnService.evaluateModelAndExplain(payload, payload.getContext());
        return Response.ok(response).build();
    }
}
