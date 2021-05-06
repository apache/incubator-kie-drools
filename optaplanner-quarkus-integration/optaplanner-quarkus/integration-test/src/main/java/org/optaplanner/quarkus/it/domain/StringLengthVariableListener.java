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

package org.optaplanner.quarkus.it.domain;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public class StringLengthVariableListener
        implements VariableListener<TestdataStringLengthShadowSolution, TestdataStringLengthShadowEntity> {

    @Override
    public void beforeEntityAdded(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    @Override
    public void afterEntityAdded(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    @Override
    public void afterVariableChanged(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        int oldLength = (entity.getLength() != null) ? entity.getLength() : 0;
        int newLength = getLength(entity.getValue());
        if (oldLength != newLength) {
            scoreDirector.beforeVariableChanged(entity, "length");
            entity.setLength(getLength(entity.getValue()));
            scoreDirector.afterVariableChanged(entity, "length");
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    private static int getLength(String value) {
        if (value != null) {
            return value.length();
        } else {
            return 0;
        }
    }
}
