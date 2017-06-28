/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.curriculumcourse.domain.solver;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Period;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;

public class PeriodStrengthWeightFactory implements SelectionSorterWeightFactory<CourseSchedule, Period> {

    @Override
    public PeriodStrengthWeight createSorterWeight(CourseSchedule schedule, Period period) {
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

        @Override
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
