/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public final class BavetConstraintSessionFactory<Solution_, Score_ extends Score<Score_>> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final List<BavetConstraint<Solution_>> constraintList;

    public BavetConstraintSessionFactory(SolutionDescriptor<Solution_> solutionDescriptor,
            List<BavetConstraint<Solution_>> constraintList) {
        this.solutionDescriptor = solutionDescriptor;
        this.constraintList = constraintList;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public BavetConstraintSession<Solution_, Score_> buildSession(boolean constraintMatchEnabled,
            Solution_ workingSolution) {
        ScoreDefinition<Score_> scoreDefinition = solutionDescriptor.getScoreDefinition();
        Score_ zeroScore = scoreDefinition.getZeroScore();
        // Extract constraint weights, excluding constraints where weight is zero.
        Map<BavetConstraint<Solution_>, Score_> constraintToWeightMap =
                constraintList.stream()
                        .map(constraint -> {
                            Score_ weight = constraint.extractConstraintWeight(workingSolution); // Expensive, only do once.
                            return new Object[] { constraint, weight };
                        })
                        .filter(constraintAndWeight -> !constraintAndWeight[1].equals(zeroScore))
                        .collect(Collectors.toMap(
                                constraintAndWeight -> (BavetConstraint<Solution_>) constraintAndWeight[0],
                                constraintAndWeight -> (Score_) constraintAndWeight[1]));
        return new BavetConstraintSession<>(constraintMatchEnabled, scoreDefinition, constraintToWeightMap);
    }

}
