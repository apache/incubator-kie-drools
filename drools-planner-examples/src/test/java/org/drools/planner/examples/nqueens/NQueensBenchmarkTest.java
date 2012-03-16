/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.nqueens;

import java.io.File;

import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.examples.common.app.PlannerBenchmarkTest;
import org.junit.Test;

public class NQueensBenchmarkTest extends PlannerBenchmarkTest {

    @Override
    protected String createBenchmarkConfigResource() {
        return "/org/drools/planner/examples/nqueens/benchmark/nqueensBenchmarkConfig.xml";
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void benchmark64Queens() {
        runBenchmarkTest(new File("data/nqueens/unsolved/unsolvedNQueens64.xml"));
    }

}
