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

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Period;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;

import static com.google.common.base.Functions.identity;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

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

        // The higher unavailablePeriodPenaltyCount, the weaker
        private static final Comparator<PeriodStrengthWeight> BASE_COMPARATOR =
                reverseOrder(comparingInt((PeriodStrengthWeight w) -> w.unavailablePeriodPenaltyCount));
        private static final Comparator<Period> PERIOD_COMPARATOR = comparingInt((Period p) -> p.getDay().getDayIndex())
                .thenComparingInt(p -> p.getTimeslot().getTimeslotIndex())
                .thenComparingLong(Period::getId);
        private static final Comparator<PeriodStrengthWeight> COMPARATOR = comparing(identity(), BASE_COMPARATOR)
                .thenComparing(w -> w.period, PERIOD_COMPARATOR);

        private final Period period;
        private final int unavailablePeriodPenaltyCount;

        public PeriodStrengthWeight(Period period, int unavailablePeriodPenaltyCount) {
            this.period = period;
            this.unavailablePeriodPenaltyCount = unavailablePeriodPenaltyCount;
        }

        @Override
        public int compareTo(PeriodStrengthWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
