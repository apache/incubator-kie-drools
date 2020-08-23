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

import org.acme.maintenancescheduling.domain.MaintainableUnit;
import org.acme.maintenancescheduling.domain.MaintenanceCrew;
import org.acme.maintenancescheduling.domain.MaintenanceJob;
import org.acme.maintenancescheduling.domain.MaintenanceSchedule;
import org.acme.maintenancescheduling.domain.MutuallyExclusiveJobs;
import org.acme.maintenancescheduling.domain.TimeGrain;
import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

import java.util.Arrays;

public class MaintenanceSchedulingConstraintProviderTest {

    private final ConstraintVerifier<MaintenanceScheduleConstraintProvider, MaintenanceSchedule> constraintVerifier =
            ConstraintVerifier.build(new MaintenanceScheduleConstraintProvider(), MaintenanceSchedule.class,
                    MaintenanceJob.class);

    @Test
    public void jobsMustStartAfterReadyTimeGrainUnpenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::jobsMustStartAfterReadyTimeGrain)
                .given(maintenanceJob)
                .penalizesBy(0);
    }

    @Test
    public void jobsMustStartAfterReadyTimeGrainPenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 2, 10, 4, true);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::jobsMustStartAfterReadyTimeGrain)
                .given(maintenanceJob)
                .penalizesBy(2);
    }

    @Test
    public void jobsMustFinishBeforeDeadlineUnpenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::jobsMustFinishBeforeDeadline)
                .given(maintenanceJob)
                .penalizesBy(0);
    }

    @Test
    public void jobsMustFinishBeforeDeadlinePenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);
        TimeGrain startingTimeGrain = new TimeGrain(8);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::jobsMustFinishBeforeDeadline)
                .given(maintenanceJob)
                .penalizesBy(2);
    }

    @Test
    public void assignAllCriticalJobsUnpenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, false);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::assignAllCriticalJobs)
                .given(maintenanceJob)
                .penalizesBy(0);
    }

    @Test
    public void assignAllCriticalJobsPenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::assignAllCriticalJobs)
                .given(maintenanceJob)
                .penalizesBy(1);
    }

    @Test
    public void oneJobPerCrewPerPeriodUnpenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);
        MaintenanceCrew maintenanceCrew = new MaintenanceCrew("Maintenance crew");
        maintenanceJob.setAssignedCrew(maintenanceCrew);
        maintenanceJob.setId(0L);

        MaintainableUnit otherUnit = new MaintainableUnit("Other unit");
        MaintenanceJob otherJob = new MaintenanceJob("Other job", otherUnit, 0, 10, 4, true);
        TimeGrain otherTimeGrain = new TimeGrain(4);
        otherJob.setStartingTimeGrain(otherTimeGrain);
        otherJob.setAssignedCrew(maintenanceCrew);
        otherJob.setId(1L);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::oneJobPerCrewPerPeriod)
                .given(maintenanceJob, otherJob)
                .penalizesBy(0);
    }

    @Test
    public void oneJobPerCrewPerPeriodPenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);
        MaintenanceCrew maintenanceCrew = new MaintenanceCrew("Maintenance crew");
        maintenanceJob.setAssignedCrew(maintenanceCrew);
        maintenanceJob.setId(0L);

        MaintainableUnit otherUnit = new MaintainableUnit("Other unit");
        MaintenanceJob otherJob = new MaintenanceJob("Other job", otherUnit, 0, 10, 4, true);
        TimeGrain otherTimeGrain = new TimeGrain(2);
        otherJob.setStartingTimeGrain(otherTimeGrain);
        otherJob.setAssignedCrew(maintenanceCrew);
        otherJob.setId(1L);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::oneJobPerCrewPerPeriod)
                .given(maintenanceJob, otherJob)
                .penalizesBy(2);
    }

    @Test
    public void mutuallyExclusiveJobsUnpenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);
        maintenanceJob.setId(0L);

        MaintainableUnit otherUnit = new MaintainableUnit("Other unit");
        MaintenanceJob otherJob = new MaintenanceJob("Other job", otherUnit, 0, 10, 4, true);
        TimeGrain otherTimeGrain = new TimeGrain(2);
        otherJob.setStartingTimeGrain(otherTimeGrain);
        otherJob.setId(1L);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::mutuallyExclusiveJobs)
                .given(maintenanceJob, otherJob)
                .penalizesBy(0);
    }

    @Test
    public void mutuallyExclusiveJobsPenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);
        MaintenanceCrew maintenanceCrew = new MaintenanceCrew("Maintenance crew");
        TimeGrain startingTimeGrain = new TimeGrain(0);
        maintenanceJob.setAssignedCrew(maintenanceCrew);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);
        maintenanceJob.setId(0L);

        MaintainableUnit otherUnit = new MaintainableUnit("Other unit");
        MaintenanceJob otherJob = new MaintenanceJob("Other job", otherUnit, 0, 10, 4, true);
        TimeGrain otherTimeGrain = new TimeGrain(2);
        otherJob.setAssignedCrew(maintenanceCrew);
        otherJob.setStartingTimeGrain(otherTimeGrain);
        otherJob.setId(1L);

        MutuallyExclusiveJobs mutuallyExclusiveJobs =
                new MutuallyExclusiveJobs(maintenanceJob, otherJob);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::mutuallyExclusiveJobs)
                .given(maintenanceJob, otherJob, mutuallyExclusiveJobs)
                .penalizesBy(2);
    }

    @Test
    public void oneJobPerUnitPerPeriodUnpenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);
        maintenanceJob.setId(0L);

        MaintenanceJob otherJob = new MaintenanceJob("Other job", maintainableUnit, 0, 10, 4, true);
        TimeGrain otherTimeGrain = new TimeGrain(4);
        otherJob.setStartingTimeGrain(otherTimeGrain);
        otherJob.setId(1L);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::oneJobPerUnitPerPeriod)
                .given(maintenanceJob, otherJob)
                .penalizesBy(0);
    }

    @Test
    public void oneJobPerUnitPerPeriodPenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);
        MaintenanceCrew maintenanceCrew = new MaintenanceCrew("Maintenance crew");
        TimeGrain startingTimeGrain = new TimeGrain(0);
        maintenanceJob.setAssignedCrew(maintenanceCrew);
        maintenanceJob.setStartingTimeGrain(startingTimeGrain);
        maintenanceJob.setId(0L);

        MaintenanceJob otherJob = new MaintenanceJob("Other job", maintainableUnit, 0, 10, 4, true);
        TimeGrain otherTimeGrain = new TimeGrain(2);
        otherJob.setAssignedCrew(maintenanceCrew);
        otherJob.setStartingTimeGrain(otherTimeGrain);
        otherJob.setId(1L);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::oneJobPerUnitPerPeriod)
                .given(maintenanceJob, otherJob)
                .penalizesBy(2);
    }

    @Test
    public void assignAllNonCriticalJobsUnpenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, true);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::assignAllNonCriticalJobs)
                .given(maintenanceJob)
                .penalizesBy(0);
    }

    @Test
    public void assignAllNonCriticalJobsPenalized() {
        MaintainableUnit maintainableUnit = new MaintainableUnit("Test unit");
        MaintenanceJob maintenanceJob = new MaintenanceJob("Maintenance job", maintainableUnit, 0, 10, 4, false);

        constraintVerifier.verifyThat(MaintenanceScheduleConstraintProvider::assignAllNonCriticalJobs)
                .given(maintenanceJob)
                .penalizesBy(1);
    }
}
