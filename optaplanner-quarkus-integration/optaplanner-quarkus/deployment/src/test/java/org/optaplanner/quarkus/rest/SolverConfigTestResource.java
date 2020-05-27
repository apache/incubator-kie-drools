/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.optaplanner.core.config.solver.SolverConfig;

@Path("/solver-config")
@ApplicationScoped
public class SolverConfigTestResource {

    @Inject
    SolverConfig solverConfig;

    @GET
    @Path("/seconds-spent-limit")
    @Produces(MediaType.TEXT_PLAIN)
    public String secondsSpentLimit() {
        return "secondsSpentLimit=" + solverConfig.getTerminationConfig().getSecondsSpentLimit();
    }

}
