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
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishEntity;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishEntityGroup;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishSolution;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishValueGroup;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;

import static org.junit.Assert.*;

public class UniConstraintStreamTest extends AbstractConstraintStreamTest {

    public UniConstraintStreamTest(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Test
    public void filter_problemFact() {
        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishValueGroup.class)
                    .filter(valueGroup -> valueGroup.getCode().startsWith("MyValueGroup"))
                    .penalize();
        });

        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();
        TestdataLavishValueGroup valueGroup1 = new TestdataLavishValueGroup("MyValueGroup 1");
        solution.getValueGroupList().add(valueGroup1);
        TestdataLavishValueGroup valueGroup2 = new TestdataLavishValueGroup("MyValueGroup 2");
        solution.getValueGroupList().add(valueGroup2);

        assertScore(scoreDirector, solution,
                new AssertableMatch(valueGroup1),
                new AssertableMatch(valueGroup2));
    }

    @Test
    public void filter_entity() {
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .filter(entity -> entity.getEntityGroup() == entityGroup)
                    .penalize();
        });

        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity2);

        assertScore(scoreDirector, solution,
                new AssertableMatch(entity1),
                new AssertableMatch(entity2));
    }

    public InnerScoreDirector<TestdataLavishSolution> buildScoreDirector(Consumer<Constraint> constraintConsumer) {
        ConstraintStreamScoreDirectorFactory<TestdataLavishSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataLavishSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            Constraint constraint = constraintFactory.newConstraintWithWeight(
                    "testConstraintPackage", "testConstraintName", SimpleScore.of(1));
            constraintConsumer.accept(constraint);
        });
        return scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);
    }

    public void assertScore(InnerScoreDirector<TestdataLavishSolution> scoreDirector, TestdataLavishSolution solution,
            AssertableMatch... assertableMatches) {
        scoreDirector.setWorkingSolution(solution);
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

        public AssertableMatch(Object... justifications) {
            this(-1, justifications);
        }

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
