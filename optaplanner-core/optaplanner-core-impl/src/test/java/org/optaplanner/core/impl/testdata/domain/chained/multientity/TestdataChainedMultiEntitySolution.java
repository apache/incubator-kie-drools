/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.testdata.domain.chained.multientity;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

@PlanningSolution
public class TestdataChainedMultiEntitySolution {

    private List<TestdataChainedBrownEntity> brownEntities;
    private List<TestdataChainedGreenEntity> greenEntities;
    private List<TestdataChainedMultiEntityAnchor> anchors;
    private SimpleScore score;

    public TestdataChainedMultiEntitySolution() {
    }

    public TestdataChainedMultiEntitySolution(
            List<TestdataChainedBrownEntity> brownEntities,
            List<TestdataChainedGreenEntity> greenEntities,
            List<TestdataChainedMultiEntityAnchor> anchors) {
        this.brownEntities = brownEntities;
        this.greenEntities = greenEntities;
        this.anchors = anchors;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "brownRange")
    public List<TestdataChainedBrownEntity> getBrownEntities() {
        return brownEntities;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "greenRange")
    public List<TestdataChainedGreenEntity> getGreenEntities() {
        return greenEntities;
    }

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "anchorRange")
    public List<TestdataChainedMultiEntityAnchor> getAnchors() {
        return anchors;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }
}
