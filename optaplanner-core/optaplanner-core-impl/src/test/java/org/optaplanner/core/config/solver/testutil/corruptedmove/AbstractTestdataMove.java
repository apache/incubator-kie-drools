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

package org.optaplanner.core.config.solver.testutil.corruptedmove;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public abstract class AbstractTestdataMove extends AbstractMove<TestdataSolution> {

    TestdataEntity entity;
    TestdataValue toValue;

    AbstractTestdataMove(TestdataEntity entity, TestdataValue toValue) {
        this.entity = entity;
        this.toValue = toValue;
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return Collections.singletonList(entity);
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Collections.singletonList(toValue);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<TestdataSolution> scoreDirector) {
        scoreDirector.beforeVariableChanged(entity, "value");
        entity.setValue(toValue);
        scoreDirector.afterVariableChanged(entity, "value");
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<TestdataSolution> scoreDirector) {
        return !Objects.equals(entity.getValue(), toValue);
    }

    @Override
    public String toString() {
        return entity + " -> " + toValue;
    }
}
