/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.spring.boot.example.solver;

import java.time.Duration;
import java.util.function.Function;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.uni.DefaultUniConstraintCollector;
import org.optaplanner.spring.boot.example.domain.Investigation;

public class PoliceConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                workDuration(constraintFactory)
        };
    }

    private Constraint workDuration(ConstraintFactory constraintFactory) {
        return constraintFactory
                .from(Investigation.class)
                .groupBy(Investigation::getDetective, sumDuration(Investigation::getEstimatedDuration))
                .filter((detective, durationTotal) -> detective.getWorkDuration().compareTo(durationTotal) < 0)
                .penalize("Work duration", HardSoftScore.ONE_HARD);
    }

    // WORKAROUND
    public static <A> UniConstraintCollector<A, ?, Duration> sumDuration(Function<? super A, Duration> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                () -> new Duration[]{Duration.ZERO},
                (resultContainer, a) -> {
                    Duration value = groupValueMapping.apply(a);
                    resultContainer[0] = resultContainer[0].plus(value);
                    return (() -> resultContainer[0] = resultContainer[0].minus(value));
                },
                resultContainer -> resultContainer[0]);
    }

}
