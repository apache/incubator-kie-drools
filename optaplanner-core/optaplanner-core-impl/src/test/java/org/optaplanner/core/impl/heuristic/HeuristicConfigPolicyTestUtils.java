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

package org.optaplanner.core.impl.heuristic;

import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.solver.ClassInstanceCache;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public final class HeuristicConfigPolicyTestUtils {

    public static HeuristicConfigPolicy<TestdataSolution> buildHeuristicConfigPolicy() {
        return buildHeuristicConfigPolicy(TestdataSolution.buildSolutionDescriptor());
    }

    public static <Solution_> HeuristicConfigPolicy<Solution_>
            buildHeuristicConfigPolicy(SolutionDescriptor<Solution_> solutionDescriptor) {
        return new HeuristicConfigPolicy.Builder<>(EnvironmentMode.REPRODUCIBLE, null, null, null, null, solutionDescriptor,
                ClassInstanceCache.create())
                .build();
    }

    private HeuristicConfigPolicyTestUtils() {
    }
}
