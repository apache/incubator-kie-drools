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

package org.optaplanner.quarkus.drl.it;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.quarkus.drl.it.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.drl.it.domain.TestdataQuarkusSolution;

@Path("/optaplanner/test")
public class OptaPlannerTestResource {

    @Inject
    SolverManager<TestdataQuarkusSolution, Long> solverManager;

    @POST
    @Path("/solver-factory")
    @Produces(MediaType.TEXT_PLAIN)
    public String solveWithSolverFactory() throws InterruptedException {
        TestdataQuarkusSolution planningProblem = new TestdataQuarkusSolution();
        planningProblem.setEntityList(Arrays.asList(
                new TestdataQuarkusEntity(),
                new TestdataQuarkusEntity()));
        planningProblem.setLeftValueList(Arrays.asList("a", "b", "c"));
        planningProblem.setRightValueList(Arrays.asList("1", "2", "3"));
        SolverJob<TestdataQuarkusSolution, Long> solverJob = solverManager.solve(1L, planningProblem);
        try {
            TestdataQuarkusSolution sol = solverJob.getFinalBestSolution();
            StringBuilder out = new StringBuilder();
            out.append("score=").append(sol.getScore()).append('\n');
            for (int i = 0; i < sol.getEntityList().size(); i++) {
                out.append("entity." + i + ".fullValue=").append(sol.getEntityList().get(i).getFullValue()).append('\n');
            }
            return out.toString();
        } catch (ExecutionException e) {
            throw new IllegalStateException("Solving failed.", e);
        }
    }

}
