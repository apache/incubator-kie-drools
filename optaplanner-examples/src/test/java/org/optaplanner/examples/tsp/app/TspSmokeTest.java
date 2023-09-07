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

package org.optaplanner.examples.tsp.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.tsp.domain.TspSolution;

class TspSmokeTest extends SolverSmokeTest<TspSolution, SimpleLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/tsp/unsolved/europe40.json";

    @Override
    protected TspApp createCommonApp() {
        return new TspApp();
    }

    @Override
    protected Stream<TestData<SimpleLongScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, UNSOLVED_DATA_FILE,
                        SimpleLongScore.of(-217364246),
                        SimpleLongScore.of(-217364246)),
                TestData.of(ConstraintStreamImplType.BAVET, UNSOLVED_DATA_FILE,
                        SimpleLongScore.of(-216469618),
                        SimpleLongScore.of(-217364246)));
    }
}
