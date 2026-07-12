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

package org.optaplanner.core.impl.testdata.domain.constraintconfiguration;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;

public final class TestdataConstraintWeighIncrementalScoreCalculator
        implements IncrementalScoreCalculator<TestdataConstraintConfigurationSolution, SimpleScore> {

    private TestdataConstraintConfigurationSolution workingSolution;
    private List<TestdataEntity> entityList;

    @Override
    public void resetWorkingSolution(TestdataConstraintConfigurationSolution workingSolution) {
        this.workingSolution = workingSolution;
        this.entityList = new ArrayList<>(workingSolution.getEntityList());
    }

    @Override
    public void beforeEntityAdded(Object entity) {
        // No need to do anything.
    }

    @Override
    public void afterEntityAdded(Object entity) {
        entityList.add((TestdataEntity) entity);
    }

    @Override
    public void beforeVariableChanged(Object entity, String variableName) {
        throw new UnsupportedOperationException(); // Will not be called.
    }

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        throw new UnsupportedOperationException(); // Will not be called.
    }

    @Override
    public void beforeEntityRemoved(Object entity) {
        // No need to do anything.
    }

    @Override
    public void afterEntityRemoved(Object entity) {
        entityList.remove((TestdataEntity) entity);
    }

    @Override
    public SimpleScore calculateScore() {
        SimpleScore constraintWeight = workingSolution.getConstraintConfiguration().getFirstWeight();
        return constraintWeight.multiply(entityList.size());
    }
}
