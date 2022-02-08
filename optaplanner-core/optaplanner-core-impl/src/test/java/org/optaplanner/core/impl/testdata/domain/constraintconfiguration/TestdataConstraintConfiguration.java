/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.constraintconfiguration;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@ConstraintConfiguration
public class TestdataConstraintConfiguration extends TestdataObject {

    private SimpleScore firstWeight = SimpleScore.of(1);
    private SimpleScore secondWeight = SimpleScore.of(20);

    public TestdataConstraintConfiguration() {
        super();
    }

    public TestdataConstraintConfiguration(String code) {
        super(code);
    }

    @ConstraintWeight("First weight")
    public SimpleScore getFirstWeight() {
        return firstWeight;
    }

    public void setFirstWeight(SimpleScore firstWeight) {
        this.firstWeight = firstWeight;
    }

    @ConstraintWeight(constraintPackage = "packageOverwrittenOnField", value = "Second weight")
    public SimpleScore getSecondWeight() {
        return secondWeight;
    }

    public void setSecondWeight(SimpleScore secondWeight) {
        this.secondWeight = secondWeight;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
