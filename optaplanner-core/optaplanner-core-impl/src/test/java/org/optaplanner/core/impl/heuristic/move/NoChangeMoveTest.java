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

package org.optaplanner.core.impl.heuristic.move;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class NoChangeMoveTest {

    @Test
    void isMoveDoable() {
        assertThat(new NoChangeMove<>().isMoveDoable(null)).isTrue();
    }

    @Test
    void createUndoMove() {
        assertThat(new NoChangeMove<>().createUndoMove(null))
                .isInstanceOf(NoChangeMove.class);
    }

    @Test
    void getPlanningEntities() {
        assertThat(new NoChangeMove<>().getPlanningEntities()).isEmpty();
    }

    @Test
    void getPlanningValues() {
        assertThat(new NoChangeMove<>().getPlanningValues()).isEmpty();
    }

    @Test
    void rebase() {
        ScoreDirector<TestdataSolution> destinationScoreDirector = mockRebasingScoreDirector(
                TestdataSolution.buildSolutionDescriptor(), new Object[][] {});
        NoChangeMove<TestdataSolution> move = new NoChangeMove<>();
        move.rebase(destinationScoreDirector);
    }

}
