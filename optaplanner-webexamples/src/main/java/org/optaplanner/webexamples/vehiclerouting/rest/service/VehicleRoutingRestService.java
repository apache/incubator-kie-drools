/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.webexamples.vehiclerouting.rest.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.optaplanner.webexamples.vehiclerouting.rest.domain.JsonMessage;
import org.optaplanner.webexamples.vehiclerouting.rest.domain.JsonVehicleRoutingSolution;

/**
 * @see DefaultVehicleRoutingRestService
 */
@Path("/vehiclerouting")
public interface VehicleRoutingRestService {

    @GET
    @Path("/solution")
    @Produces("application/json")
    JsonVehicleRoutingSolution getSolution();

    @POST
    @Path("/solution/solve")
    @Produces("application/json")
    JsonMessage solve();

    @POST
    @Path("/solution/terminateEarly")
    @Produces("application/json")
    JsonMessage terminateEarly();
}
