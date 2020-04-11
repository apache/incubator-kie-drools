/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.test.api.score.stream;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.stream.common.AbstractConstraint;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;

public final class SingleConstraintAssertion<Solution_> extends AbstractAssertion<Solution_,
        SingleConstraintAssertion<Solution_>, SingleConstraintVerifier<Solution_>> {

    private final Map<String, ConstraintMatchTotal> constraintMatchTotalMap;

    protected SingleConstraintAssertion(SingleConstraintVerifier<Solution_> singleConstraintVerifier,
            Map<String, ConstraintMatchTotal> constraintMatchTotalMap) {
        super(singleConstraintVerifier);
        this.constraintMatchTotalMap = Collections.unmodifiableMap(constraintMatchTotalMap);
    }

    private Number getImpact() {
        ScoreDefinition scoreDefinition = getParentConstraintVerifier().getConstraintStreamScoreDirectorFactory()
                .getScoreDefinition();
        Score zeroScore = scoreDefinition.getZeroScore();
        Number zero = zeroScore.toLevelNumbers()[0]; // Zero in the exact numeric type expected by the caller.
        if (constraintMatchTotalMap.isEmpty()) {
            return zero;
        }
        // We do not know the matchWeight, so we need to deduce it.
        // Constraint matches give us a score, whose levels are in the form of (matchWeight * constraintWeight).
        // Here, we strip the constraintWeight.
        Score totalMatchWeightedScore = constraintMatchTotalMap.values().stream()
                .map(matchScore -> scoreDefinition.divideBySanitizedDivisor(matchScore.getScore(),
                        matchScore.getConstraintWeight()))
                .reduce(zeroScore, Score::add);
        // Each level of the resulting score now has to be the same number, the matchWeight.
        // Except for where the number is zero.
        List<Number> matchWeightsFound = Arrays.stream(totalMatchWeightedScore.toLevelNumbers())
                .distinct()
                .filter(matchWeight -> !Objects.equals(matchWeight, zero))
                .collect(Collectors.toList());
        if (matchWeightsFound.isEmpty()) {
            return 0;
        } else if (matchWeightsFound.size() != 1) {
            throw new IllegalStateException("Impossible state: expecting at most one match weight," +
                    " but got matchWeightsFound (" + matchWeightsFound + ") instead.");
        }
        return matchWeightsFound.get(0);
    }

    private static void assertCorrectMatchWeight(Number matchWeightTotal) {
        if (matchWeightTotal.doubleValue() <= 0) {
            throw new IllegalArgumentException("Expected a positive match weight, given (" + matchWeightTotal + ").");
        }
    }

    private void assertImpact(ScoreImpactType scoreImpactType, Number matchWeightTotal, String message) {
        Number impact = getImpact();
        AbstractConstraint<?, ?> constraint = (AbstractConstraint<?, ?>) getParentConstraintVerifier()
                .getConstraintStreamScoreDirectorFactory()
                .getConstraints()[0];
        // Null means we're just looking for any kind of penalty or an impact.
        boolean isCorrectImpactType = scoreImpactType == null || scoreImpactType == constraint.getScoreImpactType();
        if (isCorrectImpactType && matchWeightTotal.equals(impact)) {
            return;
        }
        String constraintId = constraint.getConstraintId();
        String assertionMessage = buildAssertionErrorMessage(scoreImpactType, matchWeightTotal,
                constraint.getScoreImpactType(), impact, constraintId, message);
        throw new AssertionError(assertionMessage);
    }

    private static String buildAssertionErrorMessage(ScoreImpactType expectedImpactType, Number expectedImpact,
            ScoreImpactType actualImpactType, Number actualImpact, String constraintId, String message) {
        boolean hasMessage = message != null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream printStream = new PrintStream(baos)) {
            String expectation = hasMessage ? message : "Broken expectation.";
            String preformattedMessage = "%s%n" +
                    "%18s: %s%n" +
                    "%18s: %s (%s)%n" +
                    "%18s: %s (%s)";
            String expectedImpactLabel = "Expected " + getImpactTypeLabel(expectedImpactType);
            String actualImpactLabel = "Actual " + getImpactTypeLabel(actualImpactType);
            printStream.printf(preformattedMessage,
                    expectation,
                    "Constraint", constraintId,
                    expectedImpactLabel, expectedImpact, expectedImpact.getClass(),
                    actualImpactLabel, actualImpact, actualImpact.getClass());
            return baos.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed assembling asserting message.", e);
        }
    }

    private static String getImpactTypeLabel(ScoreImpactType scoreImpactType) {
        if (scoreImpactType == ScoreImpactType.PENALTY) {
            return "penalty";
        } else if (scoreImpactType == ScoreImpactType.REWARD) {
            return "reward";
        } else { // Needs to work with null.
            return "impact";
        }
    }

    /**
     * Asserts that the {@link Constraint} under test, given a set of facts, results in a specific penalty.
     *
     * @param matchWeightTotal sum of weights of constraint matches from applying the given facts to the constraint
     * @param message optional description of the scenario being asserted
     * @throws AssertionError when the expected penalty is not observed
     */
    public void penalizesBy(int matchWeightTotal, String message) {
        assertCorrectMatchWeight(matchWeightTotal);
        assertImpact(ScoreImpactType.PENALTY, matchWeightTotal, message);
    }

    /**
     * As defined by {@link #penalizesBy(int, String)}.
     */
    public void penalizesBy(long matchWeightTotal, String message) {
        assertCorrectMatchWeight(matchWeightTotal);
        assertImpact(ScoreImpactType.PENALTY, matchWeightTotal, message);
    }

    /**
     * As defined by {@link #penalizesBy(int, String)}.
     */
    public void penalizesBy(BigDecimal matchWeightTotal, String message) {
        assertCorrectMatchWeight(matchWeightTotal);
        assertImpact(ScoreImpactType.PENALTY, matchWeightTotal, message);
    }

    /**
     * As defined by {@link #penalizesBy(int, String)} with a null message.
     */
    public void penalizesBy(int matchWeightTotal) {
        penalizesBy(matchWeightTotal, null);
    }

    /**
     * As defined by {@link #penalizesBy(int, String)} with a null message.
     */
    public void penalizesBy(long matchWeightTotal) {
        penalizesBy(matchWeightTotal, null);
    }

    /**
     * As defined by {@link #penalizesBy(int, String)} with a null message.
     */
    public void penalizesBy(BigDecimal matchWeightTotal) {
        penalizesBy(matchWeightTotal, null);
    }

    /**
     * Asserts that the {@link Constraint} under test, given a set of facts, results in a specific reward.
     *
     * @param matchWeightTotal sum of weights of constraint matches from applying the given facts to the constraint
     * @param message optional description of the scenario being asserted
     * @throws AssertionError when the expected reward is not observed
     */
    public void rewardsWith(int matchWeightTotal, String message) {
        assertCorrectMatchWeight(matchWeightTotal);
        assertImpact(ScoreImpactType.REWARD, matchWeightTotal, message);
    }

    /**
     * As defined by {@link #rewardsWith(int, String)}.
     */
    public void rewardsWith(long matchWeightTotal, String message) {
        assertCorrectMatchWeight(matchWeightTotal);
        assertImpact(ScoreImpactType.REWARD, matchWeightTotal, message);
    }

    /**
     * As defined by {@link #rewardsWith(int, String)}.
     */
    public void rewardsWith(BigDecimal matchWeightTotal, String message) {
        assertCorrectMatchWeight(matchWeightTotal);
        assertImpact(ScoreImpactType.REWARD, matchWeightTotal, message);
    }

    /**
     * As defined by {@link #rewardsWith(int, String)} with a null message.
     */
    public void rewardsWith(int matchWeightTotal) {
        rewardsWith(matchWeightTotal, null);
    }

    /**
     * As defined by {@link #rewardsWith(int, String)} with a null message.
     */
    public void rewardsWith(long matchWeightTotal) {
        rewardsWith(matchWeightTotal, null);
    }

    /**
     * As defined by {@link #rewardsWith(int, String)} with a null message.
     */
    public void rewardsWith(BigDecimal matchWeightTotal) {
        rewardsWith(matchWeightTotal, null);
    }

    /**
     * Asserts that the {@link Constraint} under test, given a set of facts, results in neither penalty nor reward.
     *
     * @param message optional description of the scenario being asserted
     * @throws AssertionError when either a penalty or a reward is observed
     */
    public void hasNoImpact(String message) {
        assertImpact(null, 0, message);
    }

    /**
     * As defined by {@link #hasNoImpact(String)} with a null message.
     */
    public void hasNoImpact() {
        hasNoImpact(null);
    }

}
