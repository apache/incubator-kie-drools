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

package org.optaplanner.examples.nqueens.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.nqueens.domain.NQueens;

class NQueensSmokeTest extends SolverSmokeTest<NQueens, SimpleScore> {

    @Override
    protected NQueensApp createCommonApp() {
        return new NQueensApp();
    }

    @Override
    protected Stream<TestData<SimpleScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, "data/nqueens/unsolved/16queens.json",
                        SimpleScore.ZERO,
                        SimpleScore.ZERO,
                        SimpleScore.ZERO),
                TestData.of(ConstraintStreamImplType.BAVET, "data/nqueens/unsolved/16queens.json",
                        SimpleScore.ZERO,
                        SimpleScore.ZERO,
                        SimpleScore.ZERO));
    }
}
