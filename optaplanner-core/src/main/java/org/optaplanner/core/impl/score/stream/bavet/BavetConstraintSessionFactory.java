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

package org.optaplanner.core.impl.score.stream.bavet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.core.impl.score.stream.ConstraintSessionFactory;

public final class BavetConstraintSessionFactory<Solution_, Score_ extends Score<Score_>>
        implements ConstraintSessionFactory<Solution_, Score_> {

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

    @Override
    public ConstraintSession<Solution_, Score_> buildSession(boolean constraintMatchEnabled,
            Solution_ workingSolution) {
        ScoreDefinition<Score_> scoreDefinition = solutionDescriptor.getScoreDefinition();
        Score_ zeroScore = scoreDefinition.getZeroScore();
        Map<BavetConstraint<Solution_>, Score_> constraintToWeightMap = new LinkedHashMap<>(constraintList.size());
        for (BavetConstraint<Solution_> constraint : constraintList) {
            Score_ constraintWeight = (Score_) constraint.extractConstraintWeight(workingSolution);
            if (!constraintWeight.equals(zeroScore)) {
                constraintToWeightMap.put(constraint, constraintWeight);
            }
        }
        return new BavetConstraintSession<>(constraintMatchEnabled, scoreDefinition, constraintToWeightMap);
    }

}
