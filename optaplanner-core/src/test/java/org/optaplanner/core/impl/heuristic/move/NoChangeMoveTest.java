/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.move;

import org.junit.Test;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.assertEquals;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.*;

public class NoChangeMoveTest {

    @Test
    public void isMoveDoable() {
        assertEquals(true, new NoChangeMove<>().isMoveDoable(null));
    }

    @Test
    public void createUndoMove() {
        assertInstanceOf(NoChangeMove.class, new NoChangeMove<>().createUndoMove(null));
    }

    @Test
    public void getPlanningEntities() {
        assertEquals(true, new NoChangeMove<>().getPlanningEntities().isEmpty());
    }

    @Test
    public void getPlanningValues() {
        assertEquals(true, new NoChangeMove<>().getPlanningValues().isEmpty());
    }

    @Test
    public void rebase() {
        ScoreDirector<TestdataSolution> destinationScoreDirector = mockRebasingScoreDirector(
                TestdataSolution.buildSolutionDescriptor(), new Object[][]{});
        NoChangeMove<TestdataSolution> move = new NoChangeMove<>();
        move.rebase(destinationScoreDirector);
    }

}
