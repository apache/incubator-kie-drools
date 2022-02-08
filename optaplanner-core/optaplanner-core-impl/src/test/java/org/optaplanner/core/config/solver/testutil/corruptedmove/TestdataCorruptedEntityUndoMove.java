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

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataCorruptedEntityUndoMove extends AbstractTestdataMove {

    public TestdataCorruptedEntityUndoMove(TestdataEntity entity, TestdataValue toValue) {
        super(entity, toValue);
    }

    @Override
    protected AbstractMove<TestdataSolution> createUndoMove(ScoreDirector<TestdataSolution> scoreDirector) {
        // Corrupts the undo move by creating a new entity and not undo-ing the value
        return new TestdataCorruptedEntityUndoMove(new TestdataEntity("corrupted"), toValue);
    }
}
