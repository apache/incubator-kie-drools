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

package org.optaplanner.core.api.score.stream.uni;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.AbstractConstraintStreamTest;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

import static org.junit.Assert.*;

public class UniConstraintStreamTest extends AbstractConstraintStreamTest {

    public UniConstraintStreamTest(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Test
    public void filter() {
        InnerScoreDirector<TestdataSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataValue.class)
                    .filter(value -> value.getCode().endsWith("1"))
                    .penalize();
        });

        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v11 = new TestdataValue("v11");
        solution.setValueList(Arrays.asList(v1, v2, v11));
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e11 = new TestdataEntity("e11");
        solution.setEntityList(Arrays.asList(e1, e2, e11));
        scoreDirector.setWorkingSolution(solution);

        assertScore(scoreDirector, new AssertableMatch(-1, v1), new AssertableMatch(-1, v11));
    }

    public InnerScoreDirector<TestdataSolution> buildScoreDirector(Consumer<Constraint> constraintConsumer) {
        ConstraintStreamScoreDirectorFactory<TestdataSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                        TestdataSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            Constraint constraint = constraintFactory.newConstraintWithWeight(
                    "testConstraintPackage", "testConstraintName", SimpleScore.of(1));
            constraintConsumer.accept(constraint);
        });
        return scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);
    }

    public void assertScore(InnerScoreDirector<TestdataSolution> scoreDirector,
            AssertableMatch... assertableMatches) {
        SimpleScore score = (SimpleScore) scoreDirector.calculateScore();
        int scoreTotal = Arrays.stream(assertableMatches).mapToInt(assertableMatch -> assertableMatch.score).sum();
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
                    fail("The assertableMatch (" +  assertableMatch + ") does not exist in the constraintMatchSet ("
                            + constraintMatchTotal.getConstraintMatchSet() + ").");
                }
            }

        }
    }

    private static class AssertableMatch {

        private List<Object> justificationList;
        private int score;

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
