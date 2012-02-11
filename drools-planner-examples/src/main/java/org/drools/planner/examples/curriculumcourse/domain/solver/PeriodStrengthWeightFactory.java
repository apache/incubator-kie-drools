/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.curriculumcourse.domain.solver;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.api.domain.variable.PlanningValueStrengthWeightFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.curriculumcourse.domain.Period;
import org.drools.planner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;

public class PeriodStrengthWeightFactory implements PlanningValueStrengthWeightFactory {

    public Comparable createStrengthWeight(Solution solution, Object planningValue) {
        CurriculumCourseSchedule schedule = (CurriculumCourseSchedule) solution;
        Period period = (Period) planningValue;
        int unavailablePeriodPenaltyCount = 0;
        for (UnavailablePeriodPenalty penalty : schedule.getUnavailablePeriodPenaltyList()) {
            if (penalty.getPeriod().equals(period)) {
                unavailablePeriodPenaltyCount++;
            }
        }
        return new PeriodStrengthWeight(period, unavailablePeriodPenaltyCount);
    }

    public static class PeriodStrengthWeight implements Comparable<PeriodStrengthWeight> {

        private final Period period;
        private final int unavailablePeriodPenaltyCount;

        public PeriodStrengthWeight(Period period, int unavailablePeriodPenaltyCount) {
            this.period = period;
            this.unavailablePeriodPenaltyCount = unavailablePeriodPenaltyCount;
        }

        public int compareTo(PeriodStrengthWeight other) {
            return new CompareToBuilder()
                    // The higher unavailablePeriodPenaltyCount, the weaker
                    .append(other.unavailablePeriodPenaltyCount, unavailablePeriodPenaltyCount) // Descending
                    .append(period.getDay().getDayIndex(), other.period.getDay().getDayIndex())
                    .append(period.getTimeslot().getTimeslotIndex(), other.period.getTimeslot().getTimeslotIndex())
                    .append(period.getId(), other.period.getId())
                    .toComparison();
        }

    }

}
