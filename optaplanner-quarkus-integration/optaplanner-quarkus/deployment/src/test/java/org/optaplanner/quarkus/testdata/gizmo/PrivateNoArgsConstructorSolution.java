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

package org.optaplanner.quarkus.testdata.gizmo;

import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

@PlanningSolution
public class PrivateNoArgsConstructorSolution {
    @PlanningEntityCollectionProperty
    List<PrivateNoArgsConstructorEntity> planningEntityList;

    @PlanningScore
    public final SimpleScore score;

    private PrivateNoArgsConstructorSolution() {
        score = null;
    }

    public PrivateNoArgsConstructorSolution(List<PrivateNoArgsConstructorEntity> planningEntityList) {
        this.planningEntityList = planningEntityList;
        score = null;
    }

    @ValueRangeProvider(id = "valueRange")
    public List<String> valueRange() {
        return Arrays.asList("1", "2", "3");
    }
}
