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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Assume;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsLogicalTuple;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public abstract class AbstractConstraintStreamTest {

    protected static final String TEST_CONSTRAINT_NAME = "testConstraintName";

    @Parameterized.Parameters(name = "constraintMatchEnabled={0}, constraintStreamImplType={1}")
    public static Object[][] data() {
        return new Object[][]{
                {false, ConstraintStreamImplType.BAVET},
                {true, ConstraintStreamImplType.BAVET},
                {false, ConstraintStreamImplType.DROOLS},
                {true, ConstraintStreamImplType.DROOLS}
        };
    }

    protected final boolean constraintMatchEnabled;
    protected final ConstraintStreamImplType constraintStreamImplType;

    protected void assumeBavet() {
        Assume.assumeTrue("This functionality is not yet supported in Drools-based constraint streams.",
                constraintStreamImplType == ConstraintStreamImplType.BAVET);
    }

    protected void assumeDrools() {
        Assume.assumeTrue("This functionality is not yet supported in Bavet constraint streams.",
                constraintStreamImplType == ConstraintStreamImplType.DROOLS);
    }

    public AbstractConstraintStreamTest(boolean constraintMatchEnabled, ConstraintStreamImplType constraintStreamImplType) {
        this.constraintMatchEnabled = constraintMatchEnabled;
        this.constraintStreamImplType = constraintStreamImplType;
    }

    // ************************************************************************
    // SimpleScore creation and assertion methods
    // ************************************************************************

    protected InnerScoreDirector<TestdataLavishSolution> buildScoreDirector(Function<ConstraintFactory, Constraint> function) {
        ConstraintStreamScoreDirectorFactory<TestdataLavishSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataLavishSolution.buildSolutionDescriptor(),
                (constraintFactory) -> new Constraint[] {function.apply(constraintFactory)},
                constraintStreamImplType);
        return scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);
    }

    private static List<Object> removeIndirection(DroolsLogicalTuple logicalTuple) {
        return IntStream.range(0, logicalTuple.getCardinality())
                .mapToObj(logicalTuple::getItem)
                .collect(Collectors.toList());
    }

    private static List<Object> removeIndirection(List<Object> justificationList) {
        return justificationList.stream()
                .flatMap(item -> {
                    if (item instanceof DroolsLogicalTuple) {
                        return removeIndirection((DroolsLogicalTuple) item).stream();
                    } else {
                        return Stream.of(item);
                    }
                }).collect(Collectors.toList());
    }

    private List<ConstraintMatch> removeIndirections(ConstraintMatchTotal constraintMatchTotal) {
        if (constraintStreamImplType != ConstraintStreamImplType.DROOLS) {
            return new ArrayList<>(constraintMatchTotal.getConstraintMatchSet());
        }
        return constraintMatchTotal.getConstraintMatchSet().stream()
                .map(constraintMatch -> new ConstraintMatch(constraintMatch.getConstraintPackage(),
                        constraintMatch.getConstraintName(), removeIndirection(constraintMatch.getJustificationList()),
                        constraintMatch.getScore()))
                .collect(Collectors.toList());
    }

    protected void assertScore(InnerScoreDirector<TestdataLavishSolution> scoreDirector,
            AssertableMatch... assertableMatches) {
        scoreDirector.triggerVariableListeners();
        SimpleScore score = (SimpleScore) scoreDirector.calculateScore();
        int scoreTotal = Arrays.stream(assertableMatches)
                .mapToInt(assertableMatch -> assertableMatch.score)
                .sum();
        if (constraintMatchEnabled) {
            String constraintPackage = scoreDirector.getSolutionDescriptor().getSolutionClass().getPackage().getName();
            for (AssertableMatch assertableMatch : assertableMatches) {
                ConstraintMatchTotal constraintMatchTotal = scoreDirector.getConstraintMatchTotalMap()
                        .get(ConstraintMatchTotal.composeConstraintId(constraintPackage, assertableMatch.constraintName));
                List<ConstraintMatch> withoutIndirection = removeIndirections(constraintMatchTotal);
                if (withoutIndirection.stream()
                        .noneMatch(constraintMatch -> assertableMatch.isEqualTo(constraintMatch))) {
                    fail("The assertableMatch (" + assertableMatch + ") is lacking,"
                            + " it's not in the constraintMatchSet ("
                            + withoutIndirection + ").");
                }
            }
            List<ConstraintMatch> withoutIndirection = scoreDirector.getConstraintMatchTotalMap().values()
                    .stream()
                    .flatMap(t -> removeIndirections(t).stream())
                    .collect(Collectors.toList());
            for (ConstraintMatch constraintMatch : withoutIndirection) {
                if (Arrays.stream(assertableMatches)
                        .filter(assertableMatch -> assertableMatch.constraintName.equals(constraintMatch.getConstraintName()))
                        .noneMatch(assertableMatch -> assertableMatch.isEqualTo(constraintMatch))) {
                    fail("The constraintMatch (" + constraintMatch + ") is in excess,"
                            + " it's not in the assertableMatches (" + Arrays.toString(assertableMatches) + ").");
                }
            }
        }
        assertEquals(scoreTotal, score.getScore());
    }

    protected static AssertableMatch assertMatch(Object... justifications) {
        return assertMatchWithScore(-1, justifications);
    }

    protected static AssertableMatch assertMatch(String constraintName, Object... justifications) {
        return assertMatchWithScore(-1, constraintName, justifications);
    }

    protected static AssertableMatch assertMatchWithScore(int score, Object... justifications) {
        return assertMatchWithScore(score, TEST_CONSTRAINT_NAME, justifications);
    }

    protected static AssertableMatch assertMatchWithScore(int score, String constraintName, Object... justifications) {
        return new AssertableMatch(score, constraintName, justifications);
    }

    protected static class AssertableMatch {

        private final int score;
        private final String constraintName;
        private final List<Object> justificationList;

        public AssertableMatch(int score, String constraintName, Object... justifications) {
            this.justificationList = Arrays.asList(justifications);
            this.constraintName = constraintName;
            this.score = score;
        }

        public boolean isEqualTo(ConstraintMatch constraintMatch) {
            if (score != ((SimpleScore) constraintMatch.getScore()).getScore()) {
                return false;
            }
            if (!constraintName.equals(constraintMatch.getConstraintName())) {
                return false;
            }
            List<Object> actualJustificationList = constraintMatch.getJustificationList();
            // Can't simply compare the lists, since the elements may be in different orders. The order is not relevant.
            if (actualJustificationList.size() != justificationList.size()) {
                return false;
            }
            return justificationList.containsAll(actualJustificationList);
        }

        @Override
        public String toString() {
            return constraintName + " " + justificationList + "=" + score;
        }

    }

}
