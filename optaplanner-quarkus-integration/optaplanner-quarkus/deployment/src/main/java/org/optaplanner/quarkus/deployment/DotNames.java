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

package org.optaplanner.quarkus.deployment;

import org.jboss.jandex.DotName;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;

public final class DotNames {

    static final DotName PLANNING_SOLUTION = DotName.createSimple(PlanningSolution.class.getName());
    static final DotName PLANNING_ENTITY = DotName.createSimple(PlanningEntity.class.getName());
    static final DotName EASY_SCORE_CALCULATOR = DotName.createSimple(EasyScoreCalculator.class.getName());
    static final DotName CONSTRAINT_PROVIDER = DotName.createSimple(ConstraintProvider.class.getName());
    static final DotName INCREMENTAL_SCORE_CALCULATOR =
            DotName.createSimple(IncrementalScoreCalculator.class.getName());

    private DotNames() {
    }

}
