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

package org.optaplanner.core.api.score.constraint;

import static org.optaplanner.core.api.score.buildin.simple.SimpleScore.ONE;
import static org.optaplanner.core.api.score.buildin.simple.SimpleScore.ZERO;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.DefaultConstraintJustification;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class ConstraintMatchTest {

    @Test
    void equalsAndHashCode() { // No CM should equal any other.
        ConstraintMatch<SimpleScore> constraintMatch = buildConstraintMatch("a. b", "c", ZERO, "e1");
        PlannerAssert.assertObjectsAreEqual(constraintMatch, constraintMatch);
        ConstraintMatch<SimpleScore> constraintMatch2 = buildConstraintMatch("a. b", "c", ZERO, "e1");
        // Cast to avoid Comparable checks.
        PlannerAssert.assertObjectsAreNotEqual(constraintMatch, (Object) constraintMatch2);
    }

    private <Score_ extends Score<Score_>> ConstraintMatch<Score_> buildConstraintMatch(String constraintPackage,
            String constraintName, Score_ score, Object... indictments) {
        return new ConstraintMatch<>(constraintPackage, constraintName, DefaultConstraintJustification.of(score, indictments),
                Arrays.asList(indictments), score);
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                buildConstraintMatch("a.b", "a", ZERO, "a"),
                buildConstraintMatch("a.b", "a", ZERO, "a", "aa"),
                buildConstraintMatch("a.b", "a", ZERO, "a", "ab"),
                buildConstraintMatch("a.b", "a", ZERO, "a", "c"),
                buildConstraintMatch("a.b", "a", ZERO, "a", "aa", "a"),
                buildConstraintMatch("a.b", "a", ZERO, "a", "aa", "b"),
                buildConstraintMatch("a.b", "a", ONE, "a", "aa"),
                buildConstraintMatch("a.b", "b", ZERO, "a", "aa"),
                buildConstraintMatch("a.b", "b", ZERO, "a", "ab"),
                buildConstraintMatch("a.b", "b", ZERO, "a", "c"),
                buildConstraintMatch("a.c", "a", ZERO, "a", "aa"),
                buildConstraintMatch("a.c", "a", ZERO, "a", "ab"),
                buildConstraintMatch("a.c", "a", ZERO, "a", "c"));
    }

}
