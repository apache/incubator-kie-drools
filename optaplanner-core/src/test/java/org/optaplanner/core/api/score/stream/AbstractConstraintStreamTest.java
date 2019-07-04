/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishSolution;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public abstract class AbstractConstraintStreamTest {

    @Parameterized.Parameters(name = "constraintMatchEnabled={0}")
    public static Object[] data() {
        return new Object[]{
                false, true
        };
    }

    protected boolean constraintMatchEnabled;

    public AbstractConstraintStreamTest(boolean constraintMatchEnabled) {
        this.constraintMatchEnabled = constraintMatchEnabled;
    }

    // ************************************************************************
    // SimpleScore creation and assertion methods
    // ************************************************************************

    protected InnerScoreDirector<TestdataLavishSolution> buildScoreDirector(Consumer<Constraint> constraintConsumer) {
        ConstraintStreamScoreDirectorFactory<TestdataLavishSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataLavishSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            Constraint constraint = constraintFactory.newConstraintWithWeight(
                    "testConstraintPackage", "testConstraintName", SimpleScore.of(1));
            constraintConsumer.accept(constraint);
        });
        return scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);
    }

    protected void assertScore(InnerScoreDirector<TestdataLavishSolution> scoreDirector,
            AssertableMatch... assertableMatches) {
        SimpleScore score = (SimpleScore) scoreDirector.calculateScore();
        int scoreTotal = Arrays.stream(assertableMatches)
                .mapToInt(assertableMatch -> assertableMatch.score)
                .sum();
        assertEquals(scoreTotal, score.getScore());
        if (constraintMatchEnabled) {
            ConstraintMatchTotal constraintMatchTotal = scoreDirector.getConstraintMatchTotalMap()
                    .get(ConstraintMatchTotal.composeConstraintId("testConstraintPackage", "testConstraintName"));
            assertEquals(assertableMatches.length, constraintMatchTotal.getConstraintMatchCount());
            for (AssertableMatch assertableMatch : assertableMatches) {
                if (constraintMatchTotal.getConstraintMatchSet().stream()
                        .noneMatch(constraintMatch
                                -> constraintMatch.getJustificationList().equals(assertableMatch.justificationList)
                                && ((SimpleScore) constraintMatch.getScore()).getScore() == assertableMatch.score)) {
                    fail("The assertableMatch (" + assertableMatch + ") does not exist in the constraintMatchSet ("
                            + constraintMatchTotal.getConstraintMatchSet() + ").");
                }
            }

        }
    }

    protected static AssertableMatch assertMatch(Object... justifications) {
        return new AssertableMatch(-1, justifications);
    }

    protected static AssertableMatch assertMatchWithScore(int score, Object... justifications) {
        return new AssertableMatch(score, justifications);
    }

    protected static class AssertableMatch {

        private int score;
        private List<Object> justificationList;

        public AssertableMatch(int score, Object... justifications) {
            this.justificationList = Arrays.asList(justifications);
            this.score = score;
        }

        @Override
        public String toString() {
            return justificationList + "=" + score;
        }

    }

}
