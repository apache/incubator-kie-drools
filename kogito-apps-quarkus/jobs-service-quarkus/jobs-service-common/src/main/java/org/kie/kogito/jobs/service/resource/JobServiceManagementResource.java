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
package org.kie.kogito.jobs.service.resource;

import org.kie.kogito.jobs.service.management.ReleaseLeaderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/management")
public class JobServiceManagementResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceManagementResource.class);

    @Inject
    Event<ReleaseLeaderEvent> releaseLeaderEventEvent;

    @POST
    @Path("/shutdown")
    public Uni<Response> shutdownHook() {
        return Uni.createFrom().voidItem()
                .onItem().invoke(i -> LOGGER.info("Job Service is shutting down"))
                .onItem().invoke(() -> releaseLeaderEventEvent.fire(new ReleaseLeaderEvent()))
                .onItem().transform(i -> Response.ok().build());
    }
}
