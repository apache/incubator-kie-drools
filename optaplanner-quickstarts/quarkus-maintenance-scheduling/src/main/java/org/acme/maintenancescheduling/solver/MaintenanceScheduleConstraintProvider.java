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

package org.acme.maintenancescheduling.solver;

import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;

import org.acme.maintenancescheduling.domain.MaintenanceJob;
import org.acme.maintenancescheduling.domain.MutuallyExclusiveJobs;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public class MaintenanceScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                jobsMustStartAfterReadyTimeGrain(constraintFactory),
                jobsMustFinishBeforeDeadline(constraintFactory),
                assignAllCriticalJobs(constraintFactory),
                oneJobPerCrewPerPeriod(constraintFactory),
                mutuallyExclusiveJobs(constraintFactory),
                oneJobPerUnitPerPeriod(constraintFactory),
                // Soft constraints
                assignAllNonCriticalJobs(constraintFactory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    public Constraint jobsMustStartAfterReadyTimeGrain(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(MaintenanceJob.class)
                .filter(maintenanceJob -> maintenanceJob.getStartingTimeGrain() != null
                        && maintenanceJob.getStartingTimeGrain().getGrainIndex() < maintenanceJob.getReadyGrainIndex())
                .penalizeConfigurable("Jobs must start after ready time grain",
                        maintenanceJob -> maintenanceJob.getReadyGrainIndex()
                                - maintenanceJob.getStartingTimeGrain().getGrainIndex());
    }

    public Constraint jobsMustFinishBeforeDeadline(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(MaintenanceJob.class)
                .filter(maintenanceJob -> maintenanceJob.getStartingTimeGrain() != null
                        && maintenanceJob.getStartingTimeGrain().getGrainIndex()
                                + maintenanceJob.getDurationInGrains() > maintenanceJob.getDeadlineGrainIndex())
                .penalizeConfigurable("Jobs must finish before deadline",
                        maintenanceJob -> maintenanceJob.getStartingTimeGrain().getGrainIndex()
                                + maintenanceJob.getDurationInGrains() - maintenanceJob.getDeadlineGrainIndex());
    }

    public Constraint assignAllCriticalJobs(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(MaintenanceJob.class)
                // Critical maintenance jobs must be assigned a crew and start period
                .filter(maintenanceJob -> maintenanceJob.isCritical() && (maintenanceJob.getAssignedCrew() == null
                        || maintenanceJob.getStartingTimeGrain() == null))
                .penalizeConfigurable("Assign all critical jobs");
    }

    public Constraint oneJobPerCrewPerPeriod(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(MaintenanceJob.class)
                .filter(maintenanceJob -> maintenanceJob.getStartingTimeGrain() != null
                        && maintenanceJob.getAssignedCrew() != null)
                .join(MaintenanceJob.class,
                        equal(MaintenanceJob::getAssignedCrew),
                        lessThan(MaintenanceJob::getId),
                        filtering((maintenanceJob, otherJob) -> maintenanceJob.calculateOverlap(otherJob) > 0))
                .penalizeConfigurable("One job per crew per period", MaintenanceJob::calculateOverlap);
    }

    public Constraint mutuallyExclusiveJobs(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(MaintenanceJob.class)
                .filter(maintenanceJob -> maintenanceJob.getStartingTimeGrain() != null)
                .join(MaintenanceJob.class,
                        lessThan(MaintenanceJob::getId),
                        filtering((maintenanceJob, otherJob) -> maintenanceJob.calculateOverlap(otherJob) > 0))
                .join(MutuallyExclusiveJobs.class,
                        filtering((maintenanceJob, otherJob, mutexJobs) -> mutexJobs.isMutuallyExclusive(maintenanceJob,
                                otherJob)))
                .penalizeConfigurable("Mutually exclusive jobs cannot overlap",
                        (maintenanceJob, otherJob, mutexJobs) -> maintenanceJob.calculateOverlap(otherJob));
    }

    public Constraint oneJobPerUnitPerPeriod(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(MaintenanceJob.class)
                .filter(maintenanceJob -> maintenanceJob.getStartingTimeGrain() != null)
                .join(MaintenanceJob.class,
                        equal(MaintenanceJob::getMaintainableUnit),
                        lessThan(MaintenanceJob::getId),
                        filtering((maintenanceJob, otherJob) -> maintenanceJob.calculateOverlap(otherJob) > 0))
                .penalizeConfigurable("One job per unit per period", MaintenanceJob::calculateOverlap);
    }

    // TODO: Add constraint that prevents specific unit from being maintained at certain period (outside of MVP)

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    public Constraint assignAllNonCriticalJobs(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUnfiltered(MaintenanceJob.class)
                // Non critical maintenance jobs must be assigned a crew and start period
                .filter(maintenanceJob -> !maintenanceJob.isCritical()
                        && (maintenanceJob.getAssignedCrew() == null || maintenanceJob.getStartingTimeGrain() == null))
                .penalizeConfigurable("Assign all non critical jobs");
    }

    // TODO: Risk: when job completion date falls within the “SafetyMargin” before the due date,
    //  square the time between the start of the “SafetyMargin” and completion date, sum for all jobs
    //  (quadratic, will scale faster. Sq root the sum of squares)

}
