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

package org.acme.maintenancescheduling.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@ConstraintConfiguration
public class MaintenanceSchedulingConstraintConfiguration {

    @ConstraintWeight("Jobs must start after ready time grain")
    public HardSoftScore jobsMustStartAfterReadyTimeGrain = HardSoftScore.ofHard(1);
    @ConstraintWeight("Jobs must finish before deadline")
    public HardSoftScore jobsMustFinishBeforeDeadline = HardSoftScore.ofHard(1);
    @ConstraintWeight("Assign all critical jobs")
    public HardSoftScore assignAllCriticalJobs = HardSoftScore.ofHard(10);
    @ConstraintWeight("One job per crew per period")
    public HardSoftScore oneJobPerCrewPerPeriod = HardSoftScore.ofHard(1);
    @ConstraintWeight("Mutually exclusive jobs cannot overlap")
    public HardSoftScore mutuallyExclusiveJobs = HardSoftScore.ofHard(1);
    @ConstraintWeight("One job per unit per period")
    public HardSoftScore oneJobPerUnitPerPeriod = HardSoftScore.ofHard(1);

    @ConstraintWeight("Assign all non critical jobs")
    private HardSoftScore assignAllNonCriticalJobs = HardSoftScore.ofSoft(10);
}
