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

package org.optaplanner.core.impl.testdata.domain.deprecated;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataAnnotatedExtendedEntity;

/**
 * @deprecated for removal with the deprecated annotations
 */
@Deprecated(/* forRemoval = true */)
@PlanningSolution
public class TestdataAnnotatedWithDeprecatedAnnotationsSolution extends TestdataSolution {

    public static SolutionDescriptor<TestdataAnnotatedWithDeprecatedAnnotationsSolution> buildExtendedSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataAnnotatedWithDeprecatedAnnotationsSolution.class,
                TestdataEntity.class, TestdataAnnotatedExtendedEntity.class);
    }

    private TestdataValue value;

    private List<TestdataValue> subValueList;

    private List<TestdataAnnotatedExtendedEntity> subEntityList;

    public TestdataAnnotatedWithDeprecatedAnnotationsSolution() {
    }

    public TestdataAnnotatedWithDeprecatedAnnotationsSolution(String code) {
        super(code);
    }

    @ProblemFactProperty
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(final TestdataValue value) {
        this.value = value;
    }

    @ValueRangeProvider(id = "subValueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> getSubValueList() {
        return subValueList;
    }

    public void setSubValueList(List<TestdataValue> subValueList) {
        this.subValueList = subValueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataAnnotatedExtendedEntity> getSubEntityList() {
        return subEntityList;
    }

    public void setSubEntityList(List<TestdataAnnotatedExtendedEntity> subEntityList) {
        this.subEntityList = subEntityList;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
