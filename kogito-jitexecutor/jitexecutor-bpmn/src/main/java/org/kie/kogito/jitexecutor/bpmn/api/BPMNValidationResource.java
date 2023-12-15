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
package org.kie.kogito.jitexecutor.bpmn.api;

import org.kie.kogito.jitexecutor.bpmn.JITBPMNService;
import org.kie.kogito.jitexecutor.bpmn.responses.JITBPMNValidationResult;
import org.kie.kogito.jitexecutor.common.requests.MultipleResourcesPayload;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("jitbpmn/validate")
public class BPMNValidationResource {

    @Inject
    JITBPMNService jitbpmnService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response schema(MultipleResourcesPayload payload) {
        JITBPMNValidationResult result = jitbpmnService.validatePayload(payload);
        return Response.ok(result.getErrors()).build();
    }

}
