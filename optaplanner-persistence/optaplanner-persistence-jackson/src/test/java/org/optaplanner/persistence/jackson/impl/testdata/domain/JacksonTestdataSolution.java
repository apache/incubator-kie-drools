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

package org.optaplanner.persistence.jackson.impl.testdata.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.persistence.jackson.api.score.buildin.simple.SimpleScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simple.SimpleScoreJacksonSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@PlanningSolution
public class JacksonTestdataSolution extends JacksonTestdataObject {

    public static SolutionDescriptor<JacksonTestdataSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(JacksonTestdataSolution.class, JacksonTestdataEntity.class);
    }

    private List<JacksonTestdataValue> valueList;
    private List<JacksonTestdataEntity> entityList;

    private SimpleScore score;

    public JacksonTestdataSolution() {
    }

    public JacksonTestdataSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<JacksonTestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<JacksonTestdataValue> valueList) {
        this.valueList = valueList;
    }

    @PlanningEntityCollectionProperty
    public List<JacksonTestdataEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<JacksonTestdataEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    @JsonSerialize(using = SimpleScoreJacksonSerializer.class)
    @JsonDeserialize(using = SimpleScoreJacksonDeserializer.class)
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
