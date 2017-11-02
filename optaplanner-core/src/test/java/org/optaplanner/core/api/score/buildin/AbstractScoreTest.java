/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin;

import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

import static org.junit.Assert.*;

public abstract class AbstractScoreTest {

    public static void assertScoreNotFeasible(FeasibilityScore... scores) {
        for (FeasibilityScore score : scores) {
            assertEquals(score + " should not be feasible.", false, score.isFeasible());
        }
    }

    public static void assertScoreFeasible(FeasibilityScore ... scores) {
        for (FeasibilityScore score : scores) {
            assertEquals(score + " should be feasible.", true, score.isFeasible());
        }
    }

}
