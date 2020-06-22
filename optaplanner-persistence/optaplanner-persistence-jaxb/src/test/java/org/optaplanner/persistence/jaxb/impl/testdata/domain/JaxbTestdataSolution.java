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

package org.optaplanner.persistence.jaxb.impl.testdata.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.persistence.jaxb.api.score.buildin.simple.SimpleScoreJaxbAdapter;

@PlanningSolution
@XmlRootElement

public class JaxbTestdataSolution extends JaxbTestdataObject {

    public static SolutionDescriptor<JaxbTestdataSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(JaxbTestdataSolution.class, JaxbTestdataEntity.class);
    }

    private List<JaxbTestdataValue> valueList;
    private List<JaxbTestdataEntity> entityList;

    private SimpleScore score;

    public JaxbTestdataSolution() {
    }

    public JaxbTestdataSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    @XmlElementWrapper(name = "valueList")
    @XmlElement(name = "jaxbTestdataValue")
    public List<JaxbTestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<JaxbTestdataValue> valueList) {
        this.valueList = valueList;
    }

    @PlanningEntityCollectionProperty
    @XmlElementWrapper(name = "entityList")
    @XmlElement(name = "jaxbTestdataEntity")
    public List<JaxbTestdataEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<JaxbTestdataEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    @XmlJavaTypeAdapter(SimpleScoreJaxbAdapter.class)
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
