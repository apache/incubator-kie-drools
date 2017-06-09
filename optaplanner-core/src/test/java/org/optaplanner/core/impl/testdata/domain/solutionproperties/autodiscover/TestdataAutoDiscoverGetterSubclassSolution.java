/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover;

import java.util.List;

import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataAutoDiscoverGetterSubclassSolution extends TestdataAutoDiscoverGetterSolution {

    private TestdataObject singleProblemFactFieldOverride;
    private List<TestdataValue> problemFactListFieldOverride;

    private List<TestdataEntity> entityListFieldOverride;
    private TestdataEntity otherEntityFieldOverride;

    public TestdataAutoDiscoverGetterSubclassSolution() {
    }

    public TestdataAutoDiscoverGetterSubclassSolution(String code) {
        super(code);
    }

    public TestdataAutoDiscoverGetterSubclassSolution(String code, TestdataObject singleProblemFact,
            List<TestdataValue> problemFactList, List<TestdataEntity> entityList,
            TestdataEntity otherEntity) {
        super(code);
        this.singleProblemFactFieldOverride = singleProblemFact;
        this.problemFactListFieldOverride = problemFactList;
        this.entityListFieldOverride = entityList;
        this.otherEntityFieldOverride = otherEntity;
    }

    @Override
    public TestdataObject getSingleProblemFact() {
        return singleProblemFactFieldOverride;
    }

    @ProblemFactProperty // CHANGE! from a collection to a single fact
    @Override
    public List<TestdataValue> getProblemFactList() {
        return problemFactListFieldOverride;
    }

    @Override
    public List<TestdataEntity> getEntityList() {
        return entityListFieldOverride;
    }

    @Override
    public TestdataEntity getOtherEntity() {
        return otherEntityFieldOverride;
    }

}
