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

package org.acme.maintenancescheduling.rest;

import io.quarkus.panache.common.Sort;
import org.acme.maintenancescheduling.domain.MaintainableUnit;
import org.acme.maintenancescheduling.domain.MaintenanceCrew;
import org.acme.maintenancescheduling.domain.MaintenanceJob;
import org.acme.maintenancescheduling.domain.MaintenanceSchedule;
import org.acme.maintenancescheduling.domain.MutuallyExclusiveJobs;
import org.acme.maintenancescheduling.domain.TimeGrain;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MaintenanceScheduleResource {

    public static final Long SINGLETON_SCHEDULE_ID = 1L;

    @Inject
    SolverManager<MaintenanceSchedule, Long> solverManager;
    @Inject
    ScoreManager<MaintenanceSchedule> scoreManager;

    // To try, open http://localhost:8080/schedule
    @GET
    public MaintenanceSchedule getSchedule() {
        // Get the solver status before loading the solution
        // to avoid the race condition that the solver terminates between them
        SolverStatus solverStatus = getSolverStatus();
        MaintenanceSchedule solution = findById(SINGLETON_SCHEDULE_ID);
        scoreManager.updateScore(solution); // Sets the score
        solution.setSolverStatus(solverStatus);
        return solution;
    }

    public SolverStatus getSolverStatus() {
        return solverManager.getSolverStatus(SINGLETON_SCHEDULE_ID);
    }

    @POST
    @Path("/solve")
    public void solve() {
        solverManager.solveAndListen(SINGLETON_SCHEDULE_ID,
                this::findById,
                this::save);
    }

    @POST
    @Path("/stopSolving")
    public void stopSolving() {
        solverManager.terminateEarly(SINGLETON_SCHEDULE_ID);
    }

    @Transactional
    protected MaintenanceSchedule findById(Long id) {
        if (!SINGLETON_SCHEDULE_ID.equals(id)) {
            throw new IllegalStateException("There is no schedule with id (" + id + ").");
        }

        return new MaintenanceSchedule(
                MaintainableUnit.listAll(Sort.by("unitName").and("id")),
                MutuallyExclusiveJobs.listAll(Sort.by("id")),
                MaintenanceCrew.listAll(Sort.by("crewName").and("id")),
                TimeGrain.listAll(Sort.by("grainIndex").and("id")),
                MaintenanceJob.listAll(Sort.by("maintainableUnit")
                        .and("startingTimeGrain").and("readyGrainIndex").and("deadlineGrainIndex").and("id")));
    }

    @Transactional
    protected void save(MaintenanceSchedule schedule) {
        for (MaintenanceJob job : schedule.getMaintenanceJobList()) {
            // TODO this is awfully naive: optimistic locking causes issues if called by the SolverManager
            MaintenanceJob persistedJob = MaintenanceJob.findById(job.getId());
            persistedJob.setStartingTimeGrain(job.getStartingTimeGrain());
            persistedJob.setAssignedCrew(job.getAssignedCrew());
        }
    }
}
